package tfa.tickets.face.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;

import net.sourceforge.jwebunit.htmlunit.HtmlUnitTestingEngineImpl;
import net.sourceforge.jwebunit.junit.JWebUnit;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

// Example of integration test using JWebUnit
public class WebHome_IT
{
  // -------------------------------------------------- tests init

    static final String ROOT_URL = "http://localhost:8080/tickets";

    private static WebClient wc = null;

    // For JS management with JWebUnit
    @SuppressWarnings("unused")
    private void waitJs()
    {
        // For some JS frameworks, wait max 3s for async javascript
        wc.waitForBackgroundJavaScript(3000);
    }

    @BeforeClass
    public static void setUpBefore() throws InterruptedException
    {
        // Precond : the application must be deployed and launched
        JWebUnit.setBaseUrl(ROOT_URL);
        setScriptingEnabled(true); // javascript on !

        HtmlUnitTestingEngineImpl engine = (HtmlUnitTestingEngineImpl) getTestingEngine();
        engine.setDefaultBrowserVersion(BrowserVersion.FIREFOX_38);

        // Open the welcome page ( try during 4*(2+4) = 24 seconds )
        for (int nb = 0;;)
        {
            try
            {
                // Try to connect ( timeout 2 s)
                beginAt("/rest/auth");
                break;
            }
            catch (Exception e)
            {
                if (!e.getMessage().contains("Connection refused") || nb++ >= 4)
                    throw e;

                // retry after 4 s
                Thread.sleep(4000);
            }
        }

        // Client connection : first call
        wc = engine.getWebClient();
        wc.setCssErrorHandler(new SilentCssErrorHandler());
        wc.waitForBackgroundJavaScript(1000);
    }

    @AfterClass
    public static void tearDownAfter() throws Exception
    {
        // Close
        wc = null;
        closeBrowser();
    }

    // -------------------------------------------------- tests cases

    @Test
    public void testFirstPage()
    {
        // ensure logout
        gotoPage("/auth/logout.xhtml");
        
        // Check login page raised 
        gotoPage("");
        assertButtonPresentWithText("Log in");
        
        // Login as guest
        JWebUnit.setTextField("login:username", "guest");
        // Cannot use setTextField due to no name on this element to preserve secret to be sent as clear on network!
        JWebUnit.getElementById("login:secret").setAttribute("value", "guest"); 
        // Put the magic captcha value for test (passkey)
        JWebUnit.setTextField("login:captcha", "mC@ptCh@");
        // Validate login form
        JWebUnit.clickButtonWithText("Log in");
       
        // We are logged, if already is good 
        
        // Check home page and login name
        assertTextPresent("tfa.onmypc.net");
        assertTextPresent("guest");

        // Check link to rest call
        JWebUnit.clickLinkWithText("Help");
        assertLinkPresentWithText("/health");
        JWebUnit.clickLinkWithText("/health");
        assertTextPresent("OK");

        // assertEquals( "xxx", getElementTextByXPath("//table[@id='2']/tbody/tr[3]/td[4]") );
        
        // logout
        gotoPage("/auth/logout.xhtml");
    }

}
