package tfa.tickets.rest.test;

import static org.junit.Assert.*;

import javax.ws.rs.core.Response;

import org.junit.Test;

import tfa.tickets.rest.IHitCountRest;

public class HitCountRest_IT extends RestApiTester
{
  // -------------------------------------------------- tests cases

  @Test
  public void testHitCount() throws Exception
  {
    IHitCountRest api = target.proxy(IHitCountRest.class);

    // Send REST request
    Response r = api.hitCount();

    // Get response value
    String result = r.readEntity(String.class);

    // Check response
    assertTrue(Integer.parseInt(result) >= -1);
  }
}
