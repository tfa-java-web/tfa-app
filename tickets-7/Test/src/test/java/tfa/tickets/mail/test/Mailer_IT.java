package tfa.tickets.mail.test;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import tfa.tickets.mail.Mailer;

/**
 * Prerequisities :   STMP server started at localhost, port 25, without auth
 *                    ( Like FakeSMTP )
 */
public class Mailer_IT
{

    @Test 
    public void testSend()
    {
        Mailer m = new Mailer( "localhost" );  
        m.setSmtpFrom( "tfa.java.web@gmail.com" );
        
        boolean ret = m.send( "tfa.java.web@gmail.com", 
                "Joyeux no�l ~ gar�ons & Scrooge", 
                "Vive le vent, vive le vent d'hiver\n" +
                "Joyeux no�l ~ jingle bells\nHo H� H�" );

        assertTrue(ret);
    }
    
    @Test 
    public void testSendHtml()
    {
        Mailer m = new Mailer( "localhost" );  
        m.setSmtpFrom( "tfa.java.web@gmail.com" );
        
        // html format, multiple recipients
        boolean ret = m.send( "tfa.java.web@gmail.com;tfa.java.web@gmail.com;tfa.java.web@gmail.com", 
                "Joyeux no�l ~ gar�ons & Scrooge", 
                "<h2>This is a test</h2><p>Vive le vent, vive le vent d'hiver</p>" +
                "<hr><p>Joyeux <b>no�l</b> ~ jingle bells\n<font size='200%'>Ho H� H�</font></p>" );

        assertTrue(ret);
    }
    
    @Test @Ignore  // todo Adjust email account and password
    public void testSendSSL()
    {
        Mailer m = new Mailer( "smtp.gmail.com", Mailer.SMTP_SSL_PORT, "tfa.java.web@gmail.com", "xxxxxxx" );  
        m.setSmtpFrom( "tfa.java.web@gmail.com" );
        
        boolean ret = m.send( "tfa.java.web@gmail.com", 
                "Joyeux no�l ~ gar�ons & Scrooge", 
                "Vive le vent, vive le vent d'hiver\n" +
                "Joyeux no�l ~ jingle bells\nHo H� H�" );

        assertTrue(ret);
    }

    @Test @Ignore // todo Adjust email account and password
    public void testSendTLS()
    {
        Mailer m = new Mailer( "smtp.gmail.com", Mailer.SMTP_TLS_PORT, "tfa.java.web@gmail.com", "xxxxxxx" );  
        m.setSmtpFrom( "tfa.java.web@gmail.com" );
        
        boolean ret = m.send( "tfa.java.web@gmail.com", 
                "Joyeux no�l ~ gar�ons & Scrooge", 
                "Vive le vent, vive le vent d'hiver\n" +
                "Joyeux no�l ~ jingle bells\nHo H� H�" );

        assertTrue(ret);
    }

    @Test 
    public void testSendAttach()
    {
        Mailer m = new Mailer( "localhost" );  
        m.setSmtpFrom( "tfa.java.web@gmail.com" );
        
        boolean ret = m.send( "tfa.java.web@gmail.com", 
                "Joyeux no�l ~ gar�ons & Scrooge", 
                "Vive le vent, vive le vent d'hiver\n" +
                "Joyeux no�l ~ jingle bells\nHo H� H�" , "src/test/resources/logback.xml" );

        assertTrue(ret);
    }

    @Test 
    public void testSendMultiHtml()
    {
        Mailer m = new Mailer( "localhost" );  
        m.setSmtpFrom( "tfa.java.web@gmail.com" );
        
        boolean ret = m.send( "tfa.java.web@gmail.com", 
                "Joyeux no�l ~ gar�ons & Scrooge", 
                "Vive le vent, vive le vent d'hiver\n" +
                "Joyeux no�l ~ jingle bells\nHo H� H�" , "src/test/resources/logback.xml" );

        assertTrue(ret);
        
        String[] mdest = { "tfa.java.web@gmail.com" };
        String[] mfile = { "src/test/resources/garden.jpg", "src/test/resources/application.properties" };
        
                
        ret = m.send( mdest, 
                "Joyeux no�l ~ gar�ons & Scrooge", 
                "<h2>Vive le vent, vive le vent d'hiver</h2>\n" +
                "<br/><br/>Joyeux <b>no�l</b> ~ <i style='color:red'>jingle bells</i>\n<span style='float:right'>Ho H� H�</span>" , mfile );

        assertTrue(ret);
    }

    @Test
    public void testSendApp()
    {
        // Config from application.properties
        Mailer m = new Mailer();  
        
        boolean ret = m.send( "tfa.java.web@gmail.com", 
                "Joyeux no�l ~ gar�ons & Scrooge", 
                "Vive le vent, vive le vent d'hiver\n" +
                "Joyeux no�l ~ jingle bells\nHo H� H�" );

        assertTrue(ret);
    }

}
