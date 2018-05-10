package tfa.tickets.face;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;

/**
 * Bean to manage current ticket displayed
 */
@ManagedBean @ViewScoped
public class Uploader implements Serializable
{
    private static final long serialVersionUID = -2868307192971542946L;

    // Uploaded part of multipart/form-data  
    private Part file;

    // ------------ constructor

    public Uploader()
    {
        super();
    }

    public String upload()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        String error = "unknown error";
     
        if ( file == null ) 
        {
            error = "no file selected";
        }
        else 
        {
            // Open with auto closing
            try ( InputStream f = file.getInputStream() )
            {
                // filename sent
                String filename = file.getSubmittedFileName(); 
    
                // Get file content ( after uploaded ) !
                while ( f.available() > 0 ) {
                    byte[] data = new byte[1024];
                    f.read(data, 0, 1024);
                }
                
                // use the file, todo ...
                
                // Message OK
                error = filename + " loaded";
            }
            catch (IOException e)
            {  
                // Message IO exception without detail
                error = "error: IO exception";
            }
        }
        // Message without detail
        FacesMessage message = new FacesMessage( error );
        message.setSeverity(FacesMessage.SEVERITY_ERROR);
        fc.addMessage(null, message);
        
        // Keep message displayed after refresh of page
        Flash flash = fc.getExternalContext().getFlash();
        flash.setKeepMessages(true);
         
        // Discard file data
        file = null;

        // Refresh page with GET after POST 
        return "todo?faces-redirect=true";
    }

    public void validateFile(FacesContext ctx, UIComponent comp, Object value)
    {
        List<FacesMessage> msgs = new ArrayList<FacesMessage>();
        //Part file = (Part) value;
        
        // Checks at server side (after uploading), 
        // must be also checked at js side (before uploading) !
        
        //if (file.getSize() > (10*1024000) )
        //    msgs.add(new FacesMessage("file too big"));
   
        // Security , check possible extensions : white list
        //String filename = file.getSubmittedFileName(); 
        //int pos = filename.lastIndexOf(".");
        //String ext = (pos > 0 ? filename.substring(pos) : "");
        
        //if ( ! (ext.equals(".txt") || ext.equals(".xml") || ext.equals(".properties"))
        //||   ! ("text/plain".equals(file.getContentType()) || "text/xml".equals(file.getContentType())) ) 
        //    msgs.add(new FacesMessage("not a text file"));
        
        if (!msgs.isEmpty())
            throw new ValidatorException(msgs);    
    }

    // ------------ getters setters

    public Part getFile()
    {
        return file;
    }

    public void setFile(Part file)
    {
        this.file = file;
    }
}
