package tfa.tickets.core;

import java.util.Date;

import org.owasp.html.Handler;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlSanitizer;
import org.owasp.html.HtmlStreamRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tfa.tickets.entities.Status;
import tfa.tickets.entities.Ticket;
import tfa.tickets.entities.User;

/**
 * Utility to check input string for security (see OWASP)
 * avoid the most of injection, non coherent data, buffer overflow, ...
 */
public class InputValidator 
{
  // Standard SLF4J logger
  private static final Logger log = LoggerFactory.getLogger(InputValidator.class);

  // Owasp html sanitiser
  HtmlPolicyBuilder policyBuilder;

  // ------------------------------------------------------ singleton pattern

  private static InputValidator instance = null;

  public InputValidator()
  {
    // TODO : to adapt to my application usage later
    policyBuilder = new HtmlPolicyBuilder()
        .allowElements(
            "a", "label", "noscript", "h1", "h2", "h3", "h4", "h5", "h6",
            "p", "i", "b", "u", "strong", "em", "small", "big", "pre", "code", "input",
            "cite", "samp", "sub", "sup", "strike", "center", "blockquote",
            "hr", "br", "col", "font", "map", "span", "div", "img", "del", "mark",
            "ul", "ol", "li", "dd", "dt", "dl", "tbody", "thead", "tfoot",
            "table", "td", "th", "tr", "colgroup", "fieldset", "legend")
        .allowAttributes("dir", "checked", "href", "name", "title", "type")
        .globally()
        .allowStyling();
    
    if (instance == null)
      instance = this;
  }

  public static InputValidator getInstance()
  {
    if (instance == null)
      instance = new InputValidator();
    
    return instance;
  }

  // ------------------------------------------------------ standard validators

  public void intValid(final String o)
  {
    //ex: -123 10
    if (!o.matches("[-]?[0-9]+") || o.length() > 13)
      throw new IllegalArgumentException("Bad input for integer param");
  }

  public void identValid(final String o, final int max)
  {
    //ex:  Field8  in_progress
    if (!o.matches("[a-zA-Z0-9_]+") || o.charAt(0) <= '9' || o.length() > max)
      throw new IllegalArgumentException("Bad input for ident or name param");
  }

  // simple date YYYY-mm-dd format only
  public void dateValid(final String o)
  {
    // 2016-12-31
    if (o.length() != 10 || !o.matches("[0-9-]+") || o.charAt(0) == '-' || o.split("-").length != 3)
      throw new IllegalArgumentException("Bad input for date param");
  }

  public void asciiValid(final String o, final int max)
  {
    // only printable ascii character, without cr lf tab, accents
    if (!o.matches("[ -~]*") || o.length() > max)
      throw new IllegalArgumentException("Bad input for ascii text");
  }

  public void isoValid(final String o, final int max)
  {
    // only iso8859-1 text, with end of line
    if (!o.matches("[ -~¡-ÿ\n\r\t]*") || o.length() > max)
      throw new IllegalArgumentException("Bad input for basic-html-text");
  }

  // return escaped text
  public String textValid(final String o, final int max)
  {
    // iso8859-1 text only
    isoValid(o, max);

    // escape < and > for html/xml tags,
    String result = replaceEach(o, new String[] { "&", "<", ">", "\"" },
        new String[] { "&amp;", "&lt;", "&gt;", "&quot;" });

    // escape dangerous char or sequences " % @ \ $ & ~ ` ^ --
    result = replaceEach(result, new String[] { "#", "'", "/", "!", "%", "@", "\\\\", "\\$", "~", "`", "\\^", "--" },
        new String[] { "&#x23;", "&#x27;", "&#x2F;", "&#x21;", "&#x25;", "&#x40;", "&#x5C;", "&#x24;", "&#x7E;",
          "&#x60;", "&#x5E;", "==" });

    return result;
  }

  // return escaped line
  public String lineValid(final String o, final int max)
  {
    // as text
    String result = textValid(o, max);

    // but only one line
    if (o.contains("\n") || o.contains("\r"))
      throw new IllegalArgumentException("Bad input for text");

    // without tab
    return result.replace('\t', ' ');
  }

  public void fileValid(final String o, final String exts)
  {
    // filename and relative path (with accents, double quote, blank)
    isoValid(o, 255);

    // not path
    if (o.contains("\r") || o.contains("\n"))
      throw new IllegalArgumentException("invalid path");

    // dangerous path
    if (o.contains("..") || o.contains(".\\."))
      throw new IllegalArgumentException("no parent path");

    // absolute path forbidden
    if (o.startsWith("/") || o.startsWith("\\") || o.startsWith("~") || o.startsWith("$") || o.startsWith("%"))
      throw new IllegalArgumentException("no absolute path");

    // dangerous char in path
    if (o.contains(";") || o.contains(":") || o.contains("`") || o.contains("'") || o.contains("(") || o.contains("{")
      || o.contains("["))
      throw new IllegalArgumentException("invalid path");

    // allowed extensions (white list, the best solution)
    if (exts != null && !exts.isEmpty())
    {
      if (!endsWith(o, exts))
        throw new IllegalArgumentException("invalid extension");
    }
    else // main dangerous extensions (black list, not complete)
    {
      String black = ".exe,.bat,.cmd,.reg,.msi,.com,.sh,.sql,.inf,.jar,.py,.pl,.js,.vb,.vbs,.vbe";
      if (endsWith(o, black))
        throw new IllegalArgumentException("invalid extension");
    }
  }

  // ------------------------------------------------------------------------- app validator

  // it uses owasp lib (and then guava)
  public String htmlValid(final String o, final int max)
  {
    // only iso8859-1 text
    isoValid(o, max);

    // owasp basic html validation
    StringBuilder sb = new StringBuilder();
    HtmlStreamRenderer renderer = HtmlStreamRenderer.create(sb, new Handler<String>()
    {
      public void handle(String errorMessage)
      {
        throw new IllegalArgumentException("invalid html text");
      }
    });

    HtmlSanitizer.Policy policy = policyBuilder.build(renderer);
    HtmlSanitizer.sanitize(o, policy);
    return sb.toString();
  }

  // see generic dao
  public void orderValid(final String o)
  {
    // field+ Field- field
    if (!o.matches("[a-zA-Z0-9_]+[+-]?") || o.length() > 33)
      throw new IllegalArgumentException("Bad input for order param");
  }

  // see generic dao
  public void filterValid(final String o)
  {
    // field !Field Field>= !field% field!=
    if (!o.matches("[!]?[a-zA-Z0-9_]+[<>=%!]*") || o.length() > 35)
      throw new IllegalArgumentException("Bad input for filter param");
  }

  public void ticketValid(final Ticket t, boolean exist)
  {
    // no-existing id required
    if (!exist && t.getId() != null)
      throw new IllegalArgumentException("ticket with existing id posted " + t.getId());

    // existing id required
    if (exist && t.getId() == null) log.warn("ticket with no id put");

    // valid and escape title and description text
    t.setTitle(lineValid(t.getTitle(), Ticket.maxTitleLength));
    t.setDesc(htmlValid(t.getDesc(), Ticket.maxDescLength));

    // valid user and status if exists
    if (t.getUser() != null && !t.getUser().isEmpty()) identValid(t.getUser(), User.nameLength);
    if (t.getStatus() != null && !t.getStatus().isEmpty()) identValid(t.getStatus(), Status.nameLength);

    // set current date if not already existing
    if (t.getDate() == null) t.setDate(new Date());
  }

  // -------------------------------------------------------------------------- tool

  public String replaceEach(final String text, final String[] regex, final String[] by)
  {
    String result = text;

    if (regex.length != by.length)
      throw new IllegalArgumentException();

    for (int i = 0; i < regex.length; i++)
      result = result.replaceAll(regex[i], by[i]);

    return result;
  }

  public boolean endsWith(final String text, final String suffixes)
  {
    String[] ext = suffixes.split(",");
    for (int i = 0; i < ext.length; i++)
      if (text.endsWith(ext[i]))
        return true;

    return false;
  }

}
