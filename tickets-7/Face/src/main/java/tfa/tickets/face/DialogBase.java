package tfa.tickets.face;

import java.io.Serializable;

/**
 * Base class for gui dialog bean
 */
public class DialogBase implements Serializable
{
  private static final long serialVersionUID = 1L;

  // is dialog displayed (shown / closed)
  private boolean displayed = false;

  // ---------------------------------- default actions

  public String validate()
  {
    return cancel();
  }

  public String cancel()
  {
    // close
    setDisplayed(false);

    // stay on same page
    return null;
  }
  
  public String redirect( String page )
  {
    return page.concat("?faces-redirect=true");
  }

  // ------------------------------------- setter getter

  public boolean isDisplayed()
  {
    return displayed;
  }

  public void setDisplayed(boolean displayed)
  {
    this.displayed = displayed;
  }
}
