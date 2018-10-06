package tfa.tickets.base;

import java.util.List;

import tfa.tickets.entities.User;

/**
 * Represents Data access to User list (read only)
 */
public interface IUserDao 
{
  public static IUserDao getInstance()
  {
    return new UserDao();
  }
  
  public List<User> getList();
  
}
