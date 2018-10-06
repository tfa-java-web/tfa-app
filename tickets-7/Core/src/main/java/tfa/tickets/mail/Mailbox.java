package tfa.tickets.mail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.core.Configuration;

/**
 * Class to manage email reception ( pop or imap )
 */
public class Mailbox
{
    // Standard SLF4J logger
    private static Logger log = LoggerFactory.getLogger(Mailbox.class);

    // Standards port numbers
    public static final int IMAP_SSL_PORT = 993;
    public static final int POP3_SSL_PORT = 995;
    public static final int IMAP_PORT = 143;
    public static final int POP3_PORT = 110;

    // Mailbox default config
    private String mailHost;
    private int mailPort = POP3_PORT;
    private String mailAddress = null;
    private String mailPassword = null;
    private boolean imap = false;

    // Configured session
    private Session session;
    private Store store;

    /**
     * Create a mailbox session configured from application properties
     */
    public Mailbox()
    {
        this(null,null,null);
    }

    /**
     * Create a simple mailbox session or a session from application properties
     * 
     * @param mail_host
     *            or null -> from application properties
     */
    public Mailbox(String mail_host, String mail_addr, String mail_password)
    {
        super();

        // No minimal configuraion ?
        if (mail_host == null || mail_host.isEmpty())
        {
            // Read configuration from application.properties
            mailHost = Configuration.getParam("mail.pop3.host");
            if (mailHost == null || mailHost.isEmpty())
            {
                mailHost = Configuration.getParam("mail.imap.host");
                imap = true;
            }
            if (mailHost == null || mailHost.isEmpty())
                throw new IllegalArgumentException("mail POP3 ou IMAP hostname and params not defined!");

            if (!imap)
            {
                mailPort = POP3_PORT;
                if (Configuration.getParam("mail.pop3.port") != null)
                    mailPort = Configuration.getIntParam("mail.pop3.port");

                mailAddress = Configuration.getParam("mail.pop3.user");
                mailPassword = Configuration.getParam("mail.pop3.password");
                
                if ( mail_addr != null && !mail_addr.isEmpty() ) 
                {
                    if ( mailAddress != null ) log.warn( "mail.pop3.user properties overwritten!" );
                    mailAddress = mail_addr;
                    Configuration.getInstance().setParam("mail.pop3.user",mail_addr);
                }
                if ( mail_password != null && !mail_password.isEmpty() ) 
                {
                    if ( mailPassword != null ) log.warn( "mail.pop3.password properties overwritten!" );
                    mailPassword = mail_password;
                    Configuration.getInstance().setParam("mail.pop3.password",mail_password);
                }
            }
            else
            {
                mailPort = IMAP_PORT;
                if (Configuration.getParam("mail.imap.port") != null)
                    mailPort = Configuration.getIntParam("mail.imap.port");

                mailAddress = Configuration.getParam("mail.imap.user");
                mailPassword = Configuration.getParam("mail.imap.password");
                
                if ( mail_addr != null && !mail_addr.isEmpty() ) 
                {
                    if ( mailAddress != null ) log.warn( "mail.imap.user properties overwritten!" );
                    mailAddress = mail_addr;
                    Configuration.getInstance().setParam("mail.imap.user",mail_addr);
                }
                if ( mail_password != null && !mail_password.isEmpty() ) 
                {
                    if ( mailPassword != null ) log.warn( "mail.imap.password properties overwritten!" );
                    mailPassword = mail_password;
                    Configuration.getInstance().setParam("mail.imap.password",mail_password);
                }
           }

            // and other specific parameters
            init(Configuration.getProperties());
        }
        else
        {
            // Simple default config: Hostname from param, POP3 port 110, no SSL/TLS,
            this.mailHost = mail_host;
            this.mailAddress = mail_addr;
            this.mailPassword = mail_password;
            init(null);
        }
    }

    /**
     * Create the mailbox session from standard arguments
     * 
     * @param mail_host
     * @param mail_port
     * @param mail_addr
     * @param mail_password
     */
    public Mailbox(String mail_host, int mail_port, String mail_addr, String mail_password)
    {
        super();

        // Config with auth and SSL according to port number
        this.mailHost = mail_host;
        this.mailPort = mail_port;
        this.mailAddress = mail_addr;
        this.mailPassword = mail_password;
        init(null);
    }

    // Do the configuration
    private void init(Properties properties)
    {
        // Config not delivered
        if (properties == null)
        {
            properties = new Properties();

            String debug = Configuration.getParam("mail.debug");
            properties.put("mail.debug", debug != null ? debug : "false");

            if (mailPort == IMAP_PORT)
            {
                imap = true;
                properties.put("mail.store.protocol", "imap");
                properties.put("mail.imap.host", mailHost);
                properties.put("mail.imap.port", mailPort);
            }
            if (mailPort == POP3_PORT)
            {
                imap = false;
                properties.put("mail.pop3.host", mailHost);
                properties.put("mail.pop3.port", mailPort);
            }
            if (mailPort == IMAP_SSL_PORT)
            {
                imap = true;
                properties.put("mail.store.protocol", "imaps");
                properties.put("mail.imap.host", mailHost);
                properties.put("mail.imap.port", mailPort);
                properties.put("mail.imap.user", mailAddress);
                properties.put("mail.imap.socketFactory", mailPort);
                properties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }
            if (mailPort == POP3_SSL_PORT)
            {
                imap = false;
                properties.put("mail.store.protocol", "pop3s");
                properties.put("mail.pop3.host", mailHost);
                properties.put("mail.pop3.port", mailPort);
                properties.put("mail.pop3.user", mailAddress);
                properties.put("mail.pop3.socketFactory", mailPort);
                properties.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }

            // default 10s
            // properties.put("mail.imap.timeout", "10000");
            // properties.put("mail.imap.connectiontimeout", "10000");
            // properties.put("mail.pop3.timeout", "10000");
            // properties.put("mail.pop3.connectiontimeout", "10000");
        }

        // Manage Authentication
        Authenticator auth = null;
        if (mailAddress != null && !mailAddress.isEmpty())
        {
            auth = new javax.mail.Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(mailAddress, mailPassword);
                }
            };
        }
        // Configure session
        session = Session.getDefaultInstance(properties, auth);
        try
        {
            // Get mailbox store
            store = session.getStore((imap ? "imap" : "pop3"));
        }
        catch (NoSuchProviderException e)
        {
            log.error(e.getMessage());
            store = null;
        }
    }

    /**
     * Receive Email from mailbox
     * 
     * @throws MessagingException
     */
    public Email[] receive(String folderName)
    {
        // Return value : init
        Email[] emails = null;

        // Not configured
        if (store == null)
        {
            log.warn("no mailbox well configured");
            return emails;
        }

        // POP3 has fixed foldername
        if (!imap)
            folderName = "INBOX";

        int nb = 0;
        Folder folder = null;
        Message[] messages = null;
        try
        {
            // Socket connection
            store.connect();

            // Read list of messages
            folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            messages = folder.getMessages();

            // Transfer messages to local email array
            emails = new Email[messages.length];
            for (int i = 0; i < messages.length; i++)
            {
                Message message = messages[i];

                // Subject, from address
                emails[nb].setSubject(message.getSubject());
                emails[nb].setFrom(message.getFrom()[0].toString());

                // Direct "to address" list, but not cc, cci
                int j = 0;
                Address[] tab = message.getRecipients(RecipientType.TO);
                String[] to = new String[tab.length];
                for (Address r : tab)
                    to[j++] = r.toString();
                emails[nb].setTo(to);

                // Main text (may be html)
                String text = message.getContent().toString();
                emails[nb].setText(text);

                log.trace(emails[nb].toString());

                // Any attachements ?
                saveParts(message.getContent(), emails[nb]);

                nb++;

                // Machine mailbox read : Delete after it !!
                // Not very secure, a better way:
                // delete it after business treatment
                message.setFlag(Flags.Flag.DELETED, true);
            }
        }
        catch (MessagingException | IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            try
            {
                if (folder != null)
                    folder.close(true);

                if (store != null)
                    store.close();
            }
            catch (MessagingException e)
            {
                log.trace(e.getMessage());
            }
        }
        // In case of error with partial read success
        for (int j = nb; j < emails.length; j++)
            emails[j] = null;

        // Return emails
        return emails;
    }

    // Save emails-parts to files into temp directory
    private void saveParts(Object content, Email email) throws MessagingException
    {
        // has attachements ?
        if (!(content instanceof Multipart))
            return;

        Multipart multi = ((Multipart) content);

        // For each parts (one or more)
        int parts = multi.getCount();
        for (int j = 0; j < parts; j++)
        {
            try
            {
                // A part
                MimeBodyPart part = (MimeBodyPart) multi.getBodyPart(j);
                if (part.getContent() instanceof Multipart)
                {
                    // Part within a part, recursion...
                    saveParts(part.getContent(), email);
                    continue;
                }

                // filename extension
                String extension = "";
                if (part.isMimeType("text/html"))
                    extension = "html";
                else if (part.isMimeType("text/plain"))
                    extension = "txt";
                else
                    extension = part.getDataHandler().getName();

                // Extend filenames-array of email
                int idx = 0;
                String[] filenames = email.getFilenames();
                if (filenames != null)
                {
                    idx = filenames.length;
                    filenames = java.util.Arrays.copyOf(filenames, idx + 1);
                }
                else
                    filenames = new String[1];

                // Save file into /tmp with unique name
                String tmp = System.getProperty("java.io.tmpdir");
                Path filename = Paths.get(tmp, part.getContentID() + "-" + idx + "." + extension);
                Files.copy(part.getInputStream(), filename);

                // Link file to email object
                filenames[idx] = filename.toAbsolutePath().toString();
                email.setFilenames(filenames);

                log.trace("attachement: " + filenames[idx]);
            }
            catch (IOException e)
            {
                log.error(e.getMessage());
            }
        }
    }

}
