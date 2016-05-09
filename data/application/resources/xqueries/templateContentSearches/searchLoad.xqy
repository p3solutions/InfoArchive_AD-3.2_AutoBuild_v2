module namespace searchLoad = 'searchLoad';

import module namespace common 				= 'common' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/common.xqy';
import module namespace utility 			= 'utility' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/utility.xqy';
import module namespace dynamicQueryModule 	= 'dynamicQueryModule' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/dynamicQueryModule.xqy';

declare variable $searchLoad:adddate := 'P10D';
declare variable $searchLoad:subdate := '-P10D';
declare variable $searchLoad:path := '/DATA/changeme/Collection/LOAD/';

declare function searchLoad:start($table,$Fdate,$Tdate,$restrictions,$first,$last,$currentuser){
	let $x 			:= searchLoad:computeDate($Fdate,$Tdate)
	let $fd 		:= replace(replace($x/@fromDate,"-",""),"/","")
	let $td 		:= replace(replace($x/@toDate,"-",""),"/","")
	let $table 		:= upper-case($table)
	let $main-query	:= searchLoad:searchload($table,$fd,$td)
	return common:getQuerySubsequence($main-query, $first, $last)
};

declare function searchLoad:computeDate($f,$t){
	let $f := xs:string($f)
	let $t := xs:string($t)
	return
		if(($f != "" and $t != "") or ($f = "" and $t = ""))
		then <x fromDate='{$f}' toDate='{$t}'/>
		else if($f != "" and $t = "")
			 then <x fromDate='{$f}' toDate='{searchLoad:getNewDate($f,$searchLoad:adddate)}'/>
			 else <x fromDate='{searchLoad:getNewDate($t,$searchLoad:subdate)}' toDate='{$t}' />
};

declare function searchLoad:getNewDate($date,$correction){
	xs:string(searchLoad:getDate($date) + xs:dayTimeDuration($correction))
};

declare function searchLoad:getDate($date){
	let $year 	:= substring($date,1,4)
	let $month 	:= substring($date,6,2)
	let $day 	:= substring($date,9,2)
	return
		xs:date(
		 concat(
		   searchLoad:pad-integer-to-length(xs:integer($year),4),'-',
		   searchLoad:pad-integer-to-length(xs:integer($month),2),'-',
		   searchLoad:pad-integer-to-length(xs:integer($day),2)))
};

declare function searchLoad:pad-integer-to-length($integerToPad as xs:anyAtomicType? , $length as xs:integer )  as xs:string {
	if ($length < string-length(string($integerToPad)))
	then error(xs:QName('searchLoad:Integer_Longer_Than_Length'))
	else concat(searchLoad:repeat-string( '0',$length - string-length(string($integerToPad))), string($integerToPad))
};
 
declare function searchLoad:repeat-string( $stringToRepeat as xs:string? , $count as xs:integer )  as xs:string {
	string-join((for $i in 1 to $count return $stringToRepeat), '')
};
 
declare function searchLoad:searchload($table,$fd,$td){
	let $for 	:= concat(" for $i in doc('",$searchLoad:path,"')/LOAD/LOAD_ROW ")
	let $where 	:= dynamicQueryModule:queryWhereFramer("i", ($table,$fd,$td,"SUCCESS"),("@TABLE","DATE","DATE","@STATUS"),("=",">=","<=","="))
	let $return := " return <result table='{$i/@TABLE}' date='{$i/DATE}' count='{$i/ROW_COUNT}' /> "
	return searchLoad:orderData(xhive:evaluate(concat($for,$where,$return)))
};

declare function searchLoad:orderData($results){
	for $i in distinct-values($results/@date)
	return
		for $j in distinct-values($results[@date = $i]/@table)
		return
			<result
				table	= '{$j}'
				date 	= '{searchLoad:dateConvert($i)}'
				count 	= '{sum($results[@date = $i][@table = $j]/@count)}'
				type    = 'dataLoadModule'
				title   = 'Load Details'
			/>
};


declare function searchLoad:dateConvert($input){
	if($input = "")
	then ""
	else concat(substring($input, 5, 2), "/", substring($input, 7, 2), "/", substring($input, 1, 4))
};