package tfa.tickets.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

// Root of rest calls
@ApplicationPath("/rest")
public class RestApi extends Application
{
    // Container of rest api objects
    private Set<Object> singletons = new HashSet<Object>();

    public RestApi()
    {
        // List all rest api objects
        singletons.add(new StatusRest());
        singletons.add(new HitCountRest());
        singletons.add(new TicketRest());
    }

    @Override
    public Set<Object> getSingletons()
    {
        return singletons;
    }
}
