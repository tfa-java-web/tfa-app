package tfa.tickets.rest.test;

import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import tfa.tickets.entities.Ticket;
import tfa.tickets.rest.ITicketRest;

public class TicketRest_IT extends RestApiTester
{
  // ticket api client
  private ITicketRest api;

  @Before
  public void setUpBefore() throws Exception
  {
    super.setUpBefore();

    // ticket api client
    api = target.proxy(ITicketRest.class);
  }

  @Test
  public void testRead()
  {
    Ticket t = api.read(1);
    assertNotNull(t);

    // --------

    // non existing ticket
    t = api.read(10);
    assertNull(t);
  }

  @Test
  public void testCreate()
  {
    // add new ticket
    Ticket t = new Ticket("test", "description of test");
    Response r = api.create(t);

    // reponse get
    assertNotNull(r);
    assertEquals(Response.Status.ACCEPTED.getStatusCode(), r.getStatus());

    // with int id of new ticket
    assertTrue(r.hasEntity());
    assertTrue(r.readEntity(Integer.class) >= 3);
    r.close();

    // --------

    // add existing ticket (detached)
    t.setId(1);
    r = api.create(t);

    // reponse get
    assertNotNull(r);
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus());
    assertFalse(r.hasEntity());
    r.close();

  }

  @Test
  public void testUpdate()
  {
    // update existing ticket 1
    Ticket t = new Ticket("test", "description of test");
    t.setId(1);
    t.setStatus("in_progress");
    Response r = api.update(t);

    // reponse get
    assertNotNull(r);
    assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());

    // with int id of new ticket
    assertTrue(r.hasEntity());
    assertEquals(r.readEntity(Integer.class).intValue(), 1);
    r.close();

    // --------

    // Update not existing ticket (create it with different id)
    t.setId(10000);
    r = api.update(t);

    // reponse get
    assertNotNull(r);
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), r.getStatus());

    // with int id of new ticket
    assertTrue(r.hasEntity());
    assertNotEquals(r.readEntity(Integer.class).intValue(), 10);
    r.close();
  }

  @Test
  public void testDelete()
  {
    // delete existing ticket
    Response r = api.delete(1);
    assertNotNull(r);
    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
    assertFalse(r.hasEntity());
    r.close();

    // --------

    // delete non existing ticket
    r = api.delete(10);
    assertNotNull(r);
    assertEquals(Response.Status.GONE.getStatusCode(), r.getStatus());
    assertFalse(r.hasEntity());
    r.close();
  }

  @Test
  public void testList()
  {
    // all
    List<Ticket> l = api.list(0, 0, null, null, null, null, null, null, null);
    assertNotNull(l);
    assertEquals(3, l.size());

    // with orders
    l = api.list(0, 0, "status,id", null, null, null, null, null, null);
    assertNotNull(l);
    assertEquals(3, l.size());
    assertEquals(2, l.get(0).getId().intValue());
    assertEquals(1, l.get(1).getId().intValue());
    assertEquals(3, l.get(2).getId().intValue());
    l = api.list(0, 0, "status-,id", null, null, null, null, null, null);
    assertNotNull(l);
    assertEquals(3, l.size());
    assertEquals(1, l.get(0).getId().intValue());
    assertEquals(3, l.get(1).getId().intValue());
    assertEquals(2, l.get(2).getId().intValue());

    // with filters
    l = api.list(0, 0, null, "2", null, null, null, null, null);
    assertNotNull(l);
    assertEquals(1, l.size());
    assertEquals(2, l.get(0).getId().intValue());
    l = api.list(0, 0, null, null, "%nd%", null, null, null, null);
    assertNotNull(l);
    assertEquals(1, l.size());
    assertEquals(2, l.get(0).getId().intValue());
    l = api.list(0, 0, null, null, null, "%..3", null, null, null);
    assertNotNull(l);
    assertEquals(1, l.size());
    assertEquals(3, l.get(0).getId().intValue());
    l = api.list(0, 0, null, null, null, null, "closed", null, null);
    assertNotNull(l);
    assertEquals(1, l.size());
    assertEquals(2, l.get(0).getId().intValue());
    l = api.list(0, 0, null, null, null, null, null, "tfa", null);
    assertNotNull(l);
    assertEquals(2, l.size());
    assertTrue(3 > l.get(0).getId().intValue());
    l = api.list(0, 0, null, null, null, null, null, null, "2016-01-20");
    assertNotNull(l);
    assertEquals(1, l.size());
    assertEquals(1, l.get(0).getId().intValue());
    l = api.list(0, 0, "status,id", null, null, null, "open,closed", null, "2016-01-19,2016-01-22");
    assertNotNull(l);
    assertEquals(3, l.size());
    assertEquals(2, l.get(0).getId().intValue());

    // with page,nb
    l = api.list(0, 2, null, null, null, null, null, null, null);
    assertNotNull(l);
    assertEquals(2, l.size());
    l = api.list(1, 2, null, null, null, null, null, null, null);
    assertNotNull(l);
    assertEquals(1, l.size());
    l = api.list(2, 2, null, null, null, null, null, null, null);
    assertNotNull(l);
    assertEquals(0, l.size());

    // with bad order
    try
    {
      l = api.list(0, 0, "unknown", null, null, null, null, null, null);
      fail("not reached");
    }
    catch (BadRequestException e)
    {
      assertNotNull(e);
    }
    catch (Exception e)
    {
      fail(e.toString());
    }

    // with bad filter
    try
    {
      l = api.list(0, 0, null, "156.23", null, null, null, null, null);
      fail("not reached");
    }
    catch (BadRequestException e)
    {
      assertNotNull(e);
    }
    catch (Exception e)
    {
      fail(e.toString());
    }

    // with bad page
    try
    {
      l = api.list(-1, -1, null, null, null, null, null, null, null);
      fail("not reached");
    }
    catch (BadRequestException e)
    {
      assertNotNull(e);
    }
    catch (Exception e)
    {
      fail(e.toString());
    }
  }

  @Test
  public void testCount()
  {
    // all
    assertEquals(3L, api.count(null, null, null, null, null, null).longValue());

    // with filters
    assertEquals(1L, api.count("1", null, null, null, null, null).longValue());
    assertEquals(1L, api.count(null, "The%", null, null, null, null).longValue());
    assertEquals(3L, api.count(null, null, "Description%", null, null, null).longValue());
    assertEquals(2L, api.count(null, null, null, "open", null, null).longValue());
    assertEquals(3L, api.count(null, null, null, "open,closed,in_progress", null, null).longValue());
    assertEquals(2L, api.count(null, null, null, null, "tfa", null).longValue());
    assertEquals(2L, api.count(null, null, null, null, null, "2016-01-21").longValue());
    assertEquals(3L, api.count(null, null, null, null, null, "2016-01-19,2016-01-22").longValue());
    assertEquals(1L, api.count(null, null, null, "closed", "tfa", "2016-01-21").longValue());
    assertEquals(0L, api.count("3", null, null, "closed", "tfa", "2016-01-21").longValue());

    // with bad filters (id)
    try
    {
      assertEquals(3L, api.count("un", null, null, null, null, null).longValue());
      fail("not reached");
    }
    catch (BadRequestException e)
    {
      assertNotNull(e);
    }
    catch (Exception e)
    {
      fail(e.toString());
    }

    // with bad filters (date)
    try
    {
      assertEquals(3L, api.count(null, null, null, null, null, "10 dec 2016").longValue());
      fail("not reached");
    }
    catch (BadRequestException e)
    {
      assertNotNull(e);
    }
    catch (Exception e)
    {
      fail(e.toString());
    }
  }

}
