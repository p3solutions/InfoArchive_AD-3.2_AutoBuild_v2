/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 */
package com.emc.documentum.xml.dds.servlet;

import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.application.exception.InitializationException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationStarter
implements ServletContextListener {
    public static final String USER_ID_ATTRIBUTE = "dds.user.id";

    public void contextDestroyed(ServletContextEvent event) {
        Application application = DDS.getApplication();
        application.stop();
    }

    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        try {
            this.initializeApplication(context.getInitParameter("dds.application.config.path"));
        }
        catch (InitializationException ie) {
            ie.printStackTrace();
            throw new RuntimeException(ie);
        }
    }

    protected void initializeApplication(String configurationPath) throws InitializationException {
        StringBuilder configXML;
        configXML = new StringBuilder();
        try {
            BufferedReader in = this.openBufferedReader(configurationPath);
            Throwable throwable = null;
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    configXML.append(line);
                }
            }
            catch (Throwable x2) {
                throwable = x2;
                throw x2;
            }
            finally {
                if (in != null) {
                    if (throwable != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable x2) {
                            throwable.addSuppressed(x2);
                        }
                    } else {
                        in.close();
                    }
                }
            }
        }
        catch (IOException ioe) {
            throw new InitializationException("Error reading application configuration file: ", ioe);
        }
        if (!DDS.createApplication(configXML.toString()).fullStartup()) {
            throw new InitializationException("The application could not be started. Please check whether the Main Store is accessible, and whether the application configuration is correct.");
        }
    }

    private BufferedReader openBufferedReader(String configurationPath) throws FileNotFoundException, IOException {
        BufferedReader in;
        if (configurationPath == null || "".equals(configurationPath.trim())) {
            URL url = Thread.currentThread().getContextClassLoader().getResource("application-bootstrap.xml");
            if (url == null) {
                FileInputStream fis = new FileInputStream("application-bootstrap.xml");
                in = new BufferedReader(new InputStreamReader((InputStream)fis, StandardCharsets.UTF_8));
            } else {
                in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            }
        } else {
            FileInputStream fis = new FileInputStream(configurationPath);
            in = new BufferedReader(new InputStreamReader((InputStream)fis, StandardCharsets.UTF_8));
        }
        return in;
    }
}

