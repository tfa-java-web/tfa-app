package tfa.tickets.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * REST Api for Status object
 */

@Path("/status")
public interface IStatusRest
{
    @GET
    public Response status();
}
