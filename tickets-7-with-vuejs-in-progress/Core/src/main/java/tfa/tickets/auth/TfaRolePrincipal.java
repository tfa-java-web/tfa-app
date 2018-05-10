package tfa.tickets.auth;

/**
 * Implement a role for JAAS
 */
public class TfaRolePrincipal extends TfaPrincipal
{
    private static final long serialVersionUID = -6188172855802126445L;

    public TfaRolePrincipal(String name)
    {
        super(name);
    }
};
