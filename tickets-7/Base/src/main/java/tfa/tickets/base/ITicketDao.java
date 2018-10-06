package tfa.tickets.base;


import tfa.tickets.entities.Ticket;

/**
 * Represents Data access to Ticket entity, with all generic operations
 */
public interface ITicketDao extends IGenericDao<Ticket>
{
  // see IGenericDao : no specific interface for the moment
  public static ITicketDao getInstance()
  {
    return new TicketDao();
  }
}
