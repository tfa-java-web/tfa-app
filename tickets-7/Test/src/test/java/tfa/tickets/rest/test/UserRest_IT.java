package tfa.tickets.rest.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import tfa.tickets.entities.User;
import tfa.tickets.rest.IUserRest;

public class UserRest_IT extends RestApiTester
{
  // -------------------------------------------------- tests cases

  @Test
  public void testList() throws Exception
  {
    IUserRest api = target.proxy(IUserRest.class);

    // Send REST request
    List<User> l = api.list();

    // Check response
    assertEquals(5, l.size());
    assertTrue(l.get(0).getIdent().length() > 0);
    assertTrue(l.get(0).getFullName().length() > 0);
    assertTrue(l.get(0).getMail().length() > 0);
  }
}
