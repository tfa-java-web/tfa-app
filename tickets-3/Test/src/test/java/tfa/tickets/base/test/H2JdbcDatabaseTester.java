package tfa.tickets.base.test;

import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.h2.H2DataTypeFactory;

// Extend a DbUnit class to avoid warning on DATATYPE_FACTORY
public class H2JdbcDatabaseTester extends JdbcDatabaseTester
{
    public H2JdbcDatabaseTester(String d, String c, String u, String p) throws ClassNotFoundException
    {
        super(d, c, u, p);
    }

    @Override // for H2
    public IDatabaseConnection getConnection() throws Exception
    {
        IDatabaseConnection c = super.getConnection();
        c.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
        return c;
    }
}
