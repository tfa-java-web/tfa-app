package tfa.tickets.core.test;

import static org.junit.Assert.*;

import org.junit.Test;

import tfa.tickets.core.InputValidator;
import tfa.tickets.entities.Ticket;

public class InputValidatorTest
{
  private InputValidator iv = InputValidator.getInstance();
  
  @Test
  public void testIntValid()
  {
    iv.intValid("0");
    iv.intValid("123456");
    iv.intValid("-123");
    try { iv.intValid("'-123'"); fail(); } catch (Exception e ){};
    try { iv.intValid("-123aa"); fail(); } catch (Exception e ){};
    try { iv.intValid("-0.123e+12"); fail(); } catch (Exception e ){};
  }

  @Test
  public void testIdentValid()
  {
    iv.identValid("a",20);
    iv.identValid("Azerty_32",20);
    try { iv.identValid("$HOME",20); fail(); } catch (Exception e ){};
    try { iv.identValid("'John Doe'",20); fail(); } catch (Exception e ){};
    try { iv.identValid("a12345678901234567890123456789",20); fail(); } catch (Exception e ){};
  }

  @Test
  public void testDateValid()
  {
    iv.dateValid("2016-12-31");
    try { iv.dateValid("\"2016-12-31\""); fail(); } catch (Exception e ){};
    try { iv.dateValid("Oct,1 2016"); fail(); } catch (Exception e ){};
    try { iv.dateValid("2016-10-3"); fail(); } catch (Exception e ){};
  }

  @Test
  public void testOrderCheck()
  {
    iv.orderValid("Field+");
    iv.orderValid("Field-");
    iv.orderValid("Field_0");
    try { iv.orderValid("' AND TRUE AND '"); fail(); } catch (Exception e ){};
    try { iv.orderValid("!123"); fail(); } catch (Exception e ){};
    try { iv.orderValid("\\123"); fail(); } catch (Exception e ){};
  }

  @Test
  public void testFilterValid()
  {
    iv.filterValid("!Field%");
    iv.filterValid("Field_4>=");
    iv.filterValid("Field<");
    iv.filterValid("Field");
    iv.filterValid("!Field");
    iv.filterValid("Field!=");
    try { iv.orderValid("' AND TRUE AND '"); fail(); } catch (Exception e ){};
    try { iv.orderValid("#!/bin/bash"); fail(); } catch (Exception e ){};
    try { iv.orderValid("\\123"); fail(); } catch (Exception e ){};
    try { iv.orderValid("a123456789012345678901234567890123456789"); fail(); } catch (Exception e ){};
  }

  @Test
  public void testAsciiValid()
  {
    iv.asciiValid("~\"'(",32);
    try { iv.asciiValid("\n\r",32); fail();  } catch (Exception e ){};
    try { iv.asciiValid("יחא",32); fail(); } catch (Exception e ){};
  }
  
  @Test
  public void testIsoValid()
  {
    iv.isoValid("~\"'(&י\"'(טחש^ח",32);
    try { iv.isoValid("\f",32); fail(); } catch (Exception e ){};
  }
  
  @Test
  public void testTextValid()
  {
    iv.textValid("blah blah  ~\"'( \n\r...",32);
    iv.textValid("blah blah <a>bold</a>",32);
    try { iv.textValid("a1234567890\n1234567890\n1234567890\t123456789\n",32); fail(); } catch (Exception e ){};
    
    String s = iv.textValid("&<>\"'/#!%@\\$~`^--",32);
    assertEquals( "&amp;&lt;&gt;&quot;&#x27;&#x2F;&#x23;&#x21;&#x25;&#x40;&#x5C;&#x24;&#x7E;&#x60;&#x5E;==", s );
    
    s = iv.textValid("blah & blah <pre>the text \t this \n\r a page </pre>",132);
    assertEquals( "blah &amp; blah &lt;pre&gt;the text \t this \n\r a page &lt;&#x2F;pre&gt;", s );
  }
  
  @Test
  public void testLineValid()
  {
    iv.lineValid("blah blah & < < > ייטט  ~\"'(",32);
    try { iv.lineValid("blah blah <a>bold</a>\n\r secondline",64); fail(); } catch (Exception e ){};
    try { iv.lineValid("a123456789012345678901234567890123456789",32); fail(); } catch (Exception e ){};
  }
  
  @Test
  public void testFileValid()
  {
    // valid relative path
    iv.fileValid("\"a file.txt\"", null);
    iv.fileValid("./the/diectory", null);
    iv.fileValid("a\\win\\dir", null);
  
    // extensions checks
    iv.fileValid("dir.dat/file.png", ".jpg,.png");
    try { iv.fileValid("file.zip", ".jpg,.png"); fail(); } catch (Exception e ){};
    try { iv.fileValid("file.bat", null); fail(); } catch (Exception e ){};
    
    // invalid
    try { iv.fileValid("\n\r", null); fail(); } catch (Exception e ){};
    try { iv.fileValid("a;b;c", null); fail(); } catch (Exception e ){};
    
    // absolute, parent : invalid
    try { iv.fileValid("aaa/../../bbb", null); fail(); } catch (Exception e ){};
    try { iv.fileValid("/bin", null); fail(); } catch (Exception e ){};
    try { iv.fileValid("~/.bashrc", null); fail(); } catch (Exception e ){};
    try { iv.fileValid("$HOME/test", null); fail(); } catch (Exception e ){};
    try { iv.fileValid("%HOME%\\test", null); fail(); } catch (Exception e ){};
    try { iv.fileValid("D:\\test", null); fail(); } catch (Exception e ){};
  }
  
  @Test
  public void testBasicHtmlValid()
  {
    // simple
    String s = iv.htmlValid("blah blah <b>bold</b>",32);
    assertEquals( s, "blah blah <b>bold</b>" );
   
    // complex
    String h = "<div class='container' style='margin-top: 50px; margin-bottom: 10px;'>"
      +"<div class='row'>"
      + "<div class='col-md-12'>"
      + "  <h1>"
      + "  Sources - Step 1 : Structure"
      + "  <a class='btn btn-default btn-md' href='/bin/tfa-src-1.zip'>"
      + "     <span class='glyphicon glyphicon-save'> [src]</span>"
      + "  </a>"
      + "  <a style='float: right' class='btn btn-default btn-sm' href='step-2.xhtml' role='button'> Step 2 </a>"
      + "  </h1>"
      + "  <p>Theses sources files implement the structure, the skeleton of the web application. All is put in place"
      + "  to build, debug and launch a jsf web application. Some example classes have been created to show an hello"
      + "  word page, with an hit counter computed and displayed by JSF bean, and with a REST call example. </p>"
      + "  <a href='#modules'>Maven modules</a>"
      + "</div></div>";
    
    // 1st pass : sanitise,  eliminate class
    s = iv.htmlValid(h,1024);
    assertNotEquals( s, h );
    assertFalse( s.contains("class=") );
    
    // 2nd pass : no change, already sanitised
    String s2 = iv.htmlValid(s,1024);
    assertEquals( s, s2 );
    
    // iframe forbidden tab
    try { iv.htmlValid("blah <iframe style='aaa'>azerty</iframe>",32); fail(); } catch (Exception e ){};
  }
  
  @Test
  public void testTicketValid()
  {
    Ticket t = new Ticket("aaa","bbb");
    // iv.ticketValid(t,true);  // only a warn log
    iv.ticketValid(t,false);
    
    t = new Ticket("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789","bbb");
    try { iv.ticketValid(t,false); fail(); } catch (Exception e ){};
    
    t = new Ticket("01234567890","<a href='xxx'/>");
    iv.ticketValid(t,false);
    
    t.setStatus("י+א");
    try { iv.ticketValid(t,false); fail(); } catch (Exception e ){};
  }

}
