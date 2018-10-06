package tfa.tickets.base.test;

import static org.junit.Assert.*;

import java.io.File;
import javax.persistence.EntityManagerFactory;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import tfa.tickets.base.IGenericDao;

/**
 * Base class of Dao test with DBUnit, SQL H2 access
 */
public class DaoUnitTester
{
  // -------------------------------------------------- tests init

  // DBUnit
  static IDatabaseTester databaseTester;
  static IDataSet loadedDataSet;

  // default persistent unit 
  static String pu = "TicketsPUTest";
  
  @BeforeClass
  public static void setUpClass() throws Exception
  {
    // Setup jpa hibernate, build database model
    EntityManagerFactory emf = IGenericDao.init(pu);

    // DbUnit connection to the same H2 database as persistence.xml
    String url = (String) emf.getProperties().get("javax.persistence.jdbc.url");
    databaseTester = new H2JdbcDatabaseTester("org.h2.Driver", url, "sa", "sa");

    // Loading data from .csv
    loadedDataSet = new CsvDataSet(new File("./src/test/resources/dataset"));
    databaseTester.setDataSet(loadedDataSet);
    assertTrue(loadedDataSet.getTable("Status").getRowCount() > 0);
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
    // Close DbUnit
    databaseTester = null;
    loadedDataSet = null;

    // Close jpa hibernate
    IGenericDao.term();
  }

  @Before
  public void setUpBefore() throws Exception
  {
    // Reinit database with read data
    databaseTester.onSetup();
  }

  @After
  public void tearDownAfter() throws Exception
  {
    // Close the entity manager (of the thread)
    IGenericDao.lazyCloseEm();

    // Purge database
    databaseTester.onTearDown();
  }

  public static void setPersistentUnit(String pu)
  {
    DaoUnitTester.pu = pu;
  }

}
