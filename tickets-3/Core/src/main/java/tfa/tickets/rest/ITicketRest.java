package tfa.tickets.rest;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import tfa.tickets.entities.Ticket;

/**
 * REST Api for Ticket object
 */
@Path("/tickets")
public interface ITicketRest
{
  @GET
  @Path("{id : \\d+}")
  @Produces(MediaType.APPLICATION_JSON)
  public Ticket read(@PathParam("id") final Integer id);

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public Response create(final Ticket t);

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public Response update(final Ticket t);

  @DELETE
  @Path("{id : \\d+}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response delete(@PathParam("id") final Integer id);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Ticket> list(
      @DefaultValue("0") @QueryParam("page") final Integer page,
      @DefaultValue("0") @QueryParam("size") final Integer size,
      @DefaultValue("") @QueryParam("order") final String order,
      @DefaultValue("") @QueryParam("status") final String status,
      @DefaultValue("") @QueryParam("user") final String user);

  @GET
  @Path("count")
  @Produces(MediaType.APPLICATION_JSON)
  public Long count(
      @DefaultValue("") @QueryParam("status") final String status,
      @DefaultValue("") @QueryParam("user") final String user);

}
