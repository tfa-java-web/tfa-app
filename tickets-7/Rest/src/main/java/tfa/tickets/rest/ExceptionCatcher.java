package tfa.tickets.rest;

import java.util.NoSuchElementException;

import javax.persistence.EntityExistsException;
import javax.persistence.RollbackException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Catch exception raised from REST API requests, and compute appropriated response
@Provider
class ExceptionCatcher implements ExceptionMapper<RuntimeException>
{
  // Standard SLF4J logger
  private static final Logger log = LoggerFactory.getLogger(ExceptionCatcher.class);

  @Override
  public Response toResponse(RuntimeException e)
  {
    // already build response
    if (e instanceof WebApplicationException)
    {
      // embedded response
      Response r = ((WebApplicationException) e).getResponse();

      // log level according status
      if (r.getStatus() < 300) log.trace(e.toString());
      else if (r.getStatus() < 400) log.warn(e.toString());
      else log.error(e.toString());

      return r;
    }

    // compute status from exception category
    Status s;
    if (e instanceof IllegalArgumentException)
    {
      log.trace(e.toString());
      s = Response.Status.BAD_REQUEST;
    }
    else if (e instanceof NoSuchElementException)
    {
      log.trace(e.toString());
      s = Response.Status.NOT_FOUND;
    }
    else if (e instanceof RollbackException || e instanceof EntityExistsException)
    {
      log.warn(e.toString());
      s = Response.Status.CONFLICT;
    }
    else // default returned status
    {
      log.error(e.toString());
      s = Response.Status.INTERNAL_SERVER_ERROR;
    }

    // http response with only code (no details send back for security)
    return Response.status(s).build();
  }
}
