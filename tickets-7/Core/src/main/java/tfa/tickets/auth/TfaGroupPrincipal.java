package tfa.tickets.auth;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Implement a group of users or roles for JAAS
 */
public class TfaGroupPrincipal implements Group, Serializable
{
    private static final long serialVersionUID = -6370773117621370973L;

    private String name;
    
    Hashtable<String,Principal> members = new Hashtable<String,Principal>();
    
    public TfaGroupPrincipal(final String name)
    {
        if (name == null)
            throw new NullPointerException("illegal null input");
        
        this.name = name;
    }
    
    @Override
    public boolean addMember(Principal user)
    {
        if ( members.get(user.getName()) != null ) return false;
        members.put(user.getName(),user);
        return true;
    }

    @Override
    public boolean removeMember(Principal user)
    {
        if ( members.get(user.getName()) == null ) return false;
        members.remove(user.getName());
        return false;
    }

    @Override
    public boolean isMember(Principal member)
    {
        return members.containsKey(member.getName());
    }

    @Override
    public Enumeration<? extends Principal> members()
    {
        return members.elements();
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

        if (!(o instanceof TfaGroupPrincipal))
            return false;
        
        TfaGroupPrincipal that = (TfaGroupPrincipal) o;

        if (this.getName().equals(that.getName()))
            return true;
        
        return false;
    }

    public int hashCode()
    {
        return name.hashCode();
    }
}
