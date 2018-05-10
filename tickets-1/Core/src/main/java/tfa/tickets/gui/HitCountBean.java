package tfa.tickets.gui;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.base.HitCountDao;

/**
 * Example of JSF Bean use to count the sessions opened on this application and display to users
 */
@ManagedBean
@SessionScoped
public class HitCountBean implements Serializable
{
    // Id because bean can be serialised
    private static final long serialVersionUID = 597655839534817135L;

    // Standard SLF4J logger
    private static final Logger log = LoggerFactory.getLogger(HitCountBean.class);

    // Persistent storage for global hit count (protected for test access)
    private static HitCountDao dao = null;

    // Global application hit count
    private static int hitCount = -1;

    // ----------------------------------------------------- count

    // @SessionScoped --> new session --> new hit
    public HitCountBean()
    {
        super();

        // hit count storage
        if (dao == null)
            dao = new HitCountDao();

        // read storage at first time
        if (hitCount < 0)
        {
            hitCount = dao.read();
            log.trace("read hitCount = " + hitCount);
        }

        // increment hit count at each new session
        hitCount++;
        log.trace("increment hitCount = " + hitCount);

        // store new count by 10 times
        if (hitCount % 10 == 0)
        {
            dao.store(hitCount);
            log.trace("store hitCount = " + hitCount);
        }
    }

    // store hitCount at application exit
    public static void end()
    {
        if (dao == null)
            return;

        // save
        dao.store(hitCount);
        log.trace("store hitCount = " + hitCount);

        // close dao
        dao.end();
    }

    // -------------------------------------------------- getters setters

    // getter
    public int getHitCount()
    {
        // displayed hit count value
        return getGlobalHitCount();
    }

    // static getter
    public static int getGlobalHitCount()
    {
        return hitCount;
    }

    // setter (for test)
    public static void setHitCount(int hitCount)
    {
        HitCountBean.hitCount = hitCount;
    }

    // setter (for test)
    public static void setDao(HitCountDao dao)
    {
        HitCountBean.dao = dao;
    }

}
