package tfa.tickets.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

// Root of rest calls of tickets application
@ApplicationPath("/rest")
public class RestInitialize extends Application
{
    // Container of rest api objects
    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> classes = new HashSet<Class<?>>();

    public RestInitialize()
    {       
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

        // Provider (exception mapper)
        singletons.add(new ExceptionCatcher());
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


}
