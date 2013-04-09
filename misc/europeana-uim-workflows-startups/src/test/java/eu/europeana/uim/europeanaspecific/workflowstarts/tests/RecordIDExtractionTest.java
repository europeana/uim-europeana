/**
 * 
 */
package eu.europeana.uim.europeanaspecific.workflowstarts.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import eu.europeana.uim.europeanaspecific.workflowstarts.util.SaxBasedIDExtractor;


/**
 *
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since 26 Mar 2013
 *
 */
public class RecordIDExtractionTest {

	final static String unmarsallableEDM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	@Test
	public void extrtactIDsTest() throws IOException{
	
		File test = new File("src/test/resources/edm/sample.xml");

		InputStream ins = new FileInputStream(test);
		
		String edmxml = IOUtils.toString(ins, "UTF-8");
		
		SaxBasedIDExtractor extractor = new SaxBasedIDExtractor();
		
		List<String> ids = extractor.extractIDs(edmxml);
		
		for(String id:ids){
			System.out.println(id);
		}
		
		assertEquals(ids.size(),3);
	}
}
