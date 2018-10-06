package tfa.tickets.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Implementation of REST api for Tickets objects
class UploadFile implements IUploadFile, Serializable
{
    private static final long serialVersionUID = -8158198932784761195L;

    // Standard SLF4J logger
    private static final Logger log = LoggerFactory.getLogger(UploadFile.class);

    // ------------------------------------ CRUD Rest API

    public UploadFile()
    {
        super();
    }

    public Response upload(final MultipartFormDataInput mp)
    {
        // normal status or create returned
        Status status = Response.Status.NOT_ACCEPTABLE;

        String fileName = "";
        Map<String, List<InputPart>> uploadForm = mp.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("uploadedFile");

        for (InputPart inputPart : inputParts)
        {

            try
            {

                MultivaluedMap<String, String> header = inputPart.getHeaders();
                fileName = getFileName(header);

                // convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                // check input
                if (validateFile(fileName, inputStream))
                {
                    // ok
                    status = Response.Status.ACCEPTED;
                    log.trace(" file recieved " + fileName);
                }
            }
            catch (IOException e)
            {
                log.error(e.toString());
            }
        }

        return Response.status(status).entity("uploaded").build();
    }

    private boolean validateFile(String fileName, InputStream inputStream)
    {
        return true;
    }

    private String getFileName(MultivaluedMap<String, String> header)
    {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition)
        {
            if ((filename.trim().startsWith("filename")))
            {

                String[] name = filename.split("=");

                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

}
