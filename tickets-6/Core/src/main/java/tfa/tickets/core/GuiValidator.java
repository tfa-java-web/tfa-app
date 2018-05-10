package tfa.tickets.core;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import tfa.tickets.entities.Ticket;
import tfa.tickets.entities.User;

/**
 * Custom JSF Validator for Ticket edition based on InputValidator
 */
@FacesValidator("tfa.Ticket")
public class GuiValidator implements Validator

{
    @Override
    public void validate(FacesContext context, UIComponent component, Object oval) throws ValidatorException
    {
        // Delegate to InputValidator
        InputValidator check = InputValidator.getInstance();

        try
        {
            String id = component.getId();
            String val = (String)oval;
            switch (id)
            {
                case "title":
                    if (val.length() < 3)
                        throw new IllegalArgumentException("Title required or too short");
                    check.lineValid(val, Ticket.maxTitleLength);                  
                    break;

                case "desc":
                    if (val.length() < 3)
                        throw new IllegalArgumentException("Description required or too short");
                    String valSanitized = check.htmlValid(val, Ticket.maxDescLength);
                    if (!val.equals(valSanitized))
                    {
                        int i = 0;
                        while (i < valSanitized.length())
                        {
                            if (val.charAt(i) != valSanitized.charAt(i))
                                break;
                            i++;
                        }
                        int j = i+20;
                        if ( j > val.length() ) j = val.length();
                        throw new IllegalArgumentException("Description sanitizer error at " + val.substring(i, j));
                    }
                    break;

                case "user":
                    if (val.length() > 0)
                        check.identValid(val, User.nameLength);
                    break;

                default:
                    throw new IllegalArgumentException("unknow error");
            }
        }
        catch (RuntimeException e)
        {
            FacesMessage message = new FacesMessage(e.getMessage());
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

}
