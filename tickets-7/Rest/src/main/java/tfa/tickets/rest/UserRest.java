package tfa.tickets.rest;

import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.base.IUserDao;
import tfa.tickets.entities.User;

//Implementation of REST api for tickets-users list
class UserRest implements IUserRest, Serializable
{
    private static final long serialVersionUID = 7507094579952173936L;

    // Standard SLF4J logger
    private static final Logger log = LoggerFactory.getLogger(UserRest.class);

    // Get a dao to access base
    private IUserDao dao = IUserDao.getInstance();

    public UserRest()
    {
        super();
    }

    public List<User> list()
    {
        // REST call
        log.trace("rest call : list");

        // at least one user in base
        List<User> result = dao.getList();
        if (result == null || result.isEmpty())
            throw new NoSuchElementException("no user-list found");

        return result;
    }
}
