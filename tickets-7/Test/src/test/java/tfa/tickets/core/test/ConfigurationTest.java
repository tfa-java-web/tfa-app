package tfa.tickets.core.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import tfa.tickets.core.Configuration;

public class ConfigurationTest
{
  // -------------------------------------------------- test reinit

  @Before
  public void setUp()
  {
    // reset to defaults
    Configuration.getInstance().reset();

    // return to defaults + application.properties (core)
    Configuration.getInstance().load(null);
  }

  // -------------------------------------------------- tests cases

  @Test
  public void testGetInstance()
  {
    assertNotNull(Configuration.getInstance());
  }

  @Test
  public void testDefaultLoad()
  {
    // load from defaults + application.properties (core)
    assertNull(Configuration.getParam("test"));
    assertNull(Configuration.getParam("appli"));
    assertNull(Configuration.getParam("ext.test"));
    assertNull(Configuration.getParam("test2"));
    assertNotNull(Configuration.getParam("webMasterMail"));
    assertNotNull(Configuration.getParam("isProduction"));
    assertNotNull(Configuration.getParam("isDeveloppement"));
  }

  @Test
  public void testReset()
  {
    // reset to defaults
    Configuration.getInstance().reset();

    assertNull(Configuration.getParam("test"));
    assertNull(Configuration.getParam("webMasterMail"));
    assertTrue(Configuration.isProduction());
    assertFalse(Configuration.isDeveloppement());
  }

  @Test
  public void testGetParam()
  {
    assertNull(Configuration.getParam("test"));
    assertEquals("tfa.java.web@gmail.com", Configuration.getParam("webMasterMail"));
    assertNull(Configuration.getParam(""));
    assertNull(Configuration.getParam(null));

    // see also testLoad5
  }

  @Test
  public void testSetParam()
  {
    Configuration.getInstance().setParam("testSet", "valueSet");
    assertEquals("valueSet", Configuration.getParam("testSet"));
    Configuration.getInstance().setParam("testSet", "");
    assertEquals("", Configuration.getParam("testSet"));
    Configuration.getInstance().setParam("testSet", null);
    assertNull(Configuration.getParam("testSet"));
    Configuration.getInstance().setParam("", "valueSet");
    Configuration.getInstance().setParam(null, "valueSet");
    Configuration.getInstance().setParam("isProduction", null);
    assertNull(Configuration.getParam("isProduction"));
    assertFalse(Configuration.isProduction());
    Configuration.getInstance().setParam("testSet", " valueSet\t\ntest  ");
    assertEquals(" valueSet\t\ntest  ", Configuration.getParam("testSet"));
  }

  @Test
  public void testResetParam()
  {
    Configuration.getInstance().resetParam("webMasterMail");
    assertNull(Configuration.getParam("webMasterMail"));
    Configuration.getInstance().setParam("webMasterMail", "valueSet");
    assertNotNull(Configuration.getParam("webMasterMail"));
    Configuration.getInstance().resetParam("webMasterMail");
    assertNull(Configuration.getParam("webMasterMail"));
    Configuration.getInstance().setParam("isProduction", "false");
    assertFalse(Configuration.isProduction());
    Configuration.getInstance().resetParam("isProduction");
    assertTrue(Configuration.isProduction());
  }

  @Test
  public void testIsProduction()
  {
    assertTrue(Configuration.isProduction());
  }

  @Test
  public void testSetIsProduction()
  {
    assertTrue(Configuration.isProduction());
    Configuration.getInstance().setParam("isProduction", "False");
    assertFalse(Configuration.isProduction());

    // Note that any string (not "true") returns false
    Configuration.getInstance().setParam("isProduction", "1");
    assertFalse(Configuration.isProduction());
    Configuration.getInstance().setParam("isProduction", "vrai");
    assertFalse(Configuration.isProduction());
    Configuration.getInstance().setParam("isProduction", "");
    assertFalse(Configuration.isProduction());
  }

  @Test
  public void testIsDeveloppement()
  {
    assertFalse(Configuration.isDeveloppement());
  }

  @Test
  public void testSetIsDeveloppement()
  {
    assertFalse(Configuration.isDeveloppement());
    Configuration.getInstance().setParam("isDeveloppement", "TRUE");
    assertTrue(Configuration.isDeveloppement());

    // Note that any string (not "true") returns false
    Configuration.getInstance().setParam("isDeveloppement", "0");
    assertFalse(Configuration.isDeveloppement());
    Configuration.getInstance().setParam("isDeveloppement", "un");
    assertFalse(Configuration.isDeveloppement());
    Configuration.getInstance().setParam("isDeveloppement", "Yes");
    assertFalse(Configuration.isDeveloppement());
  }

  @Test
  public void testWebMasterMail()
  {
    assertEquals("tfa.java.web@gmail.com", Configuration.getParam("webMasterMail"));
  }

  @Test
  public void testSetWebMasterMail()
  {
    assertEquals("tfa.java.web@gmail.com", Configuration.webMasterMail());
    Configuration.getInstance().setWebMasterMail("testMail");
    assertEquals("testMail", Configuration.webMasterMail());
    Configuration.getInstance().setWebMasterMail(null);
    assertNull(Configuration.webMasterMail());
  }

  @Test
  public void testLoad1()
  {
    // return to defaults + non existant .properties
    Configuration.getInstance().reset();
    Configuration.getInstance().load("null.properties");

    assertNull(Configuration.webMasterMail());
    assertFalse(Configuration.isDeveloppement());
    assertTrue(Configuration.isProduction());
    assertNull(Configuration.getParam("test"));
  }

  @Test
  public void testLoad2()
  {
    // return to defaults + test.properties
    Configuration.getInstance().reset();
    Configuration.getInstance().load("test.properties");

    assertNull(Configuration.webMasterMail());
    assertTrue(Configuration.isDeveloppement());
    assertFalse(Configuration.isProduction());
    assertNotNull(Configuration.getParam("test"));
  }

  @Test
  public void testLoad3()
  {
    // return to defaults + application.properties (core) + test.properties
    Configuration.getInstance().load("test.properties");

    assertNotNull(Configuration.webMasterMail());
    assertTrue(Configuration.isDeveloppement());
    assertFalse(Configuration.isProduction());
    assertNotNull(Configuration.getParam("test"));
  }

  @Test
  public void testLoad4()
  {
    // return to defaults + application.properties (core) + test.properties
    Configuration.getInstance().load("./src/test/resources/test.properties");

    assertNotNull(Configuration.webMasterMail());
    assertTrue(Configuration.isDeveloppement());
    assertFalse(Configuration.isProduction());
    assertNotNull(Configuration.getParam("test"));
  }

  @Test
  public void testLoad5()
  {
    // return to defaults + application.properties (core) + test.properties + test2.properties
    Configuration.getInstance().load("./src/test/resources/test.properties");
    Configuration.getInstance().load("./src/test/resources/test2.properties");

    // Warning blanks at end of line are get !
    assertEquals("test@test.com  ", Configuration.webMasterMail());
    assertEquals("the \"'string'\" = יאח$  ", Configuration.getParam("theString"));
    assertFalse(Configuration.isDeveloppement());
    assertFalse(Configuration.isProduction());
    assertNotNull(Configuration.getParam("test"));
    assertNotNull(Configuration.getParam("test2"));
  }

  @Test
  public void testLoad6()
  {
    // return to defaults + test.properties + ext/test.properties
    Configuration.getInstance().reset();
    System.setProperty("configPath", "./src/test/resources/ext");
    Configuration.getInstance().load("test.properties");

    assertNull(Configuration.webMasterMail());
    assertTrue(Configuration.isDeveloppement());
    assertEquals("false", Configuration.getParam("test"));
    assertFalse(Configuration.isProduction());
    assertNotNull(Configuration.getParam("ext.test"));
  }

  @Test
  public void testLoad7()
  {
    // return to defaults + ext/test2.properties
    Configuration.getInstance().reset();
    System.setProperty("configPath", "./src/test/resources/ext");
    Configuration.getInstance().load("test3.properties");

    assertNull(Configuration.webMasterMail());
    assertFalse(Configuration.isDeveloppement());
    assertFalse(Configuration.isProduction());
    assertNull(Configuration.getParam("test"));
    assertNull(Configuration.getParam("test2"));
    assertNotNull(Configuration.getParam("test3"));
  }

  @Test
  public void testLoadNomal()
  {
    // This IS the Normal Usage
    // . defaults into java code (Configuration)
    // + "application.properties" into java core resources
    // + an optional external file "application.properties" with directory specified with -DconfigPath=...

    Configuration.getInstance().reset();
    System.setProperty("configPath", "./src/test/resources/ext");
    Configuration.getInstance().load(null);

    assertNotNull(Configuration.webMasterMail());
    assertFalse(Configuration.isDeveloppement());
    assertTrue(Configuration.isProduction());
  }

}
