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
    // Get current hit count in bean
    int hits = HitCountBean.getGlobalHitCount();

    // Return the count as string for REST call
    log.trace("rest call : hitCount = " + hits);
    return Response.status(Response.Status.OK).entity(Integer.toString(hits)).build();
  }
}
