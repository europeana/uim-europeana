/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.uim.plugin.thumbler.utils;

import java.io.File;
import java.io.IOException;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessExecutor;
import org.im4java.process.ProcessStarter;

/**
* Image Magick specific functionality used by the Thumbler plugin
* 
* @author Georgios Markakis <gwarkx@hotmail.com>
* @since 12 Apr 2012
*/

public final class ImageMagickUtils {

	private final static ProcessExecutor exec;
	
	//Static initialisation block used to initialise the exact location
	//of the ImageMagick binaries invoked by Img4J.
	static{
		String myPath="C:\\Program Files\\ImageMagick-6.7.7-Q16\\;C:\\Software\\exiftool\\";
		ProcessStarter.setGlobalSearchPath(myPath);
		exec = new ProcessExecutor();
	}

	/**
	 * Private Constructor, no instantiation allowed
	 */
	private ImageMagickUtils(){
		
	}
	
	
	
	/**
	 * Converts a given File object, which represents a downloaded
	 * file from the linkchecking process (that can be a GIF,a TIFF
	 * or a PDF file) into a JPEG file and returns it.
	 * 
	 * @param img
	 */
	public static File convert(File img0){
		String img = img0.getAbsolutePath();
		String img2 = img0.getAbsolutePath() + ".jpg";
		IMOperation  op = new IMOperation ();
		op.addImage(img);
		op.addImage(img2);
		ConvertCmd convert = new ConvertCmd();
			try {
				convert.run(op);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IM4JavaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new File(img2);

	}

}
