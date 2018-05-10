package tfa.tickets.rest;

import static org.junit.Assert.*;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// Example of integration test using RestEasyClient
public class RestApi_IT
{
    // URL of rest service to test
    static final String ROOT_URL = "http://localhost:8080/tickets/rest";

    // Client of service
    static ResteasyClient client;
    static ResteasyWebTarget target;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        // Create client of REST api
        client = new ResteasyClientBuilder().build();
        target = client.target(ROOT_URL);
        StatusRestApi api = target.proxy(StatusRestApi.class);

        // Call the rest status ( try during 4*(2+2) = 16 seconds )
        for (int nb = 0;;)
        {
            try
            {
                // Try to connect ( timeout 2 s)
                api.status();
                break;
            }
            catch (Exception e)
            {
                if (!e.getCause().toString().contains("Connection refused") || nb++ >= 4)
                    throw e;

                // Retry after 2 s
                Thread.sleep(2000);
            }
        }
        // Close client
        client.close();
    }

    @Before
    public void setUpBefore()
    {
        // Re-Create client of REST api
        client = new ResteasyClientBuilder().build();
        target = client.target(ROOT_URL);
    }

    @After
    public void tearDownAfter()
    {
        // Close client
        client.close();
    }

    @Test
    public void testHitCount() throws Exception
    {
        HitCountRestApi api = target.proxy(HitCountRestApi.class);

        // Send REST request
        Response r = api.hitCount();

        // Get response value
        String result = r.readEntity(String.class);

        // Check response
        assertTrue(result.contains("visits"));
    }
}
