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

    public RestInitialize()
    {       
        // List all rest api classes 
        singletons.add(new HealthRest());
        singletons.add(new HitCountRest());
        singletons.add(new StatusRest());
        singletons.add(new UserRest());
        singletons.add(new TicketRest());

        // Provider (exception mapper)
        singletons.add(new ExceptionCatcher());
    }

    @Override
    public Set<Object> getSingletons()
    {
        return singletons;
    }

}
