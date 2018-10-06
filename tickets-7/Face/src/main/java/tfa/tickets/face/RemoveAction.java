package tfa.tickets.face;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import tfa.tickets.base.ITicketDao;
import tfa.tickets.entities.Ticket;

/**
 * Bean to manage dialog box to confirm removing of one ticket
 */
@ManagedBean @ViewScoped
public class RemoveAction extends DialogBase
{
  private static final long serialVersionUID = 1680336913046179140L;
  
  // Ticket to be removed
  private Ticket ticket = null;

  // action to open confirmation dialog
  public String confirm(Ticket t)
  {
    if (t == null || t.getId() == null)
      return null;

    setTicket(t);
    setDisplayed(true);
    return null;
  }

  // action to do the removing
  @Override
  public String validate()
  {
    if (ticket != null)
    {
      ITicketDao dao = ITicketDao.getInstance();
      dao.delete(ticket);
    }

    // Refresh all
    return redirect("home");
  }

  // -------------------------------- getters setters

  public Ticket getTicket()
  {
    return ticket;
  }

  public void setTicket(Ticket ticket)
  {
    this.ticket = ticket;
  }

}
