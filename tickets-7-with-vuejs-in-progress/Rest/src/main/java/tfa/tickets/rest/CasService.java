package tfa.tickets.rest;

import java.io.IOException;
import java.io.Serializable;

import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.security.PermitAll;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import tfa.tickets.core.Configuration;

/**
 * CAS Authentication REST service
 * make the link between JAAS Login and Jasig CAS callbacks
 */
@Path("/cas")
@PermitAll
class CasService implements Serializable
{
    private static final long serialVersionUID = -8473853218536105653L;
    private static final String casUrl = Configuration.getJaasConfig("casUrl");
    private static String casService = null;

    
    @GET
    @Path("/login")
    public Response login( @Context final HttpServletRequest request ) throws URISyntaxException
    {
        // Construct cas service URL within this service
        if ( casService == null )
            casService = request.getRequestURL().toString().replace("/login", "/service");
          
        // redirect to server cas
        String url = casUrl + "/login?service=" + casService;
        return Response.temporaryRedirect(new URI(url)).build();
    }

    @GET
    @Path("/logout")
    public Response logout( @Context final HttpServletRequest request ) throws URISyntaxException, ServletException
    {
        // logout session
        request.logout();
        request.getSession().invalidate();
       
        // redirect to server cas  logout
        String url = casUrl + "/logout";
        return Response.temporaryRedirect(new URI(url)).build();
    }

    @GET
    @Path("/logerr")
    public Response logerr()
    {
        // error page
        return Response.status(Status.FORBIDDEN).entity("Cas authentication error").build();
    }

    @GET
    @Path("/service")
    public Response casService( @QueryParam("ticket") final String casTicket, 
           @Context final HttpServletRequest request ) throws ServletException, URISyntaxException
    {
        // not already logged with CAS
        if (casTicket == null || casTicket.isEmpty())
            return login( request );

        // Construct cas service URL within this service
        if ( casService == null )
            casService = request.getRequestURL().toString().replace("/login", "/service");
        
        try
        {
            // Validation by CAS 2.0 : a direct call to CAS server
            String serviceValidate = casUrl + "/serviceValidate?service=" + casService;
            
            // LoginModule processus (do the direct CAS call)
            request.login(serviceValidate, casTicket);

            // No exception : Auth OK, go to start page
            return Response.temporaryRedirect(new URI("../pages/home.xhtml")).build();
        }
        catch (ServletException e)
        {
            // redirect to server cas  logout
            String url = casUrl + "/logout?service=" + casService.replace("service", "logerr");
            return Response.temporaryRedirect(new URI(url)).build();
        }
    }

    @POST
    @Path("/service")
    public Response casLogout( @Context final HttpServletRequest request ) throws URISyntaxException, ServletException, IOException
    {
        // Direct Call from CAS server on logout
        
        // Session logout if CAS logout was not already done by myself
        request.logout();
        request.getSession().invalidate();
        return Response.ok().build();
    }

}
