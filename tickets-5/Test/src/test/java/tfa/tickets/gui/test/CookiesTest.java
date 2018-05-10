package tfa.tickets.gui.test;

import static org.junit.Assert.*;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import tfa.tickets.gui.Cookies;

@RunWith(MockitoJUnitRunner.class)
public class CookiesTest
{
  @Mock
  FacesContext context;

  @Mock
  ExternalContext econtext;
  
  // tested class
  Cookies cook;

  // simulation of browser storage
  static Map<String, Object> store = new HashMap<String, Object>();

  // Access to static protected method of JSF FacesContext
  private abstract static class ContextSetter extends FacesContext
  {
    static void set(FacesContext context)
    {
      // set the current instance of JSF Context : our mock
      setCurrentInstance(context);
    }
  }

  // Action on void mocked method addResponseCookie : to store
  private class AddCookie implements Answer<Void>
  {
    @Override
    @SuppressWarnings("rawtypes")
    public Void answer(InvocationOnMock invocation) throws Throwable
    {
      // call of void method with args
      Object[] args = invocation.getArguments();

      // mock action of void method : store cookie (name, value)
      store.put((String) args[0], new Cookie((String) args[0], (String) args[1]));

      // check expire value 
      assertTrue( 0 < (Integer)(((Map)(args[2])).get((Object)"maxAge")) );

      // check cookie path
      assertEquals( "/", (String)(((Map)(args[2])).get((Object)"path")) );
      
      // Void return
      return null;
    }
  }

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws Exception
  {
    // Object to test
    cook = new Cookies();
    
    // Mocked jsf context
    when(econtext.getRequestCookieMap()).thenReturn( store);
    when(context.getExternalContext()).thenReturn(econtext);
    
    // Set current JSF context
    ContextSetter.set(context);

    // Mock void method addResponseCookie
    AddCookie mm = new AddCookie();
    doAnswer(mm).when(econtext).addResponseCookie(any(String.class), any(String.class), any(Map.class));
  }

  @After
  public void tearDown() throws Exception
  {
    // Unset current JSF context
    ContextSetter.set(null);
    
    // keep store (static)
  }

  @Test
  public void testSetCookie()
  {
    // store empty or not (according to order of @Test execution)
    if ( !store.isEmpty() ) assertNull( store.get("name") );
    
    // test set cookie
    cook.setCookie("name", "first", 0);
    cook.setCookie("name", "xxxxx", 0);
    cook.setCookie("name2", "two,...", 0);
    
    // check storage  (special chars encoded)
    assertFalse( store.isEmpty() );
    assertEquals( "xxxxx", ((Cookie)(store.get("name"))).getValue() );
    assertEquals( "two%2C...", ((Cookie)(store.get("name2"))).getValue() );
  }

  @Test
  public void testGetCookie()
  {
    // test get cookie
    String v = cook.getCookie("name");
    
    // null or xxxxx (according to order of @Test execution)
    if (v != null) assertEquals("xxxxx", v);

    // override cookie
    cook.setCookie("name", "value", 0);
    v = cook.getCookie("name");
    assertEquals("value", v);
    
    // reset cookie to null
    cook.setCookie("name", null, 0);
    v = cook.getCookie("name");
    assertNull(v);
    
    // test get cookie
    v = cook.getCookie("name2");
    
    // null or two (according to order of @Test execution)
    if (v != null) assertEquals("two,...", v);
 }

}
