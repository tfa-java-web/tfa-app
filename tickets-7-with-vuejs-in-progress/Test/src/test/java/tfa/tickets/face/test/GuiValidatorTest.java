package tfa.tickets.face.test;

import static org.mockito.Mockito.when;

import javax.faces.component.UIComponent;
import javax.faces.validator.ValidatorException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tfa.tickets.face.GuiValidator;


@RunWith(MockitoJUnitRunner.class)
public class GuiValidatorTest
{
  private GuiValidator val = new GuiValidator();
  
  @Mock
  private UIComponent ui;

  @Test
  public void testValidate()
  {
    when(ui.getId()).thenReturn("title");
    val.validate(null, ui, "aaa");
    
    when(ui.getId()).thenReturn("desc");
    val.validate(null, ui, "bbb");
    val.validate(null, ui, "bbb ccc d eee ff \n 12345");
    
    when(ui.getId()).thenReturn("user");
    val.validate(null, ui, "");
    val.validate(null, ui, "abc");
    val.validate(null, ui, "a2345678901234567890123456789012");
  }
  
  @Test(expected=ValidatorException.class)
  public void testValidateException1()
  {
    when(ui.getId()).thenReturn("title");
    val.validate(null, ui, "ab");
  }
  
  @Test(expected=ValidatorException.class)
  public void testValidateException2()
  {
    when(ui.getId()).thenReturn("desc");
    val.validate(null, ui, "ab");
  }
  
  @Test(expected=ValidatorException.class)
  public void testValidateException3()
  {
    when(ui.getId()).thenReturn("other");
    val.validate(null, ui, "a");
  }
  
}
