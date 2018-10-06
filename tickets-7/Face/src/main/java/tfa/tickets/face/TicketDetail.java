package tfa.tickets.face;

import java.io.Serializable;
import java.util.List;


import javax.faces.event.ValueChangeEvent;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;

import tfa.tickets.base.IStatusDao;
import tfa.tickets.base.ITicketDao;
import tfa.tickets.base.IUserDao;
import tfa.tickets.entities.Status;
import tfa.tickets.entities.Ticket;
import tfa.tickets.entities.User;

/**
 * Bean to manage current ticket displayed
 */
@ManagedBean @ViewScoped
public class TicketDetail implements Serializable
{
  private static final long serialVersionUID = -2868307192971542946L;

  // the empty ticket : to display blank line
  private static Ticket empty = null;

  // the current displayed ticket in detail 
  private Ticket current = null;
  
  // id of ticket asked  (0 for a new)
  private int param = 0;

  // data access
  private ITicketDao dao = ITicketDao.getInstance();
  private IStatusDao status = IStatusDao.getInstance();
  private IUserDao users = IUserDao.getInstance();

  // ------------ constructor
  
  public TicketDetail()
  {
    super();
  }

  public static Ticket getEmpty()
  {
    // 1st time : create the empty ticket
    if (empty == null)
    {
      empty = new Ticket("", "");
      empty.setStatus("");
      empty.setUser("");
      empty.setDate(null);
    }
    return empty;
  }
  
  // ------------ edition

  public List<User> getUsers()
  {
    return users.getList();
  }

  public List<Status> getStatus()
  {
    return status.getList();
  }
  
  public void reAssign(ValueChangeEvent e)
  {
    String v = (String) e.getNewValue();
    if ( current != null ) 
      current.setUser( v );
  }
  
  public void updateStatus(ValueChangeEvent e)
  {
    String v = (String) e.getNewValue();
    if ( current != null ) 
      current.setStatus( v );
  }  
  
  // ------------ current management
  
  public Ticket getCurrent()
  {
    if (current == null && param != 0)
      current = dao.read(param);
  
    return current;
  }

  public void setCurrent(Ticket current)
  {
    this.current = current;
    setParam( (current == null || current.getId() == null) ? 0 : current.getId() );
  }

  // ------------ getters setters

  public void setParam(int param)
  {
    this.param = param;
  }

  public int getParam()
  {
    return param;
  }

}
