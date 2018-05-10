package tfa.tickets.base;

import java.util.List;

import tfa.tickets.entities.Status;

/**
 * Represents Data access to Status list (read only)
 */
public interface IStatusDao 
{
  public static IStatusDao getInstance()
  {
    return new StatusDao();
  }
  
  public List<Status> getList();
  
}
