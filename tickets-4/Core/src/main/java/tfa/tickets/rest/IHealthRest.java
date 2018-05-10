package tfa.tickets.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * REST Api for health and informations on tickets application
 */
@Path("/health")
public interface IHealthRest
{
  @GET
  public Response health();
}
