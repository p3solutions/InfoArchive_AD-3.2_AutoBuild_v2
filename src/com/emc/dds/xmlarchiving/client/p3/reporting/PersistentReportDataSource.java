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
import com.emc.dds.xmlarchiving.client.ui.LDMUIHandler;
import com.emc.documentum.xml.dds.gwt.client.rpc.DDSServices;
import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableXQueryValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Malik
 *
 */
public class PersistentReportDataSource extends StoredQueryDataSource {
    
    private static final String QUEUE = "/DATA/changeme/Reports/Queue";
    
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
            AsyncCallback<?> failureHandler) {
        super(rawXQuery, fields, searchSetting, userName, restrictions, failureHandler);
        
        final String defaultEmail = searchCfg.getPersistentReportEmail();
        final String reportOutputType = searchCfg.getReportOutputType();
        
        for (String x : fields.keySet()){
        	System.out.println(x + " " + fields.get(x));
        }
        
        Iterator<Entry<String, String>> it = fields.entrySet().iterator();
        StringBuilder searchFields = new StringBuilder();
        while (it.hasNext()){
        	searchFields.append("\n").append(it.next());
        }  
        
        String decoratedXQuery = getXQuery(null);
        
        String emailFromForms = fields.get("email");
        String email = (emailFromForms == null || emailFromForms.length() == 0) ? defaultEmail : emailFromForms;
        
        Date date = new Date();
        long milli = date.getTime();
        String dateTime = date.toString();
        String ticketName = createTicketName(userName, milli);
        String reportName = searchSetting.getName();
        String ticketContent = createContent(reportName, reportOutputType, userName, date, email, decoratedXQuery, searchFields);
        String fullTicketXQuery = createTicketGenerationXQuery(ticketName, ticketContent);
        
        registerTicket(ticketName, fullTicketXQuery);
        
        String userMessage = "Report ticket created " + dateTime;
        setXQuery(createUserXQuery(userMessage));
    }

    private String createUserXQuery(String message) {
        message = CharacterUtil.escapeXMLAttribute(message);
        String query = 
                "<results total=\"1\" searchResultItems=\"message\" nestedSearches=\"\"> " +
                "<result message=\"" + message + "\"/>" +
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
    
    private String createContent(String reportName, String reportOutputType, String userName, Date date, 
            String email, String decoratedXQuery, StringBuilder searchFields)  {
        
        reportName = (reportName == null) ? "" : reportName;
        userName = (userName == null) ? "" : userName;
        String dateTime = date.toString();
        long milliseconds = date.getTime();
        email = (email == null) ? "" : email;
        decoratedXQuery = (decoratedXQuery == null) ? "" : decoratedXQuery;
        return
            "<report>\r\n" + 
            "  <name>" + reportName + "</name>\n" + 
            "  <username>" + userName + "</username>\n" + 
            "  <datetime>" + dateTime + "</datetime>\n" + 
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
    
}
