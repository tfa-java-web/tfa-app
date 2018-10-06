package tfa.tickets.mail.test;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.mail.MessagingException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import tfa.tickets.mail.Email;
import tfa.tickets.mail.Mailbox;
import tfa.tickets.mail.Mailer;

/**
 *  Prerequisities : POP3 Server stated at localhost , port 110 , without auth
 *                   (Like FakeSMTP+POP3)
 */
public class Mailbox_IT
{
    @BeforeClass
    public static void initMessages()
    {
        Mailer m = new Mailer( "localhost" );  
        m.setSmtpFrom( "tfa.java.web@gmail.com" );
        
        boolean ret = m.send( "tfa.java.web@localhost", 
                "Joyeux noël ~ garçons & Scrooge", 
                "Vive le vent, vive le vent d'hiver\n" +
                "Joyeux noël ~ jingle bells\nHo Hô Hö" );

        assertTrue(ret);
        
        // html format, multiple recipients
        ret = m.send( "tfa.java.web@localhost;tfa.java.web@gmail.com", 
                "Joyeux noël ~ garçons & Scrooge", 
                "<h2>This is a test</h2><p>Vive le vent, vive le vent d'hiver</p>" +
                "<hr><p>Joyeux <b>noël</b> ~ jingle bells\n<font size='200%'>Ho Hô Hö</font></p>",
                "src/test/resources/logback.xml" );

        assertTrue(ret);
    }

    @Test
    public void testReceive() throws IOException, MessagingException
    {        
        Mailbox mb = new Mailbox( "localhost", "test@localhost.loc", "test" );  
        Email[] mails = mb.receive(null);
        assertEquals( 2, mails.length );
    }

    @Test @Ignore
    public void testReceivePopSsl() throws IOException, MessagingException, InterruptedException
    {
        Mailbox mb = new Mailbox( "pop.gmail.com", Mailbox.POP3_SSL_PORT, "tfa.java.web@gmail.com", "xxxxxx" );  
        Email[] mails = mb.receive("INBOX");
        assertTrue ( mails.length > 0 );   
        Thread.sleep(2000);    
    }

    @Test @Ignore
    public void testReceiveImapSsl() throws IOException, MessagingException, InterruptedException
    {
        Mailbox mb = new Mailbox( "pop.gmail.com", Mailbox.IMAP_SSL_PORT, "tfa.java.web@gmail.com", "xxxxxx" );  
        Email[] mails = mb.receive("INBOX");
        assertTrue ( mails.length > 0 );   
        Thread.sleep(2000);    
    }

}
