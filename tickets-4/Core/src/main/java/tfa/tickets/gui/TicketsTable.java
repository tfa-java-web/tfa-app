package tfa.tickets.gui;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.ListDataModel;
import javax.faces.bean.ViewScoped;

import tfa.tickets.base.ITicketDao;
import tfa.tickets.entities.Ticket;

// Displayed-tickets list controller 
@ManagedBean
@ViewScoped
public class TicketsTable extends TablePaginate implements Serializable
{
  private static final long serialVersionUID = 7043723821400374311L;

  // inject current ticket detail  bean
  @ManagedProperty(value = "#{ticketDetail}")
  private TicketDetail ticketDetail;

  // data access 
  private ITicketDao dao = ITicketDao.getInstance();

  // displayed data & selected data
  private ListDataModel<Ticket> list = null;
  private HashSet<Ticket> selection = new HashSet<Ticket>();

  // current filter criteria
  Map<String, Object> filter = null;
  
  // ------------ constructor

  public TicketsTable()
  {
    // constructor
    super();

    // 1st page load
    super.init();
  }

  // ------------- data model of gui table

  @Override
  protected int getDataCount()
  {
    // retrieve tickets count from database
    return dao.count(getFilter()).intValue();
  }

  @Override
  protected int getDataPage()
  {
    // retrieve one page of data , with filter, sort
    List<Ticket> dList = dao.getList(getFilter(), getOrder(), getPage(), getPageSize());
    int size = dList.size();

    // complete page with empty tickets
    for (int i = size; i < getPageSize(); i++)
      dList.add(TicketDetail.getEmpty());

    // displayed data
    list = new ListDataModel<Ticket>(dList);

    // significant page size
    return size;
  }

  // ------------ current management

  public String click()
  {
    // retrieve ticket under mouse click
    setIndex(list.getRowIndex());

    // set as current in detail panel
    ticketDetail.setCurrent(list.getRowData());

    // goto same page
    return null;
  }

  @Override
  protected void setAsCurrent(int index)
  {
    if (index < 0 || index >= list.getRowCount())
    {
      // none current
      list.setRowIndex(-1);
      ticketDetail.setCurrent(null);
    }
    else
    {
      // select this index
      list.setRowIndex(index);

      // set as current in detail panel
      ticketDetail.setCurrent(list.getRowData());
    }
  }

  // ------------ selection management

  public void setSelected(boolean s)
  {
    // get current ticket
    Ticket t = list.getRowData();

    // empty is not selectable
    if ( t == TicketDetail.getEmpty() ) 
      return;
    
    // set as selected (or invert) into selection list
    if (s)
      selection.add(t);
    else
      selection.remove(t);
  }

  public boolean isSelected()
  {
    // get current ticket
    Ticket t = list.getRowData();

    // is selected
    return selection.contains(t);
  }

  // ------------ getters setters

  public HashSet<Ticket> getSelection()
  {
    return selection;
  }

  public ListDataModel<Ticket> getList()
  {
    return list;
  }

  public TicketDetail getTicketDetail()
  {
    return ticketDetail;
  }

  public void setTicketDetail(TicketDetail ticketDetail)
  {
    this.ticketDetail = ticketDetail;
  }

  private Map<String, Object> getFilter()
  {
    return filter;
  }

  public void setFilter(Map<String, Object> filter)
  {
    this.filter = filter;
  }
}
