package tfa.tickets.base;

/**
 * The hit count of site (visits), not managed into sql base
 */
public interface IHitCountDao
{
  /** read it from data storage */
  public int read();

  /** write it to data storage, only if greater */
  public void store(final int hc);
}
