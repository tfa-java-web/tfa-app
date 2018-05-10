package tfa.tickets.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.base.ITicketDao;
import tfa.tickets.entities.Ticket;

//Implementation of REST api for Ticket object
class TicketRest implements ITicketRest
{
  // Standard SLF4J logger
  private static final Logger log = LoggerFactory.getLogger(TicketRest.class);

  // Get a dao to access base
  private ITicketDao dao = ITicketDao.getInstance();

  public Ticket read(final Integer id)
  {
    Ticket t = dao.read(id);

    if (t == null)
      log.trace("not existing ticket " + id);

    return t;
  }

  public Response create(final Ticket t)
  {
    if (t.getId() != null)
      log.warn("already existing ticket " + t.getId());

    Ticket tc = dao.create(t);

    return Response.status(201).entity("ticket/" + tc.getId()).build();
  }

  public Response update(final Ticket t)
  {
    if (t.getId() == null)
      log.warn("not existing ticket " + t.getId());

    Ticket tc = dao.update(t);

    return Response.status(200).entity("ticket/" + tc.getId()).build();
  }

  public Response delete(final Integer id)
  {
    Ticket t = dao.read(id);

    if (t != null)
      dao.delete(t);
    else
      log.warn("not existing ticket " + id);

    return Response.status(204).entity("removed").build();
  }

  public List<Ticket> list(final Integer page, final Integer size, final String order, final String status,
      final String user)
  {
    Map<String, Object> filter = new HashMap<String, Object>();
    List<String> orders = new ArrayList<String>();
    if (order != null && !order.isEmpty())
    {
      String[] ol = order.split(",");
      for (String o : ol)
        orders.add(o);
    }

    if (status != null && !status.isEmpty()) filter.put("status", status);
    if (user != null && !user.isEmpty()) filter.put("user", user);
    return dao.getList(filter, orders, page.intValue(), size.intValue());
  }

  public Long count(final String status, final String user)
  {
    Map<String, Object> filter = new HashMap<String, Object>();
    if (status != null && !status.isEmpty()) filter.put("status", status);
    if (user != null && !user.isEmpty()) filter.put("user", user);
    Long count = dao.count(filter);
    return count;
  }

}
