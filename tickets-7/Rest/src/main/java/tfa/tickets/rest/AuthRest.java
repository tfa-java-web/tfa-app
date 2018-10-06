package tfa.tickets.rest;

import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.auth.Encryptation;

/**
 *  Implementation of REST api for Authentication for REST client 
 */
class AuthRest extends Encryptation implements IAuthRest, Serializable 
{
    private static final long serialVersionUID = -2768309811725649166L;
    
    // Standard SLF4J logger
    private static final Logger log = LoggerFactory.getLogger(AuthRest.class);

    public Response logged( final HttpServletRequest request )
    {
        String user = null;
        if ( request.getUserPrincipal() != null ) 
            user = request.getUserPrincipal().getName();

        if ( user == null ) user = "not logged";
        return Response.ok().entity(user).build();
    }

    public Response login( final HttpServletRequest request, final String userToken ) throws ServletException
    {          
        // decode "user:token" and decrypt token  
        String mots[] = userToken.split(":");
        if ( mots.length != 2 ) throw new IllegalArgumentException("user:token expected");
        String user = mots[0];
        String token = mots[1];
           
        // Decrypt password and check captcha if any
        String pass = getPasswordFromToken(token);
            
        // log user in session  (Exception if bad user paasword ) 
        request.login(user, pass);
        
        log.info(" User " + user + " logged !");
        
        return Response.ok().build();
    }

    public Response logout( final HttpServletRequest request ) throws ServletException
    {
        // logout session
        request.logout();
        request.getSession().invalidate();
        
        log.info(" User logout !");
        
        return Response.ok().build();
    }

    public Response nextCaptcha(HttpServletRequest request) throws ServletException
    {
        // generate a new captcha image 
        byte[] img = getCaptchaImage();
        
        // img src attribute : html inline data image
        String inlineImg = "data:image/png;base64," + Base64.encodeBase64String(img);
        return Response.ok( inlineImg ).build();
    }
    
}
