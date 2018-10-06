package tfa.tickets.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Represents a request title-description in a ticket management tool
 * with an unique id, a status , a last modification date, an assign person
 * 
 * Note that there is no relation between Ticket and Status & User tables (no foreign key)
 * (the relation exists in application but is not saved into the database)
 * 
 * Note that status is without prefix (Status.toString()) in the ticket status-field.
 */
@Entity
public class Ticket implements Serializable
{
  private static final long serialVersionUID = -5631121257979264478L;

  // max lengths for strings and names
  public final static int maxTitleLength = 80;
  public final static int maxDescLength = 4096;
  public final static int maxShortDescLength = 160;

  // ---------------------------------------------- entity columns

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(columnDefinition = "VARCHAR", nullable = false, length = maxTitleLength)
  private String title;

  @Column(columnDefinition = "VARCHAR", nullable = true, length = maxDescLength)
  private String desc;

  @Column(columnDefinition = "VARCHAR", nullable = true, length = Status.nameLength)
  private String status;

  @Column(columnDefinition = "VARCHAR", nullable = true, length = User.nameLength)
  private String user;

  @Column(nullable = false)
  @Temporal(TemporalType.DATE)
  private Date date;

  // ----------------------------------------------- constructors

  public Ticket()
  {
    super();
  }

  public Ticket(String title, String desc)
  {
    super();
    this.title = title;
    this.desc = desc;
    this.status = "new"; // default status
    this.user = ""; // default no user name
    this.date = new Date(System.currentTimeMillis());
  }

  // ------------------------------------------------ Setters Getters

  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getDesc()
  {
    return desc;
  }

  @JsonIgnore @Transient
  public String getShortDesc()
  {
    if (desc == null)
      return null;

    // Truncate description
    String result = desc;
    if (result.length() > maxShortDescLength - 3)
      result = result.substring(0, maxShortDescLength - 3) + "...";

    // Change to Single line (no eol, neither form feed, tab)
    result = result.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\f', ' ');
    return result;
  }

  public void setDesc(String desc)
  {
    this.desc = desc;
  }

  public Date getDate()
  {
    return date;
  }

  public void setDate(Date date)
  {
    this.date = date;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }

  public String getUser()
  {
    return user;
  }

  public void setUser(String user)
  {
    this.user = user;
  }

  // ---------------------------------------------------- compare utility

  @Override
  public int hashCode()
  {
    // id is supposed to be unique
    return (id == null) ? 0 : id.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;

    if (obj == null)
      return false;

    if (getClass() != obj.getClass())
      return false;

    Ticket other = (Ticket) obj;
    if (id == null || other.id == null)
      return false;

    // Equals if id equality
    return id.equals(other.id);
  }

  @Override
  public String toString()
  {
    return "Ticket [id=" + id + "]";
  }

}
