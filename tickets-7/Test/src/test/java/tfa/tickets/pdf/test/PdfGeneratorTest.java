package tfa.tickets.pdf.test;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import tfa.tickets.pdf.PdfGenerator;

public class PdfGeneratorTest
{
    static private PdfGenerator file;
    
    @BeforeClass
    static public void init() throws IOException
    {
        file = new PdfGenerator("src/test/resources/sample.pdf");
    }
    
    @AfterClass
    static public void term() throws IOException
    {
        file.save( "target/sample.pdf" );
    }
    
    @Test 
    public void testSet() throws IOException
    {
        file.set("title1", "tfa author");
        file.set("description1", "cccc\nvvvvv");
        file.set("status1", "Yes");
        file.set("date1", "2018/12/31");
        // file.set("error", "qq");
    }

}
