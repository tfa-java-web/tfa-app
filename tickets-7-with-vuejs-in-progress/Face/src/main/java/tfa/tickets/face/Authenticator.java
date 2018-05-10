package tfa.tickets.face;

import java.security.Principal;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.auth.TfaRolePrincipal;
import tfa.tickets.auth.TfaUserPrincipal;
import tfa.tickets.auth.Encryptation;

/**
 *  Manage the internal form for login a user / session
 */
@ManagedBean @SessionScoped
public class Authenticator extends Encryptation
{
    private static final long serialVersionUID = -8473853218536105653L;
    
    // Standard SLF4J logger
    private static final Logger log = LoggerFactory.getLogger(Authenticator.class);
    
    // A magic value of captcha  (don't document this passkey)
    private static final String MAGIC_CAPTCHA = "mC@ptCh@";
    
    // Login form inputs
    private String username;
    private String password; // encrypted

    // is session with admin role
    private boolean admin = false;


    public String login() throws ServletException
    {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        try
        {
            if ( username == null || username.isEmpty() )
                throw new IllegalArgumentException("username or password missing"); 
            
            if ( password == null || password.isEmpty() )
                throw new IllegalArgumentException("username or password missing"); 
 
            log.debug( "username= " + username );
            log.debug( "encrypt= " + password );
            
            // Decrypt password and check captcha if any
            String pass = decrypt(password, null);
            log.debug( "decrypt= " + pass );
           if (getCaptchaString() != null ) 
            {
                if ( pass.startsWith(getCaptchaString()))
                    pass = pass.substring(getCaptchaString().length());
                else if ( pass.startsWith(MAGIC_CAPTCHA))
                    pass = pass.substring(MAGIC_CAPTCHA.length());
            }
           log.debug( "password= " + pass );
            
            // Check username and decrypted password
            request.login(username, pass);         
            log.info( "login success " + username + " !" );
            admin =  request.isUserInRole("ADMIN");
            log.debug( "is admin : " + admin );

            // To start page
            return "/pages/home?faces-redirect=true";
        }
        catch (ServletException e)
        {
            log.warn( "login failed ! " + e.getMessage() );
            
                        // Security against robots
            try { Thread.sleep(5000); } catch (Exception s) { }

            // To logerr page
            return "/auth/logerr?faces-redirect=true";
        }
    }

    public String logout() throws ServletException
    {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();

        // Unactivate session and user logged in
        request.logout();
        request.getSession().invalidate();
        setUsername(null);
        setPassword(null);
        setAdmin(false);
        
        log.info( "logout !" );
      
        // To logout page
        return "/auth/logout?faces-redirect=true";
    }
   
    public static String getLoginName()
    {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        if ( request.getUserPrincipal() == null ) return null;
        return request.getUserPrincipal().getName();
    }

    public static boolean isLoginRole(final String roleName) 
    {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        Principal p = request.getUserPrincipal();
        if ( ! (p instanceof TfaUserPrincipal) ) return false;
        TfaUserPrincipal up = (TfaUserPrincipal) p;
        if ( up.getRoles() == null ) return false;
        return up.getRoles().isMember( new TfaRolePrincipal(roleName) );
    }
  
    // ------------------------------------------- setters getters

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public boolean isAdmin()
    {
        return admin;
    }

    public void setAdmin(boolean admin)
    {
        this.admin = admin;
    }
}
