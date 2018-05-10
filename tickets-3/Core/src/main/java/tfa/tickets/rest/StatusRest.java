package tfa.tickets.rest;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Implementation of REST api for HitCount object
class StatusRest implements IStatusRest
{
    // Standard SLF4J logger
    private static final Logger log = LoggerFactory.getLogger(StatusRest.class);

    public Response status()
    {
        // REST call
        log.trace("rest call : status");
        return Response.status(Response.Status.OK).entity("OK").build();
    }
}
