package tfa.tickets.entities.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tfa.tickets.entities.Ticket;

public class TicketTest
{
  // -------------------------------------------------- tests init

  private Ticket t;
  
  @Before
  public void setUp() throws Exception
  {
    t = new Ticket("An example of trace", 
        "18:08:24.984 INFO  [org.eclipse.jetty.server.Server] (main) jetty-9.3.6.v20151106\n"
       +"18:08:28.203 INFO  [tfa.tickets.gui.Initialize] (main) contextInitialized start\n"
       +"18:08:28.221 INFO  [tfa.tickets.core.Configuration] (main) Loaded parameters from Configuration classpath application.properties\n"
       +"18:08:29.859 INFO  [tfa.tickets.gui.Initialize] (main) contextInitialized end\n"
       +"18:08:31.121 INFO  [org.eclipse.jetty.server.Server] (main) Started @8489ms\n" );
  
    assertNotNull( t );
  }

  @After
  public void tearDown() throws Exception
  {
    t = null;
  }

  // -------------------------------------------------- tests cases

  @Test
  public void testShortDesc()
  {
    assertTrue( t.getShortDesc().length() <= Ticket.maxShortDescLength );
    assertTrue( t.getDesc().startsWith(t.getShortDesc().substring(0,20)) );
  }
  
  @Test
  public void testHashCode()
  {
    assertTrue( t.hashCode() == 0 );
    t.setId( 123 );     
    assertTrue( t.hashCode() == t.getId() );
  }
  
  @Test
  public void testEquals()
  {
    t.setId( 123 );     
    Ticket t2 = new Ticket("An example of trace", "" );
    assertFalse( t.equals(t2) );
    t2.setId( 123 );
    assertTrue( t.equals(t2) );
    t.setTitle( "other title" );
    assertTrue( t.equals(t2) );
    t2.setId( 456 );
    assertFalse( t.equals(t2) );
    t.setTitle( "An example of trace" );
    assertFalse( t.equals(t2) );
  }
  
  @Test
  public void testToString()
  {
    System.out.println( t.toString() );
    t.setId( 123 );     
    System.out.println( t.toString() );
  }

}
