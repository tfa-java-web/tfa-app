package tfa.tickets.rest;

import java.io.Serializable;

import javax.faces.bean.RequestScoped;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.gui.HitCountBean;

// Implementation of REST api for HitCount object
@RequestScoped
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
        int hits = HitCountBean.getGlobalHitCount();

        // Return the count as string for REST call
        log.trace("rest call : hitCount = " + hits);
        return Response.ok(Integer.toString(hits)).build();
    }
}
