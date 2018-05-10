package tfa.tickets.auth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.Base64;
import java.util.Random;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.cage.Cage;
import com.github.cage.GCage;

/**
 *  Manage the internal form for login a user / session
 */
@ManagedBean @SessionScoped
public class Authenticator implements Serializable
{
    private static final long serialVersionUID = -8473853218536105653L;
    
    // Standard SLF4J logger
    private static final Logger log = LoggerFactory.getLogger(Authenticator.class);
    
    // An ascii + ascii-ext phrase as key of encryptation : see also login.xhtml
    private static final String KEY = "µAv²#5\tp|Xp_àùMr@hjüéZ/vRt!k7+ -\fHgAç";
    
    // A magic value of captcha  (don't document this passkey)
    private static final String MAGIC_CAPTCHA = "mC@ptCh@";
    
    // Login form inputs
    private String username;
    private String password; // encrypted

    // is session with admin role
    private boolean admin = false;

    // Last session-login Captcha-string
    private String captchaString = null;


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
            String pass = decrypt(password, KEY);
            log.debug( "decrypt= " + pass );
           if (captchaString != null ) 
            {
                if ( pass.startsWith(captchaString))
                    pass = pass.substring(captchaString.length());
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
  
    // ------------------------------------------- basic encryptation, see tools.js

    public static String encrypt(final String text, String key)
    {
        if ( key == null ) key = KEY;
        byte[] tx = text.getBytes(Charsets.ISO_8859_1);
        byte[] xb = xor(tx, key);
        return new String(Base64.getEncoder().encode(xb), Charsets.ISO_8859_1);
    }

    public static String decrypt(final String hash, String key)
    {
        if ( key == null ) key = KEY;
        byte[] tx = Base64.getDecoder().decode(hash.getBytes(Charsets.ISO_8859_1));
        byte[] xb = xor(tx, key);
        return new String(xb, Charsets.ISO_8859_1);
    }

    private static byte[] xor(final byte[] input, final String key)
    {
        // very simple xor cryptage, variing according first char
        // just to mask password if no https ; not robust at all !
        byte[] output = new byte[input.length];
        byte[] secret = key.getBytes(Charsets.ISO_8859_1);
        output[0] = input[0];
        int spos = (int)(input[0]) % 10;
        for (int pos = 1; pos < input.length; ++pos)
        {
            output[pos] = (byte) (input[pos] ^ secret[spos]);
            spos += 1;
            if (spos >= secret.length)
                spos = 0;
        }
        return output;
    }

    private synchronized byte[] generateCaptcha()
    {
        Cage cage = new GCage();
        byte[] captchaImage = null;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream())
        {
            // Simple text to avoid input mistakes : 3 digits
            captchaString = Integer.toString(new Random().nextInt(900) + 100);
            cage.draw(captchaString, os);
            captchaImage = os.toByteArray();
        }
        catch (IOException e)
        {
            captchaString = null;
            captchaImage = null;
        }
        return captchaImage;
    }

    // ------------------------------------------- setters getters

    public byte[] getCaptchaImage()
    {
        // Next captcha image
        return generateCaptcha();
    }

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

    public String getCaptchaString()
    {
        return captchaString;
    }

    public void setCaptchaString(String captchaString)
    {
        this.captchaString = captchaString;
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
