package tfa.tickets.gui.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tfa.tickets.base.IHitCountDao;
import tfa.tickets.gui.HitCountBean;

// Example of test using mockito
@RunWith(MockitoJUnitRunner.class)
public class HitCountBeanTest
{
  // -------------------------------------------------- tests init

  @Mock // storage
  IHitCountDao mok;

  // Object to test
  HitCountBean hcb;

  @Before
  public void setUp() throws Exception
  {
    // mock storage
    when(mok.read()).thenReturn(5);

    // Reset static fields
    HitCountBean.setDao(mok);
    HitCountBean.setHitCount(-1);

    // Test new session
    hcb = new HitCountBean();
  }

  @After
  public void tearDown() throws Exception
  {
    hcb = null;
  }

  // -------------------------------------------------- tests cases

  @Test
  public void testHitCountBean()
  {
    // Check read at 1st construction
    assertNotNull(hcb);
    verify(mok, times(1)).read();
    verify(mok, never()).store(anyInt());
  }

  @Test
  public void testEnd()
  {
    // Check store
    HitCountBean.end();
    verify(mok, times(1)).store(anyInt());
    verify(mok, times(1)).store(6);
  }

  @Test
  public void testGetHitCount()
  {
    // Check increment
    assertEquals(6, hcb.getHitCount());

    // Test new session
    @SuppressWarnings("unused")
    HitCountBean hcb2 = new HitCountBean();
    hcb2 = null;

    // Test new session
    @SuppressWarnings("unused")
    HitCountBean hcb3 = new HitCountBean();

    // Check increment
    assertEquals(8, hcb.getHitCount());
  }

}
