package tfa.tickets.rest;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.base.ITicketDao;
import tfa.tickets.core.InputValidator;
import tfa.tickets.entities.Ticket;

//Implementation of REST api for Tickets objects
class TicketRest implements ITicketRest, Serializable
{
    private static final long serialVersionUID = 4478479867618483375L;

    // Standard SLF4J logger
    private static final Logger log = LoggerFactory.getLogger(TicketRest.class);

    // Get a dao to access base
    private ITicketDao dao = ITicketDao.getInstance();

    // Get input validator tool
    private InputValidator chk = InputValidator.getInstance();

    // ------------------------------------ CRUD Rest API

    public TicketRest()
    {
        super();
    }

    public Ticket read(final Integer id)
    {
        // try to find it
        Ticket t = dao.read(id);
        return t;
    }

    public Response create(final Ticket t)
    {
        // Check input ticket
        chk.ticketValid(t, false);

        // create it
        Ticket tc = dao.create(t);
        if (tc == null || tc.getId() == null)
            throw new RuntimeException("ticket not created");

        // normal status or updated returned
        Status status = Response.Status.CREATED;
        if (t.getId() != null && t.getId() == tc.getId())
            status = Response.Status.ACCEPTED;

        // return new id into response
        return Response.status(status).entity(tc.getId()).build();
    }

    public Response update(final Ticket t)
    {
        // check input ticket
        chk.ticketValid(t, true);

        // update must return same id
        Ticket tc = dao.update(t);
        if (tc == null || tc.getId() == null || (t.getId() != null && t.getId() != tc.getId()))
        {
            if (tc != null && tc.getId() != null && t.getId() != null && t.getId() != tc.getId())
                return Response.status(Response.Status.NOT_FOUND).entity(tc.getId()).build();

            throw new RuntimeException("ticket not updated");
        }

        // normal status or create returned
        Status status = Response.Status.OK;
        if (t.getId() == null)
            status = Response.Status.ACCEPTED;

        // return id (same or perhaps created one) into response
        return Response.status(status).entity(tc.getId()).build();
    }

    public Response delete(final Integer id)
    {
        // find it first
        Ticket t = dao.read(id);
        if (t == null)
        {
            log.trace("not existing ticket " + id);
            return Response.status(Response.Status.GONE).build();
        }
        // delete (with merging first)
        dao.delete(t);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    // -------------------------------------------------- Find list Rest API

    public List<Ticket> list(final Integer page, final Integer size, final String order, final String id, final String title, final String desc,
            final String status, final String user, final String date)
    {
        // check asked criteria
        if (page < 0 || size < 0)
            throw new IllegalArgumentException("negative size or page");

        // find list according these criteria
        List<Ticket> result = dao.getList(makeFilter(id, title, desc, status, user, date), makeOrder(order), page.intValue(), size.intValue());
        if (result == null)
            throw new RuntimeException("null list result");

        return result;
    }

    public Long count(final String id, final String title, final String desc, final String status, final String user, final String date)
    {
        // compute count according these criteria
        Long count = dao.count(makeFilter(id, title, desc, status, user, date));
        if (count == null || count < 0L)
            throw new RuntimeException("no or negative count");

        return count;
    }

    // --------------------------------------------------------- decode filter parameters

    private List<String> makeOrder(final String order)
    {
        // init criteria
        List<String> orders = new ArrayList<String>();

        // sort fields
        if (order != null)
        {
            // ex "status-,id"
            String[] ol = order.split(",");
            for (String o : ol)
            {
                chk.orderValid(o);
                orders.add(o);
            }
        }

        return orders;
    }

    private Map<String, Object> makeFilter(final String id, final String title, final String desc, final String status, final String user, final String date)
    {
        Map<String, Object> filter = new HashMap<String, Object>();

        // id range
        if (id != null)
        {
            // ex "123,234" ",123," "254," "23"
            String[] ir = id.split("-");
            if (ir.length > 2)
                throw new IllegalArgumentException();

            if (ir.length > 0 && !ir[0].isEmpty())
            {
                chk.intValid(ir[0]);

                if (ir.length == 2)
                    filter.put("id>=", Integer.parseInt(ir[0]));
                else
                    filter.put("id=", Integer.parseInt(ir[0]));
            }

            if (ir.length > 1 && !ir[1].isEmpty())
            {
                chk.intValid(ir[1]);
                filter.put("id<=", Integer.parseInt(ir[1]));
            }
        }

        // title like
        if (title != null)
        {
            chk.asciiValid(title, 64);
            filter.put("title%", title);
        }

        // desc like
        if (desc != null)
        {
            chk.asciiValid(desc, 64);
            filter.put("desc%", desc);
        }

        // status list
        if (status != null)
        {
            String[] l = status.split(",");
            for (String k : l)
                chk.identValid(k, 32);
            if (l.length > 0)
                filter.put("status", (l.length == 1 ? l[0] : Arrays.asList(l)));
        }

        // user
        if (user != null)
        {
            chk.identValid(user, 32);
            filter.put("user", user);
        }

        // date range
        if (date != null)
        {
            // ex
            String[] datei = date.split(",");
            if (datei.length > 2)
                throw new IllegalArgumentException();

            try
            {
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
                if (datei.length > 0 && !datei[0].isEmpty())
                {
                    chk.dateValid(datei[0]);

                    if (datei.length == 2)
                        filter.put("date>=", ft.parse(datei[0]));
                    else
                        filter.put("date=", ft.parse(datei[0]));
                }

                if (datei.length > 1 && !datei[1].isEmpty())
                {
                    chk.dateValid(datei[1]);
                    filter.put("date<=", ft.parse(datei[1]));
                }
            }
            catch (ParseException e)
            {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        return filter;
    }

}
