package tfa.tickets.gui;

import java.util.ArrayList;
import java.util.List;

import tfa.tickets.core.InputValidator;

/**
 * Paginate a table loaded page by page, with sort management.
 * Note that the table can change at every moment.
 * use cookie to memorise the table sort
 */
public abstract class TablePaginate
{
  // paginate
  private final int pageSize = 10; // data count by page
  private int nbPage = 0; // total page count
  private int page = 0; // current page index
  private int index = -1; // current data index in current page
  private int count = 0; // total data count
  private int lastPageSize = 0; // data count in last age

  // sort
  private boolean sortAsc;
  private String sortField;
  private ArrayList<String> order = new ArrayList<String>();

  // double click detection
  static private long lastTime = 0;
  static final private long doubleClickDelay = 220; // ms

  // cookies
  private Cookies cookies;

  // ------------------------------------------------ initialise

  public void init()
  {
    // first call
    if (cookies == null)
    {
      // Load sort options from cookies
      cookies = new Cookies();
      loadOptions();
    }

    // Build order criteria
    order.clear();
    order.add(sortField + (sortAsc ? "+" : "-"));

    // Second order criteria
    if (!sortField.equals("id"))
      order.add("id");

    // initialise data : first page, first index
    updateCount();
    updatePage(0, 0);

    // no current, just before zero index
    index = -1;
  }

  public void loadOptions()
  {
    try
    {
      // Sort criteria: fieldname read from cookie
      sortField = cookies.getCookie("sortField");
      if ( sortField != null && !sortField.isEmpty() ) 
        InputValidator.getInstance().identValid(sortField, 40);
      
      if (sortField == null)
        sortField = "id";

      // Sort way: boolean read from cookie
      String sa = cookies.getCookie("sortAsc");
      if ( sa != null && !sa.isEmpty() ) 
        InputValidator.getInstance().identValid(sa, 6);
      
      sortAsc = (sa == null) ? true : sa.equalsIgnoreCase("true");
    }
    catch( IllegalArgumentException e )
    {
      throw new IllegalArgumentException("Bad cookies' values : to clean");      
    }
  }
  
  // ---------------------------------------------- data interface

  // Get data for current page with sort , return count read
  abstract protected int getDataPage();

  // Get total data count
  abstract protected int getDataCount();

  // Put as current the data at index in current page
  abstract protected void setAsCurrent(int i);

  // ------------------------------------------------ data page management

  // Update nbPage
  private int updateCount()
  {
    // update total count from base
    count = getDataCount();
    if (count < 0)
      count = 0;

    // adjust the total page number
    nbPage = (count + pageSize - 1) / pageSize;

    // adjust the last page size
    lastPageSize = count % pageSize;
    if (lastPageSize == 0 && count >= pageSize)
      lastPageSize = pageSize;

    // return total count
    return count;
  }

  // Update page data and pagination
  private void updatePage(int p, int i)
  {
    // correction in case of empty table
    if (p < 0) p = 0;
    if (i < 0) i = 0;

    // wanted page and index
    setPage(p);
    setIndex(i);

    // read data of this page from database ( with order )
    int psize = getDataPage();

    // check, then adjust index and page, reload if necessary
    coherence(psize);
  }

  // ------------------------------------------------ limits management

  // page and index coherence, in case of change in database
  private void coherence(int psize)
  {
    // wanted page
    int p = page;

    // not the last page
    if (page < (nbPage - 1))
    {
      // check : list has shrunk into database
      if (psize < pageSize)
        updateCount();

      // force refresh at limit : to get possible change
      else if (page == 0 && index == 0)
        updateCount();
    }
    else // last page
    {
      // force refresh at limit : to get possible change
      if (index >= (lastPageSize - 1))
        updateCount();
    }

    // coherence page & index / nbPage, pageSize & count

    if (page >= nbPage)
      page = nbPage - 1;

    if (page < 0)
      page = 0;

    if (index >= count)
      index = count - 1;

    if (index >= pageSize)
      index = pageSize - 1;

    if (index < 0)
      index = 0;

    // reload correct page if necessary (not wanted page, or incomplete page)
    if ((page != p)
      || ((page == (nbPage - 1)) && (psize < lastPageSize))
      || ((page < (nbPage - 1)) && (psize < pageSize)))
      updatePage(page, index);

    // in theory :
    // recursive calls can loop in case of continuous database changes,
    // but there's a very little probability : not check here.
  }

  // ----------------------------------------------- list management

  public String prev()
  {
    // first of page
    if (index <= 0)
    {
      if (page <= 0)
        updatePage(0, 0); // force refresh
      else
        updatePage(page - 1, pageSize - 1); // last of previous page
    }
    // previous
    else
      index--;

    // set as current
    setAsCurrent(index);

    // same page
    return null;
  }

  public String next()
  {
    // last of page
    if (index >= pageSize - 1)
    {
      if (page >= nbPage - 1)
        updatePage(nbPage - 1, lastPageSize - 1); // force refresh
      else
        updatePage(page + 1, 0); // first of next page
    }
    // last of last page
    else if ((page >= (nbPage - 1)) && (index >= (lastPageSize - 1)))
    {
      // force refresh
      updatePage(nbPage - 1, lastPageSize - 1);
    }
    // next
    else
      index++;

    // set as current
    setAsCurrent(index);

    // same page
    return null;
  }

  // ----------------------------------------------- page management

  public String prevPage()
  {
    // is normal click
    long current = System.currentTimeMillis();
    if (current - lastTime > doubleClickDelay)
    {
      // decrement page
      if (page > 0)
        updatePage(page - 1, pageSize - 1);
      // refresh first page
      else
        updatePage(page, 0);
    }
    // is double click
    else
    {
      // goto first page
      updatePage(0, 0);
    }
    lastTime = current;

    // set as current
    setAsCurrent(index);

    // same page
    return null;
  }

  public String nextPage()
  {
    // is normal click
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastTime > doubleClickDelay)
    {
      // increment page
      if (page < (nbPage - 1))
        updatePage(page + 1, 0);
      // refresh last page
      else
        updatePage(page, lastPageSize - 1);
    }
    // is double click
    else
    {
      // goto last page
      updatePage(nbPage - 1, lastPageSize - 1);
    }
    lastTime = currentTime;

    // set as current
    setAsCurrent(index);

    // same page
    return null;
  }

  // ------------------------------------------------- sort management

  public String sort(String field)
  {
    // same : inverse sort way
    if (sortField.equals(field))
    {
      sortAsc = !sortAsc;
    }
    // change sort criteria
    else
    {
      sortField = field;
      sortAsc = true;
    }
    // reload page (refresh all count if change)
    init();

    // set as current
    setAsCurrent(index);

    // save new sort criteria
    cookies.setCookie("sortField", sortField, 0);
    cookies.setCookie("sortAsc", sortAsc ? "true" : "false", 0);

    // goto same page
    return null;
  }

  // ---------------------------------------------------- setters getters

  public int getIndex()
  {
    // Index of a row in displayed page (0 based)
    return index;
  }

  public void setIndex(int i)
  {
    this.index = i;
  }

  public String getSortField()
  {
    return sortField;
  }

  public List<String> getOrder()
  {
    return order;
  }

  public int getNbPage()
  {
    return nbPage;
  }

  public int getPage()
  {
    // Index of a page in total pages (0 based)
    return page;
  }

  public void setPage(int p)
  {
    this.page = p;
  }

  public int getPageSize()
  {
    return pageSize;
  }

  // for test
  public void setCookies(Cookies cookies)
  {
    this.cookies = cookies;
  }

  public int getCount()
  {
    return count;
  }

  public int getLastPageSize()
  {
    return lastPageSize;
  }

}
