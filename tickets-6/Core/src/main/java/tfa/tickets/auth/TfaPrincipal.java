package tfa.tickets.auth;

import java.io.Serializable;
import java.security.Principal;

/**
 * Implement a user or role for JAAS
 */
public class TfaPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = -6370773117621370973L;

    private String name;

    private TfaGroupPrincipal roles;
    
    
    public TfaPrincipal(final String name)
    {
        if (name == null)
            throw new NullPointerException("illegal null input");

        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return name;
    }

    public boolean equals(Object o)
    {
        if (o == null)
            return false;

        if (this == o)
            return true;

        if (!(o instanceof TfaPrincipal))
            return false;
        
        TfaPrincipal that = (TfaPrincipal) o;

        if (this.getName().equals(that.getName()))
            return true;
        
        return false;
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public TfaGroupPrincipal getRoles()
    {
        return roles;
    }

    public void setRoles(TfaGroupPrincipal group)
    {
        this.roles  = group;
    }
}
