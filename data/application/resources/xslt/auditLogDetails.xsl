<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    <!--  XHTML output with XML syntax --> 
    <xsl:output method="xml" encoding="utf-8" indent="no" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" /> 
    <xsl:output method="html" /> 
    <xsl:param name="data-base-uri" /> 
    <xsl:template match="/">
        <!--  always emit at least an empty HTML element --> 
        <html>
            <!--  and look at the rest of the file --> 
            <xsl:apply-templates select="results"/>
        </html>
    </xsl:template>
    <xsl:template match="results">
        <head>
            <title>Audit Log</title> 
            <style type="text/css">body { font-family : 'Lucida Sans Unicode', 'Lucida Grande', sans-serif; font-size : 12px; } .header { font-weight : bold; padding : 0px 30px 0px 10px; vertical-align : top; }</style>
            <script src="SpryAssets/SpryCollapsiblePanel.js" type="text/javascript"></script>
            <link href="SpryAssets/SpryCollapsiblePanel.css" rel="stylesheet" type="text/css" />
        </head>
        <body>
            <xsl:template match="*">
				<div id="CollapsiblePanel1" class="CollapsiblePanel">
					<div class="CollapsiblePanelTab" tabindex="0">Audit Log Access</div>
					<div class="CollapsiblePanelContent">
						<table>
							<tbody>
						<!-- dump out all the query parameters -->
								<tr><td>Time</td><td><xsl:value-of select="time"/></td></tr>
								<tr><td>User</td><td><xsl:value-of select="user"/></td></tr>
								<xsl:for-each select="data/*">
									<tr><td><xsl:value-of select="name(.)"/></td><td><xsl:value-of select="text()"/></td></tr>
								</xsl:for-each>
							</tbody>  
						</table>
					</div>
				</div>
			</xsl:template>
            <script type="text/javascript">
            var CollapsiblePanel1 = new Spry.Widget.CollapsiblePanel("CollapsiblePanel1");
            </script>
        </body>
    </xsl:template>
    
</xsl:stylesheet>