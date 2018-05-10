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
    private Integer id;

    @Column(columnDefinition = "VARCHAR", nullable = false, length = nameLength)
    private String name;

    @Column(columnDefinition = "VARCHAR", nullable = false, length = colorLength)
    private String color;

    // ----------------------------------------------- constructors

    public Status()
    {
        super();
    }

    public Status(Integer id, String name, String color, int sorting)
    {
        super();
        this.id = id;
        this.name = name;
        this.color = color;
    }

    // ---------------------------------------------------- utility

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
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
        Status other = (Status) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }

    // --------------------------------------------- setters - getters

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

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
