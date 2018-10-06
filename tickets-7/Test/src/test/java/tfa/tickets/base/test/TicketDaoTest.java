package tfa.tickets.base.test;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.RollbackException;

import org.dbunit.dataset.ITable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tfa.tickets.base.IGenericDao;
import tfa.tickets.base.ITicketDao;
import tfa.tickets.entities.Ticket;

public class TicketDaoTest extends DaoUnitTester
{
  // -------------------------------------------------- tests init

  private ITicketDao dao;

  @Before
  public void setUpBefore() throws Exception
  {
    super.setUpBefore();

    dao = (ITicketDao) IGenericDao.get("Ticket");
    assertNotNull(dao);
  }

  @After
  public void tearDownAfter() throws Exception
  {
    dao = null;

    super.tearDownAfter();
  }

  // -------------------------------------------------- tests cases

  @Test
  public void testCreate()
  {
    // insert new ticket
    Ticket t = dao.create(new Ticket("An example", "INFO \n org.eclipse.jetty.server.Server\r\n"));

    // check new affected id
    assertTrue(t.getId() > 0);
    if (!IGenericDao.getTransactionByThread()) try
    {
      // check result in database
      ITable tab = databaseTester.getConnection().createTable("TICKET");
      assertEquals("An example", tab.getValue(t.getId() - 1, "title"));
      assertEquals(4, tab.getRowCount());
    }
    catch (Exception e)
    {
      fail(e.toString());
    }

    // already existing case (not detached) : update
    Integer old = t.getId();
    t.setTitle("other title");
    t = dao.create(t);
    assertEquals(old, t.getId());
    if (!IGenericDao.getTransactionByThread()) try
    {
      // check result in database
      ITable tab = databaseTester.getConnection().createTable("TICKET");
      assertEquals("other title", tab.getValue(t.getId() - 1, "title"));
      assertEquals(4, tab.getRowCount());
    }
    catch (Exception e)
    {
      fail(e.toString());
    }
  }

  @Test
  public void testRead()
  {
    // Read dasaset ticket 3
    Ticket t = dao.read(3);

    // check read id
    assertTrue(t.getId() != 0);

    // check title id
    assertEquals("The title", t.getTitle());

    // Read inexistent ticket
    t = dao.read(33);
    assertNull(t);

    // Check hib cache : no sql read again (check into log)
    t = dao.read(3);
    t.setTitle("other title");
    t = dao.read(3);
    t = dao.read(3);
    assertEquals("other title", t.getTitle());
  }

  @Test
  public void testUpdate()
  {
    // update a ticket which exist
    Ticket t = dao.read(3);
    t.setDesc("hello change!");
    t.setStatus("in progress");
    dao.update(t);
    if (!IGenericDao.getTransactionByThread()) try
    {
      // check result in database
      ITable tab = databaseTester.getConnection().createTable("TICKET");
      assertEquals("The title", tab.getValue(t.getId() - 1, "title"));
      assertEquals("hello change!", tab.getValue(t.getId() - 1, "desc"));
      assertEquals("in progress", tab.getValue(t.getId() - 1, "status"));
      assertEquals(3, tab.getRowCount());
    }
    catch (Exception e)
    {
      fail(e.toString());
    }

    // update non existing ticket : create from null id
    t = new Ticket("aaaaaa", "bbbbbbb");
    t = dao.update(t);
    assertTrue(t.getId() > 0);
    if (!IGenericDao.getTransactionByThread()) try
    {
      // check result in database
      ITable tab = databaseTester.getConnection().createTable("TICKET");
      assertEquals(4, tab.getRowCount());
    }
    catch (Exception e)
    {
      fail(e.toString());
    }

    // update non existing ticket : create from non null id
    t = new Ticket("ccccccccc", "ddddddddd");
    t.setId(33);
    t = dao.update(t);
    assertTrue(t.getId() > 0 && t.getId() != 33);
    if (!IGenericDao.getTransactionByThread()) try
    {
      // check result in database
      ITable tab = databaseTester.getConnection().createTable("TICKET");
      assertEquals(5, tab.getRowCount());
    }
    catch (Exception e)
    {
      fail(e.toString());
    }
  }

  @Test
  public void testDelete()
  {
    // delete a ticket which exist and loaded
    Ticket t = dao.read(2);
    dao.delete(t);

    if (!IGenericDao.getTransactionByThread()) try
    {
      // check result in database
      ITable tab = databaseTester.getConnection().createTable("TICKET");
      assertEquals(2, tab.getRowCount());
    }
    catch (Exception e)
    {
      fail(e.toString());
    }

    // check cannot yet load
    t = dao.read(2);
    assertNull(t);

    // delete a ticket which not exist : no exception
    t = new Ticket("phantom", "ticket");
    dao.delete(t);
    t.setId(44);
    dao.delete(t);

    // delete a ticket which exist (based on id)
    t.setId(3);
    dao.delete(t);

    if (!IGenericDao.getTransactionByThread()) try
    {
      // check result in database
      ITable tab = databaseTester.getConnection().createTable("TICKET");
      assertEquals(1, tab.getRowCount());
    }
    catch (Exception e)
    {
      fail(e.toString());
    }
    t = dao.read(3);
    assertNull(t);
  }

  @Test
  public void testCount()
  {
    // dataset has 3 rows
    assertEquals(3L, dao.count().longValue());
  }

  @Test
  public void testGetList()
  {
    // dataset has 3 rows, index 2 is The title
    List<Ticket> l = dao.getList();
    assertEquals(3, l.size());
    assertEquals("The title", l.get(2).getTitle());
  }

  @Test
  public void testCountMapOfStringObject()
  {
    // dataset has 1 row with The title
    Map<String, Object> filter = new HashMap<String, Object>();
    filter.put("title", "The title");
    assertEquals(1L, dao.count(filter).longValue());

    // dataset has 2 rows with date 21
    filter.clear();
    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
    try
    {
      filter.put("date", ft.parse("2016-01-21"));
    }
    catch (ParseException e)
    {
      fail("parsing test date");
    }
    assertEquals(2L, dao.count(filter).longValue());
  }

  @Test
  public void testGetListMapOfStringObjectListOfString() throws ParseException
  {
    // filter criteria simple : 2 rows
    Map<String, Object> filter = new HashMap<String, Object>();
    filter.put("user", "tfa");

    // sort criteria
    List<String> sort = new ArrayList<String>();
    sort.add("status");
    sort.add("user-");

    List<Ticket> l = dao.getList(filter, sort);
    assertEquals(2, l.size());
    assertEquals("closed", l.get(0).getStatus());

    // filter criteria 'and' double : 1 rows
    filter.clear();
    filter.put("user", "tfa");
    filter.put("status", "open");
    sort.clear();
    l = dao.getList(filter, sort);
    assertEquals(1, l.size());

    // filter criteria simple : 0 rows
    filter.clear();
    filter.put("user", "unknown");
    l = dao.getList(filter, sort);
    assertEquals(0, l.size());

    // filter criteria in list : 2 rows
    filter.clear();
    List<Integer> lid = new ArrayList<Integer>();
    lid.add(1);
    lid.add(3);
    filter.put("id", lid);
    l = dao.getList(filter, sort);
    assertEquals(2, l.size());

    // filter criteria < number : 2 rows
    filter.clear();
    filter.put("id<", 3);
    l = dao.getList(filter, sort);
    assertEquals(2, l.size());

    // filter criteria <= number : 3 rows
    filter.clear();
    filter.put("id<=", 3);
    l = dao.getList(filter, sort);
    assertEquals(3, l.size());

    // filter criteria > date : 2 rows
    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
    Date d = ft.parse("2016-01-20");
    filter.clear();
    filter.put("date>", d);
    l = dao.getList(filter, sort);
    assertEquals(2, l.size());

    // filter criteria like : 3 rows
    filter.clear();
    filter.put("title%", "%title%");
    filter.put("desc%", "%3%");
    l = dao.getList(filter, sort);
    assertEquals(1, l.size());

    // filter criteria not like : 2 rows
    filter.clear();
    filter.put("!title%", "The%");
    l = dao.getList(filter, sort);
    assertEquals(2, l.size());
  }

  @Test
  public void testGetListMapOfStringObjectListOfStringIntInt()
  {
    Map<String, Object> filter = new HashMap<String, Object>();
    List<String> sort = new ArrayList<String>();

    // get a page 0 size 2
    List<Ticket> l = dao.getList(null, null, 0, 2);
    assertEquals(2, l.size());

    // get a page 1 size 2
    l = dao.getList(null, null, 1, 2);
    assertEquals(1, l.size());

    // get all
    l = dao.getList(filter, sort, 0, 0);
    assertEquals(3, l.size());

    // with filter and sort
    filter.put("user", "tfa");
    sort.add("status+");
    l = dao.getList(filter, sort, 1, 1);
    assertEquals(1, l.size());
    assertEquals("open", l.get(0).getStatus());
  }

  @Test
  public void testDetachedObject()
  {
    // read a row and detach from hibernate management
    Ticket t = dao.read(2);
    IGenericDao.getEm().detach(t);

    // reread the object (stay attached)
    Ticket t2 = dao.read(2);
    t.setTitle("merged title");

    // update with detach object : update with merge t -> t2 !
    t = dao.update(t);
    assertEquals(3L, dao.count().longValue());
    assertEquals("merged title", t2.getTitle());
    assertEquals("merged title", t.getTitle());
    assertEquals(t, t2);

    if (!IGenericDao.getTransactionByThread()) try
    {
      // check result in database
      ITable tab = databaseTester.getConnection().createTable("TICKET");
      assertEquals("merged title", tab.getValue(1, "title"));
      assertEquals(3, tab.getRowCount());
    }
    catch (Exception e)
    {
      fail(e.toString());
    }
  }

  @Test
  public void testRollback()
  {
    // Make inconsistent ticket in memory
    Ticket t = dao.read(2);
    t.setTitle(null);

    try
    {
      // try to save in base
      dao.update(t);

      // simulate closing request now for by-thread mode
      if (IGenericDao.getTransactionByThread())
        IGenericDao.lazyCloseEm();

      // check Not reached
      fail("no exception catched !");
    }
    catch (RollbackException e)
    {
      // Check memory object : incoherent null title
      assertNull(t.getTitle());
      try
      {
        // Check result in database : null title not saved (commit failed - rolled back)
        ITable tab = databaseTester.getConnection().createTable("TICKET");
        assertEquals("2nd title", tab.getValue(1, "title"));
        assertEquals(3, tab.getRowCount());
      }
      catch (Exception e2)
      {
        fail(e2.toString());
      }
    }
    finally
    {
      // Close and reopen new entity manager to purge dirty entity in memory
      IGenericDao.lazyCloseEm();
      dao = (ITicketDao) IGenericDao.get("Ticket");
      assertNotNull(dao);

      // Check cleaned entity reread from base : no null title
      t = dao.read(2);
      assertNotNull(t.getTitle());
      assertEquals("2nd title", t.getTitle());
    }
  }

  @Test
  public void testLimitSecurity()
  {
    // negative index
    Ticket t = dao.read(-1);
    assertNull(t);

    try
    {
      // negative size
      dao.getList(null, null, 0, -4);
      fail("must not reached");
    }
    catch (Exception e)
    {
    }

    try
    {
      // negative page
      dao.getList(null, null, -1, 2);
      fail("must not reached");
    }
    catch (Exception e)
    {
    }

    try
    {
      // bad column name for filter
      Map<String, Object> filter = new HashMap<String, Object>();
      List<String> sort = new ArrayList<String>();
      filter.put("unknown", "blahblah");
      dao.getList(filter, sort);
      fail("must not reached");
    }
    catch (Exception e)
    {
    }

    try
    {
      // injection in column name
      Map<String, Object> filter = new HashMap<String, Object>();
      List<String> sort = new ArrayList<String>();
      filter.put("' OR TRUE", "");
      dao.getList(filter, sort);
      fail("must not reached");
    }
    catch (Exception e)
    {
    }

    try
    {
      // bad column name (comma not expected)
      Map<String, Object> filter = new HashMap<String, Object>();
      List<String> sort = new ArrayList<String>();
      sort.add("user,status");
      dao.getList(filter, sort);
      fail("must not reached");
    }
    catch (Exception e)
    {
    }

    try
    {
      // bad column name ( white space not expected)
      Map<String, Object> filter = new HashMap<String, Object>();
      List<String> sort = new ArrayList<String>();
      sort.add("- user");
      dao.getList(filter, sort);
      fail("must not reached");
    }
    catch (Exception e)
    {
    }
  }

}
