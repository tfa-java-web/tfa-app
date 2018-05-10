package tfa.tickets.mail;

import java.io.Serializable;

/**
 * Store an email,  
 * joined parts are stored into files, in temp directory
 */
public class Email implements Serializable
{
    private static final long serialVersionUID = 1069215553271928208L;

    // Classic Data of an email
    private String subject;
    private String text;
    private String from = null;
    private String[] to = null;
    private String[] filenames = null;

    public Email(String subject, String text)
    {
        super();
        this.subject = subject;
        this.text = text;
    }

    public Email(String subject, String text, String from, String[] to)
    {
        super();
        this.subject = subject;
        this.text = text;
        this.from = from;
        this.to = to;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String[] getTo()
    {
        return to;
    }

    public void setTo(String[] to)
    {
        this.to = to;
    }

    public String[] getFilenames()
    {
        return filenames;
    }

    public void setFilenames(String[] filenames)
    {
        this.filenames = filenames;
    }

    @Override
    public String toString()
    {
        return "Email [" + subject + "]";
    }
}
