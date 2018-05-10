package tfa.tickets.gui;

import java.util.HashSet;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import tfa.tickets.base.ITicketDao;
import tfa.tickets.entities.Ticket;

/**
 * Bean to manage group actions dialog 
 * (same action on a list of selected tickets)
 */
@ManagedBean
@ViewScoped
public class GroupAction extends DialogBase
{
  private static final long serialVersionUID = 986648524061266784L;
  
  // The list of selected tickets (action on to )
  private HashSet<Ticket> selection = null;
  
  // The chosen action 
  private String action = null;
  
  // Possible actions on a group of tickets
  private String[] actions = { "Unselect", "Close", "Assign", "Remove" };
  
  // The chosen user for assign action
  private String user = "myself";

  // --------------------------------- actions
  
  // action to open the group actions dialog
  public String actionChoice(HashSet<Ticket> selection)
  {
    // save the selection
    this.selection = selection;

    // display dialog box
    setDisplayed(true);

    // on same page
    return null;
  }

  // action to do the chosen group action
  @Override
  public String validate()
  {
    ITicketDao dao = ITicketDao.getInstance();
    
    // for each ticket selected
    for (Ticket t : selection)
    {
      // do the action on each of selected item
      if (action.equals("Close"))
      {
        t.setStatus("closed");
        dao.update(t);
      }
      else if (action.equals("Assign"))
      {
        t.setUser(user);
        dao.update(t);
      }
      else if (action.equals("Remove"))
      {
        dao.delete(t);
      }
      // else unselect all
    }
    // Refresh all 
    return redirect("home");
  }

  // action on group action cancel
  @Override
  public String cancel()
  {
    // Refresh all (unselect all)
    return redirect("home");
  }

  // ------------------- Setters , Getters

  public HashSet<Ticket> getSelection()
  {
    return selection;
  }

  public void setSelection(HashSet<Ticket> selection)
  {
    this.selection = selection;
  }

  public String getAction()
  {
    return action;
  }

  public void setAction(String action)
  {
    this.action = action;
  }

  public String getUser()
  {
    return user;
  }

  public void setUser(String user)
  {
    this.user = user;
  }

  public String[] getActions()
  {
    return actions;
  }

  public void setActions(String[] actions)
  {
    this.actions = actions;
  }

}
