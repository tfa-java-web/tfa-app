package tfa.tickets.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.base.IHitCountDao;

/**
 * Read/Store the hit count of site (visits) into a text file
 * ./hitCount.txt  (in the application current directory)
 */
class HitCountDao implements IHitCountDao
{
  // File to store hit count
  private final static Path file = Paths.get("./hitCount.txt");

  // Standard SLF4J logger
  private final static Logger log = LoggerFactory.getLogger(HitCountDao.class);

  // Read from file
  public int read()
  {
    int hc = 0;
    try
    {
      // Read all bytes fron file
      String line = new String(Files.readAllBytes(file));
      
      // Take care of special chars to avoid numeric error
      line = line.replaceAll("\n", "").replaceAll("\r", "");
      line = line.replaceAll("\t", "").replaceAll(" ", "");
      
      // Get int from read string 
      hc = Integer.parseInt(line);
    }
    catch (IOException e)
    {
      log.error(e.toString());
    }
    return hc;
  }

  // Store into the file (reset the file if exist, else create)
  public void store(final int hc)
  {
    try
    {
      if( hc < 0 ) 
      {
        // reset case
        Files.deleteIfExists( file );
        return;
      }
      
      // New count as string bytes
      byte[] buf = Integer.toString(hc).getBytes();
      
      // Store only if previous count into file is lesser than new one (hc)
      if ( (!Files.exists(file)) || ((read() < hc)  && (hc > 0)) )
        Files.write(file, buf, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    catch (IOException e)
    {
      log.error(e.toString());
    }
  }
}
