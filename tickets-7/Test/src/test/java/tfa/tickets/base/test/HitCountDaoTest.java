package tfa.tickets.base.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tfa.tickets.base.IHitCountDao;

public class HitCountDaoTest
{
  // -------------------------------------------------- tests init

  private IHitCountDao dao;

  @Before
  public void setUp() throws Exception
  {
    // Dao to test
    dao = IHitCountDao.getInstance();
    assertNotNull(dao);
  }

  @After
  public void tearDown() throws Exception
  {
    dao = null;
  }

  // -------------------------------------------------- tests cases

  @Test
  public void testStoreRead()
  {
    // Reset store reset
    dao.store(-1);

    // Check store
    dao.store(5);

    // Check read
    assertEquals(5, dao.read());

    // Check store
    dao.store(10);

    // Check read
    assertEquals(10, dao.read());

    // Check store less
    dao.store(7);

    // Check read
    assertEquals(10, dao.read());

    // Check store zero
    dao.store(0);

    // Check read
    assertEquals(10, dao.read());
  }
}
