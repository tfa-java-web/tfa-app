package tfa.tickets.rest.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import tfa.tickets.entities.Status;
import tfa.tickets.rest.IStatusRest;

public class StatusRest_IT extends RestApiTester
{
  // -------------------------------------------------- tests cases

  @Test
  public void testList() throws Exception
  {
    IStatusRest api = target.proxy(IStatusRest.class);

    // Send REST request
    List<Status> l = api.list();

    // Check response
    assertEquals(9, l.size());
    assertTrue(l.get(0).getName().length() > 0);
    assertTrue(l.get(0).getColor().length() > 0);
  }
}
