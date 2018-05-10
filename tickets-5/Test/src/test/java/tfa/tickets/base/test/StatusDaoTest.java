package tfa.tickets.base.test;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tfa.tickets.base.IStatusDao;
import tfa.tickets.entities.Status;

public class StatusDaoTest extends DaoUnitTester
{
  // -------------------------------------------------- tests init

  private IStatusDao dao;

  @Before
  public void setUpBefore() throws Exception
  {
    super.setUpBefore();

    dao = IStatusDao.getInstance();
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
  public void testGetList()
  {
    List<Status> l = dao.getList();
    assertTrue(l.size() > 0);
  }

}
