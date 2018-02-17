package com.personal.stanley;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class AngularJsWebApp {

    public static void main(String[] args) throws Exception {
        // The simple Jetty config here will serve static content from the webapp directory
        //String webappDirLocation = "/Users/stanleyopara/projects/Crypto_Watcher/crypto_watcher_webapp/src/main/webapp";
        String webappDirLocation = AngularJsWebApp.class.getClassLoader().getResource("webapp").toExternalForm();

        //System.out.println(webappDirLocation);
        // The port that we should run on can be set into an environment variable
        // Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv().get("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }
        Server server = new Server(Integer.valueOf(webPort));

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        webapp.setResourceBase(webappDirLocation);

        server.setHandler(webapp);
        server.start();
        server.join();
    }
}
