package tfa.tickets.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example of H2 JDBC access
 */
public class HitCountDao
{
    // Standard SLF4J logger
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Database connexion
    private Connection connection = null;

    public HitCountDao()
    {
        this(null);
    }

    public HitCountDao(final String database)
    {
        super();

        // H2 Default database : mem or server, saved into files HOME_DIR/tfa.*  
        String url = "jdbc:h2:~/tfa;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";
        final String user = "sa";
        final String password = "";

        // Other base than default (for test)
        if (database != null)
            url = "jdbc:h2:" + database;

        try
        {
            // Open connection
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(url, user, password);

            // Check and init database
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS stats(hitCount INT);");
            if (!connection.createStatement().executeQuery("SELECT hitCount FROM stats").next())
                connection.createStatement().execute("INSERT INTO stats VALUES (0)");
        }
        catch (ClassNotFoundException | SQLException e)
        {
            log.error(e.toString());
        }
    }

    public int read()
    {
        int hc = 0;

        // Read an integer from H2 Dababase
        try
        {
            String query = "SELECT hitCount FROM stats";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            hc = rs.next() ? rs.getInt("hitCount") : 0;
            stmt.close();
        }
        catch (SQLException e)
        {
            log.error(e.toString());
        }
        return hc;
    }

    public void store(final int hc)
    {
        // Write an integer to H2 Dababase
        try
        {
            String update = "UPDATE stats SET hitCount = ?";
            PreparedStatement stmt = connection.prepareStatement(update);
            stmt.setInt(1, hc);
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e)
        {
            log.error(e.toString());
        }
    }

    public void end()
    {
        // Close dao
        try
        {
            if (connection != null)
                connection.close();
        }
        catch (SQLException e)
        {
        }
        connection = null;
    }

}
