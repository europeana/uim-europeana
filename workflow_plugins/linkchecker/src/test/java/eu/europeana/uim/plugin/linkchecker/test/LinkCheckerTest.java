package eu.europeana.uim.plugin.linkchecker.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.im4java.core.ConvertCmd;
import org.im4java.core.ETOperation;
import org.im4java.core.ExiftoolCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessExecutor;
import org.im4java.process.ProcessStarter;
import org.junit.Ignore;
import org.junit.Test;

import eu.europeana.uim.plugin.thumbler.utils.EDMXMPValues;




public class LinkCheckerTest {
	
	private final static ProcessExecutor exec;
	
	static{
		String myPath="C:\\Program Files\\ImageMagick-6.7.7-Q16\\;C:\\Software\\exiftool\\";
		ProcessStarter.setGlobalSearchPath(myPath);
		
		 exec = new ProcessExecutor();
	}
	
	
	/**
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 */
	@Ignore 
	@Test
	public void testImageTransformation() throws IOException, InterruptedException, IM4JavaException{
		String img = "./" +
				"/src/test/resources/robo.jpg";
		
		String img2 = img = "./" +
				"/src/test/resources/robo2.png";
		
		IMOperation  op = new IMOperation ();

		op.addImage(img);
		op.addImage(img2);

		ConvertCmd convert = new ConvertCmd();


			convert.run(op);

		
	}

	
	
	/**
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 */
	@Ignore 
	@Test
	public void testXMPData() throws IOException, InterruptedException, IM4JavaException{
	
		 Map<EDMXMPValues, String> map = new HashMap<EDMXMPValues, String>();
		 
		 map.put(EDMXMPValues.dc_rights, "Free4all");
		 map.put(EDMXMPValues.edm_rights, "Free4all");
		 map.put(EDMXMPValues.edm_dataProvider, "OCPCorp");
		 map.put(EDMXMPValues.edm_provider, "OCPCorporation");
		 map.put(EDMXMPValues.dc_title, "Robby_the_Cop");
		 map.put(EDMXMPValues.xmpRights_Marked, "false");
		 map.put(EDMXMPValues.xmpRights_WebStatement, "www.somehereintheweb.com");
		 map.put(EDMXMPValues.xmpMM_OriginalDocumentID, "DOCID");
		 map.put(EDMXMPValues.cc_useGuidelines, "How_to_use_an_image");
		 map.put(EDMXMPValues.cc_attributionName, "x");
		 map.put(EDMXMPValues.cc_morePermissions, "None");
		 
		 List<String> exiftoolargs = new ArrayList<String>();
		 List<String> tags = new ArrayList<String>();
		 
         Iterator<EDMXMPValues> it = map.keySet().iterator();
		 
		 while(it.hasNext()){
			 EDMXMPValues xmpkey = it.next();
			 String xmpvalue = map.get(xmpkey);			 
			 String xmpname = xmpkey.getFieldId().split(":")[1];
			 tags.add(xmpname);
			 
			 StringBuffer sb = new StringBuffer();
			 sb.append("-");
			 sb.append(xmpkey.getFieldId());
			 sb.append("=\"");
			 sb.append(xmpvalue);
			 sb.append("\"");

			 exiftoolargs.add(sb.toString());
		 }
		 
		 
		 
		String img2 =  "./" +
				"/src/test/resources/robo2.jpg";

		ETOperation op = new ETOperation();
		op.addImage(img2);


		op.delTags(tags.toArray(new String[tags.size()]));

		op.addRawArgs(exiftoolargs);
	
       ExiftoolCmd cmd = new ExiftoolCmd();
       cmd.run(op);
	}
	}
	


