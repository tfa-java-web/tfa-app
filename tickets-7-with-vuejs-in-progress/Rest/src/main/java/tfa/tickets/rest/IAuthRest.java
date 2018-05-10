package tfa.tickets.rest;

import javax.annotation.security.PermitAll;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Api for Authentication of REST client 
 */
@Path("/auth")
@PermitAll
public interface IAuthRest
{
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response logged( @Context final HttpServletRequest request );
   
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response login( @Context final HttpServletRequest request, final String userToken ) throws ServletException;

    @DELETE
    public Response logout( @Context final HttpServletRequest request ) throws ServletException;
}
