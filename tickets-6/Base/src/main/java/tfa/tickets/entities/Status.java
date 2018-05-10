package tfa.tickets.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Represents a coloured and sorted status of a ticket
 */
@Entity
public class Status implements Serializable
{
  private static final long serialVersionUID = -6966295574450936978L;

  // max lengths for strings and names
  public static final int nameLength = 32;
  private static final int colorLength = 32;

  // ---------------------------------------------- entity columns

  @Id
  @Column(columnDefinition = "VARCHAR", nullable = false, length = nameLength)
  private String name;

  @Column(columnDefinition = "VARCHAR", nullable = false, length = colorLength)
  private String color;

  // ----------------------------------------------- constructors

  public Status()
  {
    super();
  }

  public Status(String name, String color, int sorting)
  {
    super();
    this.name = name;
    this.color = color;
  }

  // ---------------------------------------------------- utility

  @Override
  public String toString()
  {
    if ( name  == null ) return null; 
    int prefix = name.indexOf(':');
    return name.substring(prefix+1);
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.toString().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;

    if (getClass() != obj.getClass()) return false;

    Status other = (Status) obj;
    if (name == null && other.name != null) return false;
    if (name != null && other.name == null) return false;

    if (!name.toString().equals(other.name.toString())) return false;
    return true;
  }

  // --------------------------------------------- setters - getters

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getColor()
  {
    return color;
  }

  public void setColor(String color)
  {
    this.color = color;
  }
}
