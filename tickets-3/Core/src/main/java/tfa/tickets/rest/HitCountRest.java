package tfa.tickets.rest;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.gui.HitCountBean;

// Implementation of REST api for HitCount object
class HitCountRest implements IHitCountRest
{
    // Standard SLF4J logger
    private static final Logger log = LoggerFactory.getLogger(HitCountRest.class);

    public Response hitCount()
    {
        // Get current hit count
        String ret = Integer.toString(HitCountBean.getGlobalHitCount());

        // REST call
        log.trace("rest call : hitCount = " + ret);
        return Response.status(Response.Status.OK).entity(ret + " visits").build();
    }
}
