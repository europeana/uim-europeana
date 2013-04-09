/**
 * 
 */
package eu.europeana.uim.europeanaspecific.workflowstarts.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import org.junit.Test;
import eu.europeana.uim.europeanaspecific.workflowstarts.httpzip.HttpRetriever;

/**
 * Testing the HttpRetriever iterator class functionality
 * 
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since 19 Feb 2013
 *
 */
public class HttpRetrieverTest {

	@Test
	public void retrieverTest() throws IOException{
		
		int count = 0 ;
		
		URL url = new URL("http://sip-manager.isti.cnr.it/geomark/records-test4.tar.gz");
		
		HttpRetriever retiever = new HttpRetriever().createInstance(url,"/tmp/test.tar.gz");
		
		while(retiever.hasNext()){
		    count++;	
			System.out.println(retiever.next());
		}
		
		assertEquals(count,4);
		
	}
	
	
	
}
