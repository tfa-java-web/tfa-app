package tfa.tickets.face;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tfa.tickets.core.Configuration;

/**
 * My web application initializer
 */
@WebListener
public class FaceInitialize implements ServletContextListener, ServletRequestListener
{
  // Standard SLF4J logger
  private static Logger log = LoggerFactory.getLogger(FaceInitialize.class);

  public FaceInitialize()
  {
    // Created by web server
    super();       
  }

  // ---------------------------------------------------- starting

  @Override
  public void contextInitialized(ServletContextEvent sce)
  {
    log.info("contextInitialized start");
           
    // Context params
    ServletContext sc = sce.getServletContext();

    // JSF adaptations

    if (Configuration.isDeveloppement())
      sc.setInitParameter("javax.faces.PROJECT_STAGE", "Development");

    if (Configuration.isProduction())
      sc.setInitParameter("javax.faces.FACELETS_SKIP_COMMENTS", "true");

    sc.setInitParameter("javax.faces.STATE_SAVING_METHOD", "server");
    sc.setInitParameter("javax.faces.DEFAULT_SUFFIX", ".xhtml");
   
    log.info("contextInitialized end");
  }

  @Override
  public void requestInitialized(ServletRequestEvent sre)
  {
  }

  // ------------------------------------------------------- ending

  @Override
  public void requestDestroyed(ServletRequestEvent sre)
  {
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce)
  {
    // Save hit count at end of application
    HitCountBean.end();

    log.info("contextDestroyed");
  }
  
}
