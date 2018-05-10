package tfa.tickets.base;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Represents the base class of DAO of JPA entities
 * - implements crud functions on an entity with Integer pk
 * - implements read list and count, with basic filter and order
 * - params : field-value pairs to filter as field1 ope value1 AND field2 ope value2
 * - characters at end of fieldname represents ope (default =) :  % > >= <= < = != 
 * - a character ! at start of field name : not (field1 ope value1)
 * - ex1 the pair "!title%" "[AB]c%" computes as: not (title like "[AB]c%")  
 * - ex2 the pair "id"  list of 1,6,3 integer as: id in (1,6,3)
 * - orderParams : list of "field", "field+", "field-"
 * - read-list can be paginated : page index zero based and page size
 * - manage EntityManager , one by thread (one by http request)
 * - manage application transaction, by call (default) or by thread (option).
 */
public interface IGenericDao<T>
{
  // ------------------- generic dao interface

  /** create a entity (or update an attached entity) */
  public T create(final T t);

  /** Find and read an entity from primary key, null if not found */
  public T read(final Integer id);

  /** Save change on entity (by merging existing attached entity) */
  public T update(final T t);

  /** Remove entity (with previous cascade update) */
  public void delete(final T t);

  /** Retrieve all entities list (all table unordered) */
  public List<T> getList();

  /** Retrieve filtered entities list , with optional order */
  public List<T> getList(final Map<String, Object> params, final List<String> orderParams);

  /** Retrieve filtered entities list , with optional order , by page index zero based and page size */
  public List<T> getList(final Map<String, Object> params, final List<String> orderParams, final int page,  final int nb);

  /** Get total count of table */
  public Long count();

  /** Get count of filtered table */
  public Long count(final Map<String, Object> params);

  // -------------------- public client interface for implementation static methods

  /** Initialise the using of all daos */
  public static EntityManagerFactory init(final String unitName)
  {
    return GenericDao.init(unitName);
  }

  /** Finalise the using of all daos */
  public static void term()
  {
    GenericDao.term();
  }

  /** Construct a new DAO for this entity name */
  public static Object get(final String name)
  {
    return GenericDao.get(name);
  }

  /** Get Current EntityManager (for the current thread/request) */
  public static EntityManager getEm()
  {
    return GenericDao.getEm();
  }

  /** Close Current EntityManager (to call at end of request) */
  public static void lazyCloseEm()
  {
    GenericDao.lazyCloseEm();
  }

  /** Change the transaction mode */
  public static void setTransactionByThread(final boolean mode)
  {
    GenericDao.setTransactionByThread(mode);
  }

  /** Get the mode : true = auto by thread, false (default) = by dao method (manual) */
  public static boolean getTransactionByThread()
  {
    return GenericDao.getTransactionByThread();
  }

}
