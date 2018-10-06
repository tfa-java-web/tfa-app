package tfa.tickets.rest;

import java.io.Serializable;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Implementation of REST api for getting health and informations on tickets application 
class HealthRest implements IHealthRest, Serializable
{
    private static final long serialVersionUID = 90411567090090331L;

    // Standard SLF4J logger
    private static final Logger log = LoggerFactory.getLogger(HealthRest.class);

    public HealthRest()
    {
        super();
    }

    public Response health()
    {
        // REST call
        log.trace("rest call : health");

        // TODO check database connection
        // TODO check cpu loading, mem available, jvm health
        // TODO check cluster instances health

        // TODO return health details,
        // TODO app identifier, version, author

        // For now : simply return an OK
        return Response.ok("OK").build();
    }
}
