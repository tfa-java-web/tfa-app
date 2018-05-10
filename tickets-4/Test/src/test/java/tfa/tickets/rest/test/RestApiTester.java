package tfa.tickets.rest.test;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import tfa.tickets.base.test.DaoUnitTester;
import tfa.tickets.rest.IHealthRest;

/**
 * Base class to build RestEasy Client for rest api test
 */
public class RestApiTester
{
  // -------------------------------------------------- tests init

  // URL of rest service to test
  static final String ROOT_URL = "http://localhost:8080/tickets/rest";

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
    IHealthRest api = target.proxy(IHealthRest.class);

    // Call the rest status ( try during 4*(2+2) = 16 seconds )
    for (int nb = 0;;)
    {
      try
      {
        // Try to connect ( timeout 2 s)
        api.health();
        break;
      }
      catch (Exception e)
      {
        if (!e.getCause().toString().contains("Connection refused") || nb++ >= 4)
          Assert.fail("cannot connect to REST service");
        
        System.out.println("wait connection...");
        
        // Retry after 2 s
        Thread.sleep(2000);
      }
    }
    // Close client
    client.close();
  }
  
  @AfterClass
  public static void tearDownAfterClass() throws Exception 
  {
    // Close data set
    DaoUnitTester.tearDownClass();
  }
  
  @Before
  public void setUpBefore() throws Exception
  {
    // Re-Init base - data set
    base.setUpBefore();
    
    // Re-Create client of REST api
    client = new ResteasyClientBuilder().build();
    target = client.target(ROOT_URL);
  }

  @After
  public void tearDownAfter() throws Exception
  {
    // Close client
    client.close();
    
    // Clean base - data set
    base.tearDownAfter();
  }

}
