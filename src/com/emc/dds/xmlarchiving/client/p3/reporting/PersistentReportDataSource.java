package com.emc.dds.xmlarchiving.client.p3.reporting;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.emc.dds.xmlarchiving.client.configuration.SearchConfiguration;
import com.emc.dds.xmlarchiving.client.configuration.SearchSetting;
import com.emc.dds.xmlarchiving.client.data.StoredQueryDataSource;
import com.emc.dds.xmlarchiving.client.p3.util.CharacterUtil;
import com.emc.dds.xmlarchiving.client.p3.util.UUID;
import com.emc.dds.xmlarchiving.client.ui.LDMUIHandler;
import com.emc.documentum.xml.dds.gwt.client.rpc.DDSServices;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableXQueryValue;
import com.emc.documentum.xml.gwt.client.Dialog;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Malik
 *
 */
public class PersistentReportDataSource extends StoredQueryDataSource {
    
	private static final String TICKET_PREFIX = "RT";
	private static final String APP = "changeme";
    private static final String QUEUE = "/DATA/" + APP + "/Reports/Queue";
    private static final String QUEUE_PT = "/DATA/" + APP + "/Reports_PT/Queue";
    private static final String[] queue_ptList = {"Product Trace Report"};
	private static final String DECRYPT_CODE = "decrypt";
	private static final String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    /**
     * During construction of the parent StoredQueryDataSource, the raw XQuery 
     * is decorated by various code injection mechanisms.  The decorated XQuery 
     * is then saved to xDB as a report ticket, and the superclass StoredQueryDataSource 
     * XQuery is replaced with a simple query to inform the user of the ticket.
     * 
     * @param rawXQuery
     * @param fields
     * @param searchSetting
     * @param userName
     * @param restrictions
     * @param defaultEmail
     */
    public PersistentReportDataSource(String rawXQuery, Map<String, String> fields, 
            SearchSetting searchSetting, String userName, String restrictions, SearchConfiguration searchCfg,
            AsyncCallback<?> failureHandler, byte[] key, String encType, String role) {
        super(rawXQuery, fields, searchSetting, userName, restrictions, failureHandler, key, encType);
        
        final String defaultEmail = searchCfg.getPersistentReportEmail();
        final String reportOutputType = searchCfg.getReportOutputType();
  
        boolean spaceCheckFlag = true;
        Iterator<Entry<String, String>> it = fields.entrySet().iterator();
        StringBuilder searchFields = new StringBuilder("");
        while (it.hasNext()){
        	Entry<String, String> x = it.next();
        	if(searchFields.toString().equals(""))
        		searchFields.append("\n").append(x);
        	else
        		searchFields.append(";\n").append(x);
        	
        	if(spaceCheckFlag){
        		String y = x.getValue();
        		if(y.length() != 0){
        			if(y.trim().length() == 0)
        				spaceCheckFlag = false;
        		}
        	}
        	fields.put(x.getKey(),x.getValue().trim());	
        
        
        if(!spaceCheckFlag){
        	Dialog.alert("One/more fields has only spaces in the input text. Please recheck");
        	setXQuery(createUserXQuery("One/more fields has only spaces in the input text. Please recheck"));
        	return;
        }}
        
        String decoratedXQuery = getXQuery(null);
        String emailFromForms = fields.get("email");
        String email = (emailFromForms == null || emailFromForms.length() == 0) ? defaultEmail : emailFromForms;
        
        if(!email.matches(ePattern)){
        	Dialog.alert("Email is invalid. Please check");
        	setXQuery(createUserXQuery("Email is invalid"));
        	return;
        }
        
        Date date = new Date();
        long milli = date.getTime();
        String dateTime = date.toString();
        String ticketName = createTicketName(userName, milli);
        String reportName = searchSetting.getName();
        String id =  TICKET_PREFIX + UUID.uuid(10, 10);
        
        boolean flag = true;
        for (int i = 0; i < queue_ptList.length; i++) {
			if(flag && searchSetting.getName().equals(queue_ptList[i])){
				flag = false;
				break;
			}
		}
        
        String ticketContent = flag?createContent(reportName, reportOutputType, userName, date, email, decoratedXQuery, searchFields, id, restrictions, role):createContent_PT(reportName, reportOutputType, userName, date, email, decoratedXQuery, fields, id, restrictions, role);
        String fullTicketXQuery = flag?createTicketGenerationXQuery(ticketName, ticketContent):createTicketGenerationXQuery_PT(ticketName, ticketContent);     
        registerTicket(ticketName, fullTicketXQuery);
        String userMessage = "Report ticket \""+id+"\" created at " + getDateTimeLocalFormat(date);
        setXQuery(createUserXQuery(userMessage));
    }

    private String createUserXQuery(String message) {
        message = CharacterUtil.escapeXMLAttribute(message);
        String query = 
                "<results total=\"1\" searchResultItems=\"message\" nestedSearches=\"\"> " +
                "<result message=\"" + message + "\" type=\"emptyScreen\"/>" +
        		"</results>";
        return query;
    }
    
    /**
     * Has side-effect of starting the registration of the report ticket with xDB.
     * 
     * @param UserName
     * @return The ticket message for the user, which is the ticket ID.
     */
    private void registerTicket(String ticketName, String fullTicketXQuery) {
        DDSServices.getXQueryService().execute(null, fullTicketXQuery, false, 
                new AsyncCallback<List<SerializableXQueryValue>>() {
            @Override
            public void onSuccess(List<SerializableXQueryValue> result) {
                // Good!  Queue does not fetch information so no need to use result
            }
            @Override
            public void onFailure(Throwable t) {
                LDMUIHandler.displayFriendlyException(t, "Unable to create report ticket");
            }
        });
    }
    
    private static String createTicketGenerationXQuery(String ticketName, String ticketContent) {
        return 
            "let $doc as document-node() := document {\n" + 
            ticketContent + 
            "}\n" + 
            "return xhive:insert-document('" + QUEUE + "/" + ticketName + "', $doc)";
    }
    
    private static String createTicketGenerationXQuery_PT(String ticketName, String ticketContent) {
        return 
            "let $doc as document-node() := document {\n" + 
            ticketContent + 
            "}\n" + 
            "return xhive:insert-document('" + QUEUE_PT + "/" + ticketName + "', $doc)";
    }
    
//    private String createTicketName(String UserName, String dateTime) {
//        StringBuilder id = new StringBuilder();
//        id.append("Ticket_");
//        id.append(UserName);
//        id.append('_');
//        id.append(dateTime);
//        return id.toString().replaceAll("\\s+", "_");
//    }
    
    private String createTicketName(String UserName, long milliseconds) {
        StringBuilder id = new StringBuilder();
        id.append("Ticket_");
        id.append(UserName);
        id.append('_');
        id.append(milliseconds);
        id.append(".xml");
        return id.toString().replaceAll("\\s+", "_");
    }
    
    @SuppressWarnings("deprecation")
	private String createContent(String reportName, String reportOutputType, String userName, Date date, 
            String email, String decoratedXQuery, StringBuilder searchFields, String id, String restrictions, String role)  {
        
        reportName = (reportName == null) ? "" : reportName;
        userName = (userName == null) ? "" : userName;
        long milliseconds = date.getTime();
        email = (email == null) ? "" : email;
        decoratedXQuery = (decoratedXQuery == null) ? "" : decoratedXQuery;
        
        int month = (date.getMonth() + 1);
        int year = (date.getYear() + 1900);
        int day = date.getDate();
        
        int hour = date.getHours();
        int min = date.getMinutes();
        int sec = date.getSeconds()>59?59:date.getSeconds();
        
        String dateTime = getDateTimeLocalFormat(date);
        
        String decrypt = restrictions.contains(DECRYPT_CODE)?"false":"true";
        return
            "<report>\r\n" + 
            "  <id>" + id + "</id>\n" + 
            "  <name>" + reportName + "</name>\n" +
            "  <rolename>" +  role + "</rolename>\n" + 
            "  <decrypt>" +  decrypt + "</decrypt>\n" + 
            "  <username>" + userName + "</username>\n" + 
            "  <datetime>" + dateTime + "</datetime>\n" + 
            "  <CCYYMMDD>" +  year + (month<10?"0":"") + month + (day<10?"0":"") + day + "</CCYYMMDD>\n" + 
            "  <offset>" + date.getTimezoneOffset() + "</offset>\n" +
            "  <epoch1970>" + milliseconds + "</epoch1970>\n" + 
            "  <email>" + email + "</email>\n" + 
            "  <type>" + reportOutputType + "</type>\n" + 
            "  <searchField>" + searchFields + "</searchField>\n" +
            "  <xquery>\n" + 
            "<![CDATA[" + 
            decoratedXQuery + 
            "]]>\n" + 
            "</xquery>\n" + 
            "</report>\n";
    }

    @SuppressWarnings("deprecation")
	private String createContent_PT(String reportName, String reportOutputType, String userName, Date date, 
            String email, String decoratedXQuery, Map<String, String> fields, String id, String restrictions, String role)  {
        
        reportName = (reportName == null) ? "" : reportName;
        userName = (userName == null) ? "" : userName;
        long milliseconds = date.getTime();
        email = (email == null) ? "" : email;
        decoratedXQuery = (decoratedXQuery == null) ? "" : decoratedXQuery;
        
        int month = (date.getMonth() + 1);
        int year = (date.getYear() + 1900);
        int day = date.getDate();
        
        int hour = date.getHours();
        int min = date.getMinutes();
        int sec = date.getSeconds()>59?59:date.getSeconds();
        
        String dateTime = getDateTimeLocalFormat(date);
        
        String decrypt = restrictions.contains(DECRYPT_CODE)?"false":"true";
        
        
        StringBuffer sb = new StringBuffer();
        StringBuilder searchFields = new StringBuilder("");
        
        for(String fieldkey : fields.keySet()){
        	if(fieldkey.equals("file") || fieldkey.equals("reportType") || fieldkey.equals("email")){
	        	String value = fields.get(fieldkey);
	        	sb.append("<INPUT_").append(fieldkey).append(">").append(value).append("</INPUT_").append(fieldkey).append(">");
	        	if(searchFields.toString().equals(""))
	        		searchFields.append("\n").append(fieldkey).append("=").append(value);
	        	else
	        		searchFields.append(";\n").append(fieldkey).append("=").append(value);
        	}
        }
        
        return
            "<report>\r\n" + 
            "  <id>" + id + "</id>\n" + 
            "  <name>" + reportName + "</name>\n" +
            "  <rolename>" +  role + "</rolename>\n" + 
            "  <decrypt>" +  decrypt + "</decrypt>\n" + 
            "  <username>" + userName + "</username>\n" + 
            "  <datetime>" + dateTime + "</datetime>\n" + 
            "  <CCYYMMDD>" +  year + (month<10?"0":"") + month + (day<10?"0":"") + day + "</CCYYMMDD>\n" + 
            "  <offset>" + date.getTimezoneOffset() + "</offset>\n" +
            "  <epoch1970>" + milliseconds + "</epoch1970>\n" + 
            "  <email>" + email + "</email>\n" +
            sb.toString() +
            "  <searchField>" + searchFields + "</searchField>\n" +
            "  <xquery>\n" + 
            "<![CDATA[" + 
            decoratedXQuery + 
            "]]>\n" + 
            "</xquery>\n" + 
            "</report>\n";
    }
    
    private String getDateTimeLocalFormat(Date date) {
		int month = (date.getMonth() + 1);
        int year = (date.getYear() + 1900);
        int day = date.getDate();
        
        int hour = date.getHours();
        int min = date.getMinutes();
        int sec = date.getSeconds()>59?59:date.getSeconds();
        
        String dateTime = day + " " + getMonth(month) + ", " + year + " " + getHour(hour) + ":" + (min<10?"0":"") + min  + ":" + (sec<10?"0":"") + sec + " " + ((hour < 12)?"AM":"PM");
        return dateTime;
	}

	private String getHour(int hour) {
		if(hour == 0 || hour ==12)
			return "12";
		else{
			int m_hour = hour%12;
			if(m_hour < 10)
				return "0"+m_hour;
			else
				return Integer.toString(m_hour);
		}
	}

	private String getMonth(int month) {
		switch(month){
		case 1:
			return "Jan";	
		case 2:
			return "Feb";	
		case 3:
			return "Mar";	
		case 4:
			return "Apr";	
		case 5:
			return "May";	
		case 6:
			return "Jun";	
		case 7:
			return "Jul";	
		case 8:
			return "Aug";	
		case 9:
			return "Sep";	
		case 10:
			return "Oct";	
		case 11:
			return "Nov";	
		case 12:
			return "Dec";
		default:
			return "";
		}
	}
    
}
