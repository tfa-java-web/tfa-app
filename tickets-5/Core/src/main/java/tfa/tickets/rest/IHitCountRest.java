package tfa.tickets.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * REST Api for HitCount object
 */
@Path("/hitCount")
public interface IHitCountRest
{
  @GET
  public Response hitCount();
}
