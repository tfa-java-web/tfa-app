package tfa.tickets.mail;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.core.Configuration;

/**
 * Class to manage simple email sending
 */
public class Mailer
{
    // Standard SLF4J logger
    private static Logger log = LoggerFactory.getLogger(Mailer.class);

    public static final int SMTP_SSL_PORT = 465;
    public static final int SMTP_TLS_PORT = 587; 
    public static final int SMTP_PORT = 25; 
   
    // Mail smtp config
    private String smtpHost;
    private int smtpPort = SMTP_PORT;
    private String smtpUser = null;
    private String smtpPassword = null;
    private String smtpFrom = "noreply@noreply.info";

    // Configured session
    private Session session;
    
    /**
     * Create a SMTP session configured from application properties
     */
    public Mailer()
    {
        this(null);
    }
    
    /**
     * Create a simple SMTP session or a session from application properties
     * @param smtp_host  or null -> from application properties
     */
    public Mailer(String smtp_host)
    {
        super();
        
        // No minimal configuraion ?
        if ( smtp_host == null || smtp_host.isEmpty() ) 
        {
            // Read configuration from application.properties
            smtpHost = Configuration.getParam("mail.smtp.host");
            if ( smtpHost == null || smtpHost.isEmpty() )
                throw new IllegalArgumentException("SMTP hostname and params not defined!");
            
            if ( Configuration.getParam("mail.smtp.port") != null )
                smtpPort = Configuration.getIntParam("mail.smtp.port");
            
            smtpUser = Configuration.getParam("mail.smtp.user");
            smtpPassword = Configuration.getParam("mail.smtp.password");
            
            if ( Configuration.getParam("mail.smtp.port") != null )
                smtpFrom = Configuration.getParam("mail.smtp.from");
            
            // and other specific parameters
            init( Configuration.getProperties() );   
        }
        else
        {
            // Simple default config: port 25, no SSL/TLS 
            this.smtpHost = smtp_host;        
            init( null );
        }
    }

    /**
     * Create the SMTP session from standard arguments
     * @param smtp_host
     * @param smtp_port
     * @param smtp_user
     * @param smtp_password
     */
    public Mailer(String smtp_host, int smtp_port, String smtp_user, String smtp_password)
    {
        super();
        
        // Config with auth and SSL/TLS according to port number
        this.smtpHost = smtp_host;
        this.smtpPort = smtp_port;
        this.smtpUser = smtp_user;
        this.smtpPassword = smtp_password;
        init( null );
    }
    
    // Do the configuration
    private void init( Properties properties )
    { 
        // Config not delivered
        if ( properties == null )
        {
            properties = new Properties();
        
            properties.put("mail.smtp.host", smtpHost);
            properties.put("mail.smtp.port", smtpPort);
            properties.put("mail.smtp.from", smtpFrom);
    
            // properties.put("mail.debug", "true");
            
            if (smtpPort == SMTP_SSL_PORT) 
            {
                properties.put("mail.smtp.socketFactory.port", smtpPort);
                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }
            if (smtpPort == SMTP_TLS_PORT) 
            {
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.smtp.ssl.trust", smtpHost);
            }
    
            // properties.put("mail.smtp.timeout", "3000");
            // properties.put("mail.smtp.connectiontimeout", "3000");
        }
        
        Authenticator auth = null;
        if (smtpUser != null && !smtpUser.isEmpty())
        {
            properties.put("mail.smtp.auth", "true");
            auth = new javax.mail.Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(smtpUser, smtpPassword);
                }
            };
        }
        // Configure session
        session = Session.getDefaultInstance(properties, auth);
    }

    /**
     * Additional configuration
     * @param smtp_from email address
     */
    public void setSmtpFrom(String smtp_from)
    {
        this.smtpFrom = smtp_from;
        if ( session == null ) return; 
        session.getProperties().setProperty("mail.smtp.from", smtp_from);
    }
 
    /**
     * Send simple text or html message
     * @param dest
     * @param title
     * @param text or html text
     * @return boolean
     */
    public boolean send(String dest, String title, String text )
    {
        String[] mdest = dest.split(";");
        return send(mdest, title, text, (String[]) null );  
    }
    
    /**
     * Send simple text or html message + an attachements 
     * @param dest
     * @param title
     * @param text or html text
     * @param filename
     * @return boolean
     */
    public boolean send(String dest, String title, String text, String filename )
    {
        if ( filename == null || filename.isEmpty() ) 
            return send(dest, title, text);
        
        String[] mdest = dest.split(";");       
        String[] filenames = filename.split(File.pathSeparator);
        return send(mdest, title, text, filenames );          
    }
    
    /**
     * Send email object
     * @param email
     * @return boolean
     */
    public boolean send( Email email )
    {
        return send( email.getTo(), email.getSubject(), email.getText(), email.getFilenames() );
    }
    
    /**
     * Send simple text or html message + severals attachements
     * @param dest
     * @param title
     * @param text or html text
     * @param filenames
     * @return boolean
     */
    public boolean send(String[] dest, String title, String text, String[] filenames )
    {
        if ( session == null ) 
            return false; 
        
        try
        {
            Message message = new MimeMessage(session);

            int i = 0;
            InternetAddress[] internetAddresses = new InternetAddress[dest.length];
            for (String d : dest)
                internetAddresses[i++] = new InternetAddress(d);

            message.setRecipients(Message.RecipientType.TO, internetAddresses);
            message.setSubject(title);
      
            if ( filenames != null && filenames.length > 0 )
                setDescriptionWithAttachements( message, text, filenames );
            else
                setDescription( message, text );
            
            message.setHeader("X-Mailer", "Tfa Java Mailer");
            message.setSentDate(new Date());

            Transport.send(message, message.getAllRecipients());

            return true;
        }
        catch (AddressException e)
        {
            log.error(e.getMessage());
        }
        catch (MessagingException e)
        {
            log.error(e.getMessage());
        }
        return false;

    }

    // text or html
    private void setDescription( Part message, String text ) throws MessagingException
    {
        // One "end of html tag" found ? 
        Pattern pattern = Pattern.compile("<\\/\\w+\\s*>|\\/>|<\\w+\\s*>");
        if ( pattern.matcher(text).find() )
            message.setContent(text, "text/html"); 
        else
            message.setText(text);
    }
    
    // text + attachements
    private void setDescriptionWithAttachements( Part message, String text, String[] filenames ) throws MessagingException
    {         
        Multipart multipart = new MimeMultipart();
        BodyPart messagePart = new MimeBodyPart();          
        setDescription( messagePart, text );
        multipart.addBodyPart(messagePart);

        for ( String filename : filenames )
        {
            if ( filename == null || filename.isEmpty() ) continue;
            
            messagePart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename);
            messagePart.setDataHandler(new DataHandler(source));
            messagePart.setFileName( Paths.get(filename).getFileName().toString() );
            multipart.addBodyPart(messagePart);
        }
        message.setContent(multipart);
    } 
}
