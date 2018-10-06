package tfa.tickets.auth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  A JAAS login module with two usages :
 *   - either with internal login form
 *   - either with Jasig CAS SSO 
 *   - The users & rights dictionnary into a simple properties file
 */
public class TfaLoginModule implements LoginModule
{
    // Standard SLF4J logger
    private static Logger log = LoggerFactory.getLogger(TfaLoginModule.class);

    // trace info log
    private boolean debug = false;

    // user descriptor
    private Subject subject = null;
    private CallbackHandler callbackHandler = null;

    // the authentication status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;

    // username and password
    private String username = null;
    private String password = null;
    private List<String> userGroups;
   
    // principal
    private TfaUserPrincipal userPrincipal;

    // A simple database / dictionary of users : from properties file
    private Properties ldap = new Properties();

    public TfaLoginModule()
    {
        super();
    }

    @Override
    public void initialize(Subject subject, CallbackHandler handler, Map<String, ?> sharedState, Map<String, ?> options)
    {
        debug = "true".equalsIgnoreCase((String) options.get("debug"));

        if (debug)
            log.info("initialize");

        // subject
        this.subject = subject;
        this.callbackHandler = handler;
              
        // path of config properties file
        String configPath = (String) options.get("configPath");
        if (configPath == null)
            configPath = "./realm.properties";

        if (debug)
            log.info("configPath = " + configPath);

        try
        {
            // Load user dictionary consisting of lines username:password,role,role
            ldap.load(new FileInputStream(configPath));

            if (debug)
                log.info("Users configuration loaded");
        }
        catch (FileNotFoundException e)
        {
            log.error("file not found: " + configPath);

            // add default user
            if (debug)
                ldap.setProperty("admin", "passwd,ADMIN,USER");
        }
        catch (IOException e)
        {
            log.error(e.toString());
            ldap.clear();

            // add default user
            if (debug)
                ldap.setProperty("admin", "passwd,ADMIN,USER");
        }
    }

    @Override
    public boolean login() throws LoginException
    {
        if (debug)
            log.info("login");

        if (callbackHandler == null)
            throw new LoginException("callbackHandler is null");

        // init callbacks
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("name:");
        callbacks[1] = new PasswordCallback("password:", false);

        try
        {
            callbackHandler.handle(callbacks);
        }
        catch (IOException e)
        {
            throw new LoginException("IOException on callbackHandler");
        }
        catch (UnsupportedCallbackException e)
        {
            throw new LoginException("UnsupportedCallbackException on callbackHandler");
        }

        // get name and paswword form FORM
        NameCallback nameCallback = (NameCallback) callbacks[0];
        PasswordCallback passwordCallback = (PasswordCallback) callbacks[1];
        username = nameCallback.getName();
        password = new String(passwordCallback.getPassword());
        if (debug) 
        {
            log.info("entered password=" + password);
            log.info("entered username=" + username);
        }
        
        // get/check user login infos from ldap file
        try
        {    
            boolean casAuth = false;
            
            // VARIANT : CAS authentication case : ticket -> username
            if ( username.contains("/serviceValidate?service=") ) 
            {
                casAuth = true;
                // !!!! check cas ticket here !!!!
                username = casValidate(username, password); 
                if ( username == null )
                    throw new NoSuchElementException("cas validation failed");
            }
            
            // find username
            String pr = ldap.getProperty(username);
            if (pr == null)
                throw new NoSuchElementException("username not found");

            // find password,roles
            String[] words = pr.split(",");
            if (words.length < 1)
                throw new IOException("password,roles expected");

            // NO VARIANT : If native auth 
            if ( !casAuth )
            {
                // !!!! check password here !!!!
                String storedPaswword = words[0].trim();
                if (!password.equals(storedPaswword))
                    throw new IOException("failed password check");
            }
            
            // set roles, minimum USER
            userGroups = new ArrayList<String>();
            words[0] = "USER";
            for (String role : words)
                if (!userGroups.contains(role))
                    userGroups.add(role);

            if (debug)
                log.info("login succeeded, roles " + userGroups.toString());

            succeeded = true;
            return succeeded;
        }
        catch (Exception e)
        {
            if (debug)
                log.info("login failed " + e.toString());

            succeeded = false;
            throw new FailedLoginException("Login failed");
        }
    }

    @Override
    public boolean commit() throws LoginException
    {
        if (debug)
            log.info("commit");

        // login: failure, commit : return false
        if (!succeeded)
            return false;
 
        // add a Principal (authenticated identity) to the Subject
        userPrincipal = new TfaUserPrincipal(username);          
        subject.getPrincipals().add(userPrincipal);
        
        
        // add roles, as group  and  individual
        TfaGroupPrincipal group = new TfaGroupPrincipal("Roles");
        for (String role : userGroups) {
            TfaRolePrincipal r = new TfaRolePrincipal(role);
            group.addMember( r );
            subject.getPrincipals().add( r );          
        }
        userPrincipal.setRoles(group);
        subject.getPrincipals().add(group);
       
        if (debug)
            log.info("added TfaPrincipal,TfaRolePrincipal to Subject");

        // in any case, clean out state
        username = null;
        password = null;
        userGroups = null;

        // login and comit success
        commitSucceeded = true;
        return true;
    }

    @Override
    public boolean abort() throws LoginException
    {
        if (debug)
            log.info("abort");

        // login failure : abort return false
        if (!succeeded)
            return false;

        if (!commitSucceeded)
        {
            // login succeeded but overall authentication failed
            succeeded = false;
            username = null;
            password = null;
            userGroups = null;
            userPrincipal = null;
        }
        else
        {
            logout();
        }
        return true;
    }

    @Override
    public boolean logout() throws LoginException
    {
        if (debug)
            log.info("logout");

        // reset
        succeeded = false;
        commitSucceeded = false;
        username = null;
        password = null;
        userGroups = null;

        // not logged
        if (userPrincipal == null)
            return true; 

        // remove registered principals
        subject.getPrincipals().remove(userPrincipal);
        subject.getPrincipals().removeIf(p -> p instanceof TfaRolePrincipal);
        userPrincipal = null;

        // clean up CAS session : done by http redirect into CAS service  
        
        if (debug)
            log.info("logout succeed");

        return true;
    }
    
    // ------------------------------------------- VARIANT : CAS part login module

    private String casValidate(final String serviceValidateUrl, final String casTicket)
    {
        // Validation by CAS 2.0 : send request to CAS serveur and wait response
        StringBuffer response = new StringBuffer();
        try 
        {
            // Call http cas to validate ticket
            URL obj = new URL(serviceValidateUrl + "&ticket=" + casTicket);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int code = con.getResponseCode();

            // Check OK
            if (code != HttpURLConnection.HTTP_OK)
                return null;

            // Read xml response
            try ( InputStreamReader isr = new InputStreamReader(con.getInputStream());
                  BufferedReader in = new BufferedReader(isr) )
            {
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);
            }
            catch (Exception e)
            {
                return null;
            }
        }
        catch (Exception e)
        {
            return null;
        }
        String xml = response.toString();
        if (xml == null || !xml.contains("authenticationSuccess"))
            return null;
    
        // Get username from xml
        int from = xml.indexOf("user>");
        if ( from <= 0 ) return null;
        from += 5;
        int end = xml.indexOf("</", from);
        if ( end <= from ) return null;
        
        // Username is too long
        if ( (end - from) > 256 ) return null;

        String username = xml.substring(from, end);
        
        // Decoding special char if permmtted : todo

        return username;
    }

}
