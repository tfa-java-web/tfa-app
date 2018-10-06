package tfa.tickets.auth;

/**
 * Implement a user for JAAS
 */
public class TfaUserPrincipal extends TfaPrincipal
{
    private static final long serialVersionUID = -4529639197161362356L;

    public TfaUserPrincipal(String name)
    {
        super(name);
    }
};
