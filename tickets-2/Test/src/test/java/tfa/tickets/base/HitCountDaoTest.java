package tfa.tickets.base;

import static org.junit.Assert.*;

import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HitCountDaoTest
{
    // -------------------------------------------------- tests init

    // Base identifier for test : H2 in memory
    final private static String DB = "mem:test-tfa";

    // Dao to test
    private HitCountDao dao;

    // Example of DBUnit management
    private static IDatabaseTester databaseTester;
    private static FlatXmlDataSet loadedDataSet;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        // To control database
        databaseTester = new H2JdbcDatabaseTester("org.h2.Driver", "jdbc:h2:" + DB, "sa", "");

        // Initial Data set ( hitCount = 8 )
        loadedDataSet = new FlatXmlDataSet(HitCountDaoTest.class.getClassLoader().getResourceAsStream("dataset.xml"));
        databaseTester.setDataSet(loadedDataSet);
    }

    @Before
    public void setUp() throws Exception
    {
        // Create object to test (create table if not exist)
        dao = new HitCountDao(DB);

        // Reinit database with dataset
        databaseTester.onSetup();
    }

    @After
    public void tearDown() throws Exception
    {
        // Reinit database with dataset
        databaseTester.onTearDown();

        // Terminate object to test
        dao.end();
    }

    // -------------------------------------------------- tests cases

    @Test
    public void testHitCountDao()
    {
        // Check constructor
        assertNotNull(dao);

        // Check second instance
        HitCountDao dao2 = new HitCountDao(DB);
        assertNotNull(dao2);

        // Check first
        assertNotNull(dao);
    }

    @Test
    public void testRead()
    {
        // Check read
        assertEquals(8, dao.read());

        // Check read by second access (one mem database)
        HitCountDao dao2 = new HitCountDao(DB);
        assertNotNull(dao2);
        assertEquals(8, dao2.read());
    }

    @Test
    public void testStore() throws Exception
    {
        // Check store
        dao.store(5);

        // Fetch database data after executing "store"
        IDataSet databaseDataSet = databaseTester.getConnection().createDataSet();
        ITable actualTable = databaseDataSet.getTable("stats");
        assertEquals(1, actualTable.getRowCount());
        assertEquals(5, actualTable.getValue(0, "hitCount"));

        // Check read by second access (one mem database)
        HitCountDao dao2 = new HitCountDao(DB);
        assertNotNull(dao2);
        assertEquals(5, dao2.read());
    }

    @Test(expected = NullPointerException.class)
    public void testEnd()
    {
        // Check end : no exception
        dao.end();
        dao.end();

        // Using after end : exception
        assertEquals(8, dao.read());
    }
}
