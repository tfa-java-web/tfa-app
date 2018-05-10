package tfa.tickets.rest;

import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.base.IStatusDao;
import tfa.tickets.entities.Status;

//Implementation of REST api for tickets-status list
class StatusRest implements IStatusRest
{
  // Standard SLF4J logger
  private static final Logger log = LoggerFactory.getLogger(StatusRest.class);

  // Get a dao to access base
  private IStatusDao dao = IStatusDao.getInstance();

  public List<Status> list()
  {
    // REST call
    log.trace("rest call : list");

    // at least one status in base
    List<Status> result = dao.getList();
    if (result == null || result.isEmpty())
      throw new NoSuchElementException("no status-list found");

    return result;

  }
}
