package tfa.tickets.rest.test;

import java.net.ConnectException;
import java.time.Instant;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import tfa.tickets.auth.Encryptation;
import tfa.tickets.base.test.DaoUnitTester;
import tfa.tickets.rest.IAuthRest;

/**
 * Base class to build RestEasy Client for rest api test
 */
public class RestApiTester
{
  // -------------------------------------------------- tests init

  // URL of rest service to test
  static final String ROOT_URL = "http://localhost:8080/tickets/rest";

  // Auth data for test
  private static final String USER= "admin";
  private static final String PASS= "admin";
  
  // Client of service
  static ResteasyClient client;
  static ResteasyWebTarget target;
  
  // Used to control server data set
  static String pu = "TicketsPU_IT";
  private DaoUnitTester base = new DaoUnitTester();
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception
  {
    // Init data set into server base for IT 
    DaoUnitTester.setPersistentUnit(pu);
    DaoUnitTester.setUpClass();
    
    // Create client of REST api
    client = new ResteasyClientBuilder().build();
    target = client.target(ROOT_URL);
    IAuthRest api = target.proxy(IAuthRest.class);

    // Call the rest status ( try during 4*(2+4) = 24 seconds )
    for (int nb = 0;;)
    {
      try
      {
        // generate token with password and timestamp as salt 
        String timestamp = Long.toString( Instant.now().getEpochSecond() );
        String token = Encryptation.encrypt( timestamp + ":" + PASS, null);

        // Try to connect and rest login
        Response r = api.login( null, USER + ":" + token );
        if ( r.getStatusInfo() != Status.OK ) throw new Exception("Bad status " + r.getStatusInfo().toString() );
        r.close();
               
        break;
      }
      catch (Exception e)
      {
        if ( !(e.getCause() instanceof ConnectException) || nb++ >= 4)
          Assert.fail("cannot connect to REST service");
        
        System.out.println("wait connection...");
        
        // Retry after 4 s
        Thread.sleep(4000);
      }
    }
       
  }
  
  @AfterClass
  public static void tearDownAfterClass() throws Exception 
  {
    // REST Logout
    IAuthRest api = target.proxy(IAuthRest.class);
    Response r = api.logout( null );
    r.close();

    // Close client
    client.close();

    // Close data set
    DaoUnitTester.tearDownClass();
  }
  
  @Before
  public void setUpBefore() throws Exception
  {
    // Re-Init base - data set
    base.setUpBefore();
  }

  @After
  public void tearDownAfter() throws Exception
  {    
    // Clean base - data set
    base.tearDownAfter();
  }

}
