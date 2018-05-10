package tfa.tickets.gui.test;

import static org.junit.Assert.*;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tfa.tickets.gui.Cookies;
import tfa.tickets.gui.TablePaginate;

@RunWith(MockitoJUnitRunner.class)
public class TablePaginateTest
{
  @Mock
  private Cookies cookies;  
  private int count;
   
  // tested class
  class TablePaginateTst extends TablePaginate
  { 
    public TablePaginateTst() { super(); setCookies(cookies); loadOptions(); }
    @Override protected int getDataPage() { return count; }
    @Override protected int getDataCount() { return count; }
    @Override protected void setAsCurrent(int i) { }
  };
  
  private TablePaginateTst tp;
  
  // -------------------------------------------------- test reinit

  @Before
  public void setUp()
  {
    when(cookies.getCookie("sortField")).thenReturn(null);
    when(cookies.getCookie("sortAsc")).thenReturn(null);
    count = 21;
    tp = new TablePaginateTst();
    tp.init();
  }

  // -------------------------------------------------- test 
  @Test
  public void testInit()
  {   
    assertEquals( 10, tp.getPageSize() );
    assertEquals( 3, tp.getNbPage() );
    assertEquals( -1, tp.getIndex() );
    assertEquals( 21, tp.getCount() );
    assertEquals( 1, tp.getLastPageSize() );

    assertEquals( 2, tp.getOrder().size() );
    assertEquals( "date+", tp.getOrder().get(0));
    assertEquals( "id", tp.getOrder().get(1));
    
    when(cookies.getCookie("sortField")).thenReturn("id");
    when(cookies.getCookie("sortAsc")).thenReturn("false");
    
    tp = new TablePaginateTst();
    tp.init();
    
    assertEquals( 1, tp.getOrder().size() );
    assertEquals( "id-", tp.getOrder().get(0));
    
    when(cookies.getCookie("sortField")).thenReturn("user");
    when(cookies.getCookie("sortAsc")).thenReturn("true");
    
    tp = new TablePaginateTst();
    tp.init();
    
    assertEquals( 2, tp.getOrder().size() );
    assertEquals( "user+", tp.getOrder().get(0));
    assertEquals( "id", tp.getOrder().get(1));
  }

  @Test 
  public void testPrev()
  {
    tp.setPage(3);
    tp.setIndex(0);
    assertNull( tp.prev() );
    assertNull( tp.prev() );
    assertNull( tp.prev() );
    assertNull( tp.prev() );
    

    tp.setIndex(2);
    assertNull( tp.prev() );
  }

  @Test
  public void testNext()
  {
    assertNull( tp.next() );
    assertNull( tp.next() );
    assertNull( tp.next() );
  }

  @Test
  public void testPrevPage()
  {
    assertNull( tp.prevPage() );
  }

  @Test
  public void testNextPage()
  {
    assertNull( tp.nextPage() );
  }

  @Test
  public void testSort()
  {
    assertNull( tp.sort("id") );
    assertEquals( "id+", tp.getOrder().get(0));
    assertNull( tp.sort("id") );
    assertEquals( "id-", tp.getOrder().get(0));
    assertNull( tp.sort("id") );
    assertEquals( "id+", tp.getOrder().get(0));
    assertNull( tp.sort("id") );
    assertEquals( "id-", tp.getOrder().get(0));
    
    assertNull( tp.sort("user") );
    assertEquals( "user+", tp.getOrder().get(0));
    
    assertNull( tp.sort("status") );
    assertEquals( "status+", tp.getOrder().get(0));
    
    assertNull( tp.sort("date") );
    assertEquals( "date+", tp.getOrder().get(0));
    assertNull( tp.sort("date") );
    assertEquals( "date-", tp.getOrder().get(0));
    
    assertNull( tp.sort("id") );
    assertEquals( "id+", tp.getOrder().get(0));
  }


}
