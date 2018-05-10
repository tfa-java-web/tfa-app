package tfa.tickets.gui;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.codec.binary.Base64;

/**
 * Extension of GraphicImage JSF Component to manage image data 
 * inside html page as <img src = "data:image/png;base64,xxx-image-bytes-xxx" />
 */
@FacesComponent(value = "tfa.tickets.gui.GraphicImage", createTag = true)
public class GraphicImage extends HtmlGraphicImage
{
    @Override
    public void encodeBegin(FacesContext context) throws IOException
    {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("img", this);
        writeIdAttributeIfNecessary(writer, this);
        writer.writeAttribute("src", getBase64DataSrc(context), "value");
        writeAttributes(writer, this, collectAttributeNames());
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException
    {
        ResponseWriter writer = context.getResponseWriter();
        writer.endElement("img");
    }

    @Override
    public String getAlt()
    {
        // html5 : alt is required, even empty
        return (super.getAlt() == null ? "" : super.getAlt());
    }

    // Helpers --------------------------------------------------------------------------------------------------------

    private String getBase64DataSrc(FacesContext context) throws IOException
    {
        // value attribute
        ValueExpression value = getValueExpression("value");

        if (value == null)
            throw new IllegalArgumentException("t:graphicImage 'value' attribute is required.");

        // Eval the EL expression : must return bytes array 
        Object bytes = value.getValue(context.getELContext());
        if (!(bytes instanceof byte[]))
            throw new IllegalArgumentException("t:graphicImage 'value' type must be bytes array.");

        // img src attribute : html inline data image
        String data = Base64.encodeBase64String((byte[]) bytes);
        return "data:image/png;base64," + data;
    }

    private static Map<String, String> collectAttributeNames()
    {
        Map<String, String> attributeNames = new HashMap<>();

        // others attributes, jsf styleClass too
        for (PropertyKeys propertyKey : PropertyKeys.values())
        {
            String name = propertyKey.name();
            attributeNames.put(name, "styleClass".equals(name) ? "class" : propertyKey.toString());
        }
        return Collections.unmodifiableMap(attributeNames);
    }

    private static void writeAttributes(ResponseWriter writer, UIComponent component, Map<String, String> names) throws IOException
    {
        // put attributes into html
        for (Entry<String, String> entry : names.entrySet())
        {
            String name = entry.getKey();
            String html = entry.getValue();
            Object value = component.getAttributes().get(name);
            if (value != null)
                writer.writeAttribute(html, value, name);
        }
    }

    private static void writeIdAttributeIfNecessary(ResponseWriter writer, UIComponent component) throws IOException
    {
        // Put id attribute if necessary (useful for JSF)
        if (component.getId() != null || (component instanceof ClientBehaviorHolder && !((ClientBehaviorHolder) component).getClientBehaviors().isEmpty()))
        {
            Object value = component.getClientId();
            if (value != null)
                writer.writeAttribute("id", value, "id");
        }
    }

}
