package tfa.tickets.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// See associate parametrised interface IGenericDao
class GenericDao<T>
{
  // class T
  protected Class<T> entityClass;

  // One EntityManager per thread (request)
  private static ThreadLocal<EntityManager> tlem;
  protected static EntityManagerFactory emf;

  // One transaction by thread (request), else by method call (manual)
  private static boolean transactionByThread = false;

  // Standard SLF4J logger
  private final static Logger log = LoggerFactory.getLogger(GenericDao.class);

  @SuppressWarnings("unchecked")
  protected GenericDao()
  {
    // Retrieve class T
    ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
    entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
  }

  // ------------------------------- Management of entityManager per thread/request

  static EntityManagerFactory init(final String unitName)
  {
    // create assoc of em - thread
    tlem = new ThreadLocal<EntityManager>();

    // init jpa hibernate from persistence.xml
    emf = Persistence.createEntityManagerFactory(unitName);
    return emf;
  }

  static void term()
  {
    // close jpa
    emf.close();
    tlem = null;
    emf = null;
  }

  static EntityManager getEm()
  {
    // current thread has already an em assoc
    EntityManager em = tlem.get();
    if (em == null)
    {
      // no: create em
      log.trace("< em create");
      em = emf.createEntityManager();

      // assoc to current thread
      tlem.set(em);

      // transaction by thread
      if (transactionByThread)
        em.getTransaction().begin();
    }
    // return em (like a connection)
    return em;
  }

  static void lazyCloseEm()
  {
    // current thread has an em assoc
    EntityManager em = tlem.get();
    if (em != null)
    {
      // yes : transaction by thread
      if (transactionByThread)
      {
        EntityTransaction et = em.getTransaction();
        if (et != null)
        {
          try
          {
            // commit all changes
            if (et.isActive())
              et.commit();
          }
          finally
          {
            if (et.isActive())
            {
              // if rollback not allready done
              log.warn("em rollback");
              et.rollback();
            }
          }
        }
      }
      // close em ( connection)
      log.trace("> em close");
      em.close();

      // unassoc from thread
      tlem.set(null);
    }
  }

  static Object get(final String name)
  {
    try
    {
      // create a dao for this entity name
      Class<?> cl = Class.forName(GenericDao.class.getPackage().getName() + "." + name + "Dao");
      Constructor<?> ct = cl.getDeclaredConstructor();
      return ct.newInstance();
    }
    catch (Exception e)
    {
      return null;
    }
  }

  static void setTransactionByThread(final boolean mode)
  {
    // switch transaction mode : by thread-request / by method call-manual
    transactionByThread = mode;
  }

  static boolean getTransactionByThread()
  {
    // transaction mode : by thread-request / by method call-manual
    return transactionByThread;
  }

  static EntityManagerFactory getEmf()
  {
    return emf;
  }
  
  // ----------------------------------------- Generic dao : CRUD , List, Count, Query

  public T create(final T t)
  {
    // to return
    T result = t;

    // open session if not already
    EntityManager em = getEm();

    // App Managed transaction manually
    EntityTransaction et = null;
    if (!transactionByThread)
      et = em.getTransaction();

    try
    {
      // start of transaction
      if (et != null) et.begin();

      // create (or update if existing attached entity)
      em.persist(t);
      log.trace("entity persist: " + t);

      // flush transaction (check constraint)
      if (et != null) et.commit();
    }
    finally
    {
      // in case of constrain error, and et not already closed
      if (et != null && et.isActive())
      {
        log.warn("entity rollback");
        et.rollback();
      }
    }
    // returned with id filled
    return result;
  }

  public T read(final Integer id)
  {
    EntityManager em = getEm();

    // find and read object if found
    T t = em.find(entityClass, id);
    log.trace("entity find: " + t);

    return t;
  }

  public T read(final String id)
  {
    EntityManager em = getEm();

    // find and read object if found
    T t = em.find(entityClass, id);
    log.trace("entity find: " + t);

    return t;
  }

  public T update(final T t)
  {
    T result = t;
    EntityManager em = getEm();

    EntityTransaction et = null;
    if (!transactionByThread)
      et = em.getTransaction();

    try
    {
      if (et != null) et.begin();

      // merge attached/detached objects
      result = em.merge(t);
      log.trace("entity merge: " + t + " to " + result );

      if (et != null) et.commit();
    }
    finally
    {
      if (et != null && et.isActive())
      {
        log.warn("entity rollback");
        et.rollback();
      }
    }
    return result;
  }

  public void delete(final T t)
  {
    EntityManager em = getEm();

    EntityTransaction et = null;
    if (!transactionByThread)
      et = em.getTransaction();

    try
    {
      if (et != null) et.begin();

      // cascade changes before removing
      T t2 = em.merge(t);

      // remove entity
      em.remove(t2);
      log.trace("entity remove: " + t2);

      if (et != null) et.commit();
    }
    finally
    {
      if (et != null && et.isActive())
      {
        log.warn("entity rollback");
        et.rollback();
      }
    }
  }

  public List<T> getList()
  {
    return getList(null, null, 0, 0);
  }

  public List<T> getList(final Map<String, Object> params, final List<String> orderParams)
  {
    return getList(params, orderParams, 0, 0);
  }

  public Long count()
  {
    return count(null);
  }

  // ------------------------------------------------------- jp ql utility

  // SELECT T o FROM T WHERE p1=v1 AND p2=v2 ORDER BY o1 ASC,o2 DESC OFFSET page*nb LIMIT nb
  public List<T> getList(final Map<String, Object> params, final List<String> orderParams, int page, int nb)
  {
    EntityManager em = getEm();

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<T> c = cb.createQuery(entityClass);
    Root<T> t = c.from(entityClass);
    c.select(t);

    // TODO select all except some fields 
    // c.select( cb.construct(entityClazz.class, root.get("field1"), root.get("field2")) );

    addCriterii(c, cb, t, params);
    addOrders(c, cb, t, orderParams);
    TypedQuery<T> q = em.createQuery(c);
    addParams(q, params);

    if (nb < 0)
      throw new IllegalArgumentException("negative page size");

    if (nb > 0)
    {
      q.setFirstResult(page * nb);
      q.setMaxResults(nb);
    }
    return q.getResultList();
  }

  // SELECT count(*) FROM T WHERE p1=v1 AND p2=v2
  public Long count(final Map<String, Object> params)
  {
    EntityManager em = getEm();

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> c = cb.createQuery(Long.class);
    Root<T> t = c.from(entityClass);
    c.select(cb.count(t));

    addCriterii(c, cb, t, params);
    TypedQuery<Long> q = em.createQuery(c);
    addParams(q, params);

    return q.getSingleResult();
  }

  protected void addCriterii(CriteriaQuery<?> c, final CriteriaBuilder cb, final Root<T> t,
      final Map<String, Object> params)
  {
    if (params == null)
      return;

    // parameter index
    int i = 1;

    // list of criteria
    List<Predicate> lc = new ArrayList<Predicate>();
    for (final Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext();)
    {
      final Map.Entry<String, Object> entry = it.next();
      String key = entry.getKey();
      
      // accepted key : !field...  field...
      if ( key.startsWith("!") ) key = key.replaceFirst("!", "");
      Predicate e = getCriteria(cb, t, key, entry.getValue(), i);
      if ( entry.getKey().startsWith("!") ) e = cb.not(e); 
      lc.add(e);   
           
      // next parameter index
      i++;  
    }

    // 'and' of list of criteria
    if (lc.size() > 1)
      c.where(cb.and(lc.toArray(new Predicate[0])));
    else if (lc.size() == 1)
      c.where(lc.get(0));
  }

  protected Predicate getCriteria(CriteriaBuilder cb, Root<T> t, String key, Object value, int i)
  {
    // accepted key : field (in list)
    if (value instanceof List<?>)
    {
      @SuppressWarnings("rawtypes")
      ParameterExpression<List> p = cb.parameter( List.class, "p" + i);
      return t.get(key).in(p);
    }
    // accepted key : field field= field!= field> field< field>= field<= field% 
    if (key.endsWith("%"))
    {
      if (! (value instanceof String) ) throw new IllegalArgumentException("string expected");
      ParameterExpression<String> p = cb.parameter(String.class, "p" + i);
      return cb.like(t.get(key.replace("%", "")), p, '\\');
    } 
    if (key.endsWith(">"))
    {
      if ( value instanceof Date )
      {
        ParameterExpression<Date> p = cb.parameter(Date.class, "p" + i);
        return cb.greaterThan(t.get(key.replace(">", "")), p);      
      }
      if (! (value instanceof Number) ) throw new IllegalArgumentException("number expected");
      ParameterExpression<Number> p = cb.parameter(Number.class, "p" + i);
      return cb.gt(t.get(key.replace(">", "")), p);
    }
    if (key.endsWith("<"))
    {
      if ( value instanceof Date )
      {
        ParameterExpression<Date> p = cb.parameter(Date.class, "p" + i);
        return cb.lessThan(t.get(key.replace("<", "")), p);      
      }
      if (! (value instanceof Number) ) throw new IllegalArgumentException("number expected");
      ParameterExpression<Number> p = cb.parameter(Number.class, "p" + i);
      return cb.lt(t.get(key.replace("<", "")), p);
    }
    if (key.endsWith(">="))
    {
      if ( value instanceof Date )
      {
        ParameterExpression<Date> p = cb.parameter(Date.class, "p" + i);
        return cb.greaterThanOrEqualTo(t.get(key.replace(">=", "")), p);      
      }
      if (! (value instanceof Number) ) throw new IllegalArgumentException("number expected");
      ParameterExpression<Number> p = cb.parameter(Number.class, "p" + i);
      return cb.ge(t.get(key.replace(">=", "")), p);
    }
    if (key.endsWith("<="))
    {
      if ( value instanceof Date )
      {
        ParameterExpression<Date> p = cb.parameter(Date.class, "p" + i);
        return cb.lessThanOrEqualTo(t.get(key.replace("<=", "")), p);      
      }
      if (! (value instanceof Number) ) throw new IllegalArgumentException("number expected");
      ParameterExpression<Number> p = cb.parameter(Number.class, "p" + i);
      return cb.le(t.get(key.replace("<=", "")), p);
    } 
    if (key.endsWith("!="))
    {
      ParameterExpression<?> p = cb.parameter(value.getClass(), "p" + i);
      return cb.notEqual(t.get(key.replace("!=", "")), p);
    }   
    if (key.endsWith("="))
      key = key.replace("=", "" );
     
    // operator '=' default  
    ParameterExpression<?> p = cb.parameter(value.getClass(), "p" + i);
    return cb.equal(t.get(key), p);  
  }

  private void addParams(TypedQuery<?> q, Map<String, Object> params)
  {
    if (params == null)
      return;

    // parameter index
    int i = 1;

    // list of criteria
    for (final Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator(); it.hasNext();)
    {
     // set the value of next parameter
      q.setParameter("p" + (i++), it.next().getValue());
    }
  }

  private void addOrders(CriteriaQuery<T> c, final CriteriaBuilder cb, final Root<T> t, final List<String> orderParams)
  {
    if (orderParams == null)
      return;

    // list of order fields
    List<Order> lo = new ArrayList<Order>();
    for (final Iterator<String> it = orderParams.iterator(); it.hasNext();)
    {
      final String key = it.next();

      // accepted sort format 'field' 'field+' 'field-'
      if (key.endsWith("+"))
        lo.add(cb.asc(t.get(key.replace("+", ""))));
      else if (key.endsWith("-"))
        lo.add(cb.desc(t.get(key.replace("-", ""))));
      else
        lo.add(cb.asc(t.get(key)));
    }
    if (lo.size() > 0)
      c.orderBy(lo);
  }

}
