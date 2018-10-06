package tfa.tickets.rest;

import java.io.Serializable;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.base.IHitCountDao;

// Implementation of REST api for HitCount object
class HitCountRest implements IHitCountRest, Serializable
{
    private static final long serialVersionUID = -6949194433200101642L;

    // Standard SLF4J logger
    private static final Logger log = LoggerFactory.getLogger(HitCountRest.class);

    public HitCountRest()
    {
        super();
    }

    public Response hitCount()
    {
        // Get current hit count in bean
        int hits = IHitCountDao.getInstance().read();

        // Return the count as string for REST call
        log.trace("rest call : hitCount = " + hits);
        return Response.ok(Integer.toString(hits)).build();
    }
}
