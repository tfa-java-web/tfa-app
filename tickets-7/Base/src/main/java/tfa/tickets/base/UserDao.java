package tfa.tickets.base;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import tfa.tickets.entities.User;

//Not a generic DAO : only read all list
class UserDao implements IUserDao
{
  @Override
  public List<User> getList()
  {   
      // Simple query all list
      EntityManager em = GenericDao.getEmf().createEntityManager();
      TypedQuery<User> q = em.createQuery("select o from User o", User.class);
      List<User> result = q.getResultList();
      em.close();
      
      return result;
  }  
}
