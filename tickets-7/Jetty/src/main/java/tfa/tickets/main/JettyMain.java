
package tfa.tickets.main;

import java.io.File;
import java.security.ProtectionDomain;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * A main class to launch embedded jetty server as debug java application from eclipse (run as java application ) as executable war from command line ( java
 * -jar tickets.war )
 */
public final class JettyMain
{
    // The Jetty instance
    private Server server;

    // Thread pool param
    private final int maxTread = 10;
    private final int minTread = 2;

    // Http Connector param
    private final String host = "localhost";
    private final int port = 8080;

    // Signal SIGTERM raised  
    private static boolean stop = false;
    
    // Entry point to put into manifest
    public static void main(String[] args)
    {
        new JettyMain().run();
    }

    private void run()
    {
        // JettyMain directory or war filepath.
        ProtectionDomain protectionDomain = this.getClass().getProtectionDomain();
        String webappDir = protectionDomain.getCodeSource().getLocation().toExternalForm();

        // for dev&debug only : adjust path of webapp from default to real path
        webappDir = webappDir.replace("Jetty/target/classes/", "Gui/src/main/webapp/");

        // Thread pool to handle connections (max, min, timeout ms) ...
        QueuedThreadPool connectionThreadPool = new QueuedThreadPool(maxTread, minTread, 30 * 1000);

        // The server ...
        server = new Server(connectionThreadPool);
        server.addBean(new ScheduledExecutorScheduler());

        // HTTP Connector
        ServerConnector httpConnector = new ServerConnector(server);
        httpConnector.setHost(host);
        httpConnector.setPort(port);
        server.addConnector(httpConnector);

        // Well server stop
        server.setStopAtShutdown(true);

        // WebApp Context Handler
        WebAppContext webappContext = new WebAppContext();

        // Webapp directory
        webappContext.setWar(webappDir);
        webappContext.setServer(server);

        // Location to uncompress war, and speed up next start without change (see also jetty quick starter!)
        webappContext.setTempDirectory(new File("./work"));
        webappContext.setPersistTempDirectory(true);
        
        // To avoid double logback class warning
        webappContext.setParentLoaderPriority(true);
        
        // The context path
        webappContext.setContextPath("/tickets");

        // Add server reference to context ...
        webappContext.setAttribute("JETTY_MAIN", this);
        webappContext.setDisplayName("Tickets application (by tfa)");

        // Enable parsing of Xml conf, Annotations & Jndi
        Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");
        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                           "org.eclipse.jetty.plus.webapp.PlusConfiguration");

        // Scan all for annotations (can be optimized) (see also jetty quick starter!)
        // webappContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*\\.jar$|.*/target/classes/.*");
        webappContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/jsf-[^/]*/.jar$|.*/el-[^/]*/.jar$|.*/jstl-[^/]*/.jar$|.*/tickets-[^/]*/.jar$|.*/target/classes/.*");

        // Start JSF 2.2 : This is automatic by com.sun.faces.config.FacesInitializer

        // Setup JMX : no need for now
        // MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        // server.addEventListener(mbContainer);
        // server.addBean(mbContainer);
        // server.addBean(Log.getLog());

        server.setHandler(webappContext);
        
        try
        {
            // Start server ...
            server.start();
            
            // Signal SIGTERM capture
            Runtime.getRuntime().addShutdownHook(new Thread() 
            {
                @Override public void run() { stop = true; }
            });   
            
            // Scan stop on stdin (console) ...
            String cmd;
            byte[] buf = new byte[256];
            do
            {
                if ( stop ) break;
                
                // Wait command wih cr
                while (System.in.available() <= 0)
                    Thread.sleep(1000);

                if ( stop ) break;

                // Read command
                System.in.read(buf, 0, 255);
                cmd = new String(buf).replace("\r", "");

                // exit command
                if (cmd.startsWith("exit\n"))
                    System.exit(-1);

                // gc command
                if (cmd.startsWith("gc\n"))
                    System.gc();
            }
            // stop command
            while (!cmd.startsWith("stop\n"));

            // Stop the server
            server.stop();

            // Wait stopped
            server.join();
        }
        catch (Exception e)
        {
            // Unexpected exception
            System.err.println("Unable to start or stop server: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
