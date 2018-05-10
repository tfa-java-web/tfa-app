package tfa.tickets.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused") // for java reflection on fields

/**
 * Set of pairs param-value (string) for application configuration the parameters are merged from :
 * - the default values, specified in java code here
 * - the values of application.properties file into class path
 * - the values of application.properties file into -DconfigPath=...
 * - the values of <any>.properties file loaded in addition
 */
public class Configuration
{
  // Filename for application configuration
  private static final String CONFIG_FILENAME = "application.properties";

  // Env variable name for path of application configuration
  private static final String CONFIG_PATH_OPTION = "configPath";

  // Null string place holder for property in configuration file
  private static final String NULL_PARAM = "<null>";

  // Standard SLF4J logger
  private Logger log = LoggerFactory.getLogger(Configuration.class);

  // The set of parameters
  private Properties config = new Properties();

  // ----------------------------------------- default config

  private static final String isProduction = "true";
  private static final String isDeveloppement = "false";
  private static final String webMasterMail = null;

  // ----------------------------------------- singleton (private)

  private static Configuration instance = new Configuration();

  private Configuration()
  {
    super();

    // search and read config file if exist
    load(null);
  }

  public static Configuration getInstance()
  {
    return instance;
  }

  // ----------------------------------------- read configuration

  /**
   * load properties by merging from several sources
   */
  public void load(final String filePath)
  {
    // init : java coded values
    String name = CONFIG_FILENAME;
    if (filePath != null)
      name = filePath;

    // Specified file with no filepath
    if (!(name.startsWith("/") || name.startsWith(".") || name.startsWith("\\")))
    {
      // 1st: Search config file into classpath
      InputStream is = null;
      try
      {
        is = this.getClass().getClassLoader().getResourceAsStream(name);
        if (is == null)
          throw new FileNotFoundException();
        config.load(is);
        log.info("Loaded parameters from " + this.getClass().getSimpleName() + " classpath " + name);
        is.close();
      }
      catch (FileNotFoundException e)
      {
        log.trace("No " + name + " found into " + this.getClass().getSimpleName() + " classpath");
      }
      catch (IOException e)
      {
        log.error(e.toString());
      }

      // 2nd: Search config file into -DconfigPath=...
      String path = System.getProperty(CONFIG_PATH_OPTION);
      if (path != null)
      {
        try
        {
          // Search config file from vm args path
          is = new FileInputStream(path + "/" + name);
          config.load(is);
          log.info("Loaded parameters from " + path + "/" + name);
          is.close();
        }
        catch (FileNotFoundException e)
        {
          log.trace("No " + name + " found into " + CONFIG_PATH_OPTION + "=" + path);
        }
        catch (IOException e)
        {
          log.error(e.toString());
        }
      }
    }

    // 3rd: Search config file into specified filePath (priority)
    if (filePath != null && !filePath.isEmpty())
    {
      try
      {
        // Search config file from vm args path
        InputStream is = null;
        is = new FileInputStream(filePath);
        config.load(is);
        log.info("Loaded parameters from " + filePath);
        is.close();
      }
      catch (FileNotFoundException e)
      {
        log.trace("No config found into " + filePath);
      }
      catch (IOException e)
      {
        log.error(e.toString());
      }
    }
  }

  /**
   * reset all parameters to default (java coded) values
   */
  public void reset()
  {
    log.info("Reset all parameters to default values");

    // Default config (java constant)
    config.clear();
  }

  // ----------------------------------------- generic setters getters

  /**
   * get param value, return null if not defined
   */
  public static String getParam(final String name)
  {
    if (name == null || name.isEmpty())
      return null;

    // Search into loaded config
    Properties config = getInstance().config;
    String value = config.getProperty(name);
    if (value != null || config.containsKey(name))
    {
      if (value.equals(NULL_PARAM))
        return null;

      return value;
    }
    // Search into default config
    try
    {
      Field f = getInstance().getClass().getDeclaredField(name);
      if ((f.getType() == String.class) && (f.getModifiers() == (Modifier.FINAL | Modifier.STATIC | Modifier.PRIVATE)))
        value = (String) f.get(String.class);
    }
    catch (IllegalArgumentException | IllegalAccessException | SecurityException e)
    {
      getInstance().log.error(e.toString());
    }
    catch (NoSuchFieldException e)
    {
      getInstance().log.info(e.toString());
    }
    return value;
  }

  /**
   * define param value, including null value
   */
  public void setParam(final String name, final String value)
  {
    if (name == null || name.isEmpty())
      return;

    if (value == null)
      config.setProperty(name, NULL_PARAM);
    else
      // Set value
      config.setProperty(name, value);
  }

  /**
   * reset this param to default (java coded) value
   */
  public void resetParam(final String name)
  {
    if (name == null || name.isEmpty())
      return;

    // Revert to default value
    config.remove(name);
  }

  // ----------------------------------------- predefined setters getters

  public static Boolean isProduction()
  {
    return Boolean.valueOf(getParam("isProduction"));
  }

  public static Boolean isDeveloppement()
  {
    return Boolean.valueOf(getParam("isDeveloppement"));
  }

  public static String webMasterMail()
  {
    return getParam("webMasterMail");
  }

  public void setWebMasterMail(String value)
  {
    setParam("webMasterMail", value);
  }

}
