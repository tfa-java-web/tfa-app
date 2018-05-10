package tfa.tickets.gui;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;

/**
 * Manage cookies in current JSF http request/response
 * You can use also, JSF injection of  @RequestCookiesMap
 */
public class Cookies
{
  /**
   * Set a cookie
   * 
   * @param name
   * @param value
   * @param expiry
   */
  public void setCookie(String name, String value, int expiry)
  {
    if (expiry == 0)
      expiry = 365 * 24 * 3600; // 1 year max

    Map<String, Object> properties = new HashMap<>();
    properties.put("maxAge", expiry);
    properties.put("path", "/");

    String evalue = null;
    if (value != null)
    {
      try
      {
        // encode special characters
        evalue = URLEncoder.encode(value, "ISO-8859-1");
      }
      catch (UnsupportedEncodingException e)
      {
        evalue = "";
      }
    }
    FacesContext facesContext = FacesContext.getCurrentInstance();
    facesContext.getExternalContext().addResponseCookie(name, evalue, properties);
  }

  /**
   * Get a cookie (warning: value to be validated for security)
   * 
   * @param name
   * @return string value or null
   */
  public String getCookie(String name)
  {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    Cookie cookie = (Cookie) facesContext.getExternalContext().getRequestCookieMap().get(name);
    if (cookie == null || cookie.getValue() == null)
      return null;

    String value;
    try
    {
      // decode special characters
      value = URLDecoder.decode(cookie.getValue(), "ISO-8859-1");
    }
    catch (UnsupportedEncodingException e)
    {
      value = null;
    }
    return value;
  }
}
