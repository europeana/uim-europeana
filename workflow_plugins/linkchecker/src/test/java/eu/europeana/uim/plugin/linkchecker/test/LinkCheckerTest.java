package eu.europeana.uim.plugin.linkchecker.test;

import java.io.IOException;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessExecutor;
import org.im4java.process.ProcessStarter;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Georgios Markakis <gwarkx@hotmail.com>
 *
 * @since 11 Aug 2012
 */
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

	
	}
	


