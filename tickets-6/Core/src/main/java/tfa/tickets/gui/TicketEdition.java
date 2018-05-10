package tfa.tickets.gui;

import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;

import tfa.tickets.base.ITicketDao;
import tfa.tickets.entities.Ticket;

/**
 * Bean to manage edition form of a ticket ( submit new or modify )
 */
@ManagedBean @ViewScoped
public class TicketEdition implements Serializable
{
  private static final long serialVersionUID = -9109182559743810233L;

  // id of ticket to modify or 0 for a new submitted ticket
  private int param = 0;
  
  // edited ticket
  private Ticket ticket = null;
  
  // data access
  private ITicketDao dao = ITicketDao.getInstance();

  
  // ------------ constructor
  
  public TicketEdition()
  {
    super();
  }

  // ------------  validation of edition form

  public String validate()
  {
    // Adjust last modif date
    ticket.setDate(new Date());

    // Save in database
    ticket = dao.update(ticket);

    // Update displayed table, update current ticket detail at display
    return "home?faces-redirect=true&includeViewParams=true";
  }

  // ------------ getters setters

  public Ticket getTicket()
  {
    if (ticket == null)
    {
      if (param == 0)
      {
        // Create new ticket
        ticket = new Ticket();
      }
      else
      {
        // Read from base
        ticket = dao.read(param);
      }
    }
    return ticket;
  }

  public void setTicket(Ticket ticket)
  {
    this.ticket = ticket;
    setParam( (ticket == null || ticket.getId() == null) ? 0 : ticket.getId() );
  }

  public void setParam(int param)
  {
    this.param = param;
  }

  public int getParam()
  {
    return param;
  }

}
