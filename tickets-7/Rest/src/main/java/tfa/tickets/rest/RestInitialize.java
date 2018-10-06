package tfa.tickets.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Root of rest calls of tickets application
@ApplicationPath("/rest")
public class RestInitialize extends Application
{
    // Standard SLF4J logger
    private static Logger log = LoggerFactory.getLogger(RestInitialize.class);

    // Container of rest api objects
    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> classes = new HashSet<Class<?>>();
    private Map<String, Object> properties = new HashMap<String, Object>();

    public RestInitialize()
    {       
        log.info("contextInitialized start");
        
        // Security roles for Resteasy
        properties.put("resteasy.role.based.security", "true");
        
        // List all rest api classes (for Weld Injection)
        //classes.add(AuthRest.class);
        //classes.add(HealthRest.class);
        //classes.add(HitCountRest.class);
        //classes.add(StatusRest.class);
        //classes.add(UserRest.class);
        //classes.add(TicketRest.class);
        //classes.add(CasService.class);
        
        // List all rest api classes (without CDI Injection)
        // singletons --> @ApplicationScoped
        singletons.add(new AuthRest());     // for JAAS
        singletons.add(new HealthRest());
        singletons.add(new HitCountRest());
        singletons.add(new StatusRest());
        singletons.add(new UserRest());
        singletons.add(new TicketRest());
        singletons.add(new CasService());   // for JAAS +  Jasig CAS 
        singletons.add(new UploadFile());

        // Provider (exception mapper)
        singletons.add(new ExceptionCatcher());
        
        log.info("contextInitialized end");
    }

    @Override
    public Set<Object> getSingletons()
    {
        return singletons;
    }

    @Override
    public Set<Class<?>> getClasses()
    {
        return classes;
    }

    @Override
    public Map<String, Object> getProperties()
    {
         return properties;
    }
}
