<?xml version="1.0" encoding="UTF-8"?>

<p:pipeline name="main" xmlns:p="http://www.w3.org/ns/xproc"
            xmlns:c="http://www.w3.org/ns/xproc-step"
            xmlns:fo="http://www.w3.org/1999/XSL/Format"
            xmlns:dds="http://www.emc.com/documentum/xml/dds"
            xmlns:ext="http://flatironssolutions.com/decomm/xproc"
            version="1.0">

  <p:input port="query"/>
  <p:input port="stylesheet"/>

  <p:add-attribute name="add-attr" match="dds:expression"
    attribute-name="c:content-type" attribute-value="application/xquery">
    <p:input port="source" select="//dds:expression">
      <p:pipe step="main" port="query"/>
    </p:input>
  </p:add-attribute>

  <p:xquery>
    <p:input port="query">
      <p:pipe step="add-attr" port="result"/>
    </p:input>
  </p:xquery>

  <ext:rtf2html stdout="true" stderr="true" convertType="text" imgreftype="relative" imgroot="C:\tomcat 6.0\webapps\changeme\changeme"/>

  <p:xslt>
    <p:input port="stylesheet">
      <p:pipe step="main" port="stylesheet"/>
    </p:input>
    <p:input port="parameters">
      <p:empty/>
    </p:input>
  </p:xslt>

  <p:make-absolute-uris match="fo:external-graphic/@src"/>

  <p:viewport match="fo:external-graphic">
    <p:string-replace match="/fo:external-graphic/@src" replace="concat('url(&quot;', ., '&quot;)')"/>
  </p:viewport>

  <p:unwrap match="dds:wrapper"/>

  <!-- FOP does not like tables where the declared number of table columns
       does not match the actual number of row cells in the table -->
  <p:delete match="fo:table-column"/>

  <p:xsl-formatter name="formatter" content-type="application/pdf" href="transient:output.pdf"/>

  <p:identity>
    <p:input port="source">
      <p:pipe step="formatter" port="result"/>
    </p:input>
  </p:identity>

</p:pipeline>
