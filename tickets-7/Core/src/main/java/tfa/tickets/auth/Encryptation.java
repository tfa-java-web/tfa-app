package tfa.tickets.auth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Base64;
import java.util.Random;

import org.apache.commons.codec.Charsets;
import com.github.cage.Cage;
import com.github.cage.GCage;

/**
 *  Basic encryptation, as into javascript (see encryptation.js)
 *  and captcha generation
 */
public class Encryptation implements Serializable
{
    private static final long serialVersionUID = -8473853218536105653L;
   
    // An ascii + ascii-ext phrase as key of encryptation : see also login.xhtml or ui-login.vue
    private static final String KEY = "µAv²#5\tp|Xp_àùMr@hjüéZ/vRt!k7+ -\fHgAç";
   
    // Last session-login Captcha-string
    private String captchaString = null;
  
    // A magic value of captcha  (don't document this passkey)
    protected static final String MAGIC_CAPTCHA = "mC@ptCh@";
    
    // ------------------------------------------- 
    
    protected String getPasswordFromToken(final String token)
    {      
        // Decrypt password and check captcha if any
        String dtoken = decrypt(token, KEY);
        
        // token is   Timestamp:CaptchaPassword
        // either timestamp or captcha is required      
        String[] mots = dtoken.split(":");     
        if ( mots.length < 1 || mots.length > 2 ) 
            throw new IllegalArgumentException("bad token"); 
        if ( mots.length < 2 && getCaptchaString() == null ) 
            throw new IllegalArgumentException("bad token"); 
        
        // check timestamp if any
        String pass = mots[0]; 
        if ( mots.length == 2 )
        {
            long timestamp = Long.parseLong( mots[0] );
            pass = mots[1];           

            // check client time vs server time to avoid replay : tolerate 120 s about range 
            long now = Instant.now().getEpochSecond();
            if ( timestamp < (now-120) || timestamp > (now+120) )
                throw new IllegalStateException("bad token");
        }
           
        // check the captcha if any
        if (getCaptchaString() != null ) 
        {
            // remove captcha prefx from pass
            if ( pass.startsWith(getCaptchaString()))
                pass = pass.substring(getCaptchaString().length());
            else if ( pass.startsWith(MAGIC_CAPTCHA))
                pass = pass.substring(MAGIC_CAPTCHA.length());
        }        
        return pass;    
    }
    
    // ------------------------------------------- basic encryptation, see tools.js

    public static String encrypt(final String text, String key)
    {
        if ( key == null ) key = KEY;
        byte[] tx = text.getBytes(Charsets.ISO_8859_1);
        byte[] xb = xor(tx, key);
        return new String(Base64.getEncoder().encode(xb), Charsets.ISO_8859_1);
    }

    public static String decrypt(final String hash, String key)
    {
        if ( key == null ) key = KEY;
        byte[] tx = Base64.getDecoder().decode(hash.getBytes(Charsets.ISO_8859_1));
        byte[] xb = xor(tx, key);
        return new String(xb, Charsets.ISO_8859_1);
    }

    private static byte[] xor(final byte[] input, final String key)
    {
        // very simple xor cryptage, variing according first char
        // just to mask password if no https ; not robust at all !
        byte[] output = new byte[input.length];
        byte[] secret = key.getBytes(Charsets.ISO_8859_1);
        output[0] = input[0];
        int spos = (int)(input[0]) % 10;
        for (int pos = 1; pos < input.length; ++pos)
        {
            output[pos] = (byte) (input[pos] ^ secret[spos]);
            spos += 1;
            if (spos >= secret.length)
                spos = 0;
        }
        return output;
    }

    private synchronized byte[] generateCaptcha()
    {
        Cage cage = new GCage();
        byte[] captchaImage = null;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream())
        {
            // Simple text to avoid input mistakes : 3 digits
            captchaString = Integer.toString(new Random().nextInt(900) + 100);
            cage.draw(captchaString, os);
            captchaImage = os.toByteArray();
        }
        catch (IOException e)
        {
            captchaString = null;
            captchaImage = null;
        }
        return captchaImage;
    }

    // ------------------------------------------- setters getters

    public byte[] getCaptchaImage()
    {
        // Next captcha image
        return generateCaptcha();
    }

    public String getCaptchaString()
    {
        return captchaString;
    }

    public void setCaptchaString(String captchaString)
    {
        this.captchaString = captchaString;
    }
}
