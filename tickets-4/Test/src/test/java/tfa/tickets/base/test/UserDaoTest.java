package tfa.tickets.base.test;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tfa.tickets.base.IUserDao;
import tfa.tickets.entities.User;

public class UserDaoTest extends DaoUnitTester
{
  // -------------------------------------------------- tests init

  private IUserDao dao;

  @Before
  public void setUpBefore() throws Exception
  {
    super.setUpBefore();

    dao = IUserDao.getInstance();
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
    List<User> l = dao.getList();
    assertTrue(l.size() > 0);
  }

}
