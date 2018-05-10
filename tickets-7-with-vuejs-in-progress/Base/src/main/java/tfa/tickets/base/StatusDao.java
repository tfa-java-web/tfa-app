package tfa.tickets.base;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import tfa.tickets.entities.Status;

// Not a generic DAO : only read all list
class StatusDao implements IStatusDao
{
  @Override
  public List<Status> getList()
  {
      // Simple query all list
      EntityManager em = GenericDao.getEmf().createEntityManager();
      TypedQuery<Status> q = em.createQuery("select o from Status o", Status.class);
      List<Status> result = q.getResultList();
      em.close();
      
      return result;
  }  
}
