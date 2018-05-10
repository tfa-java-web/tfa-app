package tfa.tickets.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import tfa.tickets.entities.Status;

/**
 * REST Api for getting tickets-status list
 */
@Path("/status")
@RolesAllowed("USER")
public interface IStatusRest
{
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Status> list();

}
