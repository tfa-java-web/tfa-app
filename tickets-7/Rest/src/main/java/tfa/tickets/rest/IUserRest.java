package tfa.tickets.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import tfa.tickets.entities.User;

/**
 * REST Api for getting tickets Users list
 */
@Path("/users")
@RolesAllowed("USER")
public interface IUserRest
{
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<User> list();

}
