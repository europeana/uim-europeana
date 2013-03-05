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
package eu.europeana.uim.europeanaspecific.workflowstarts.httpzip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import eu.europeana.uim.europeanaspecific.workflowstarts.util.PropertyReader;
import eu.europeana.uim.europeanaspecific.workflowstarts.util.UimConfigurationProperty;


/**
 * Retrieves the specified zip file over the remote http location and performs
 * an iteration within the zipped file contents
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 5 Mar 2012
 */
public class HttpRetriever implements Iterator<String> {

	
	private TarArchiveInputStream tarInputstream;
	
	private static Random generator; 

	private int number_of_recs;

	private int records_read;
	
	private static String dest;
	
	/**
	 * Private Class constructor (can be instantiated only with factory method)
	 * 
	 * @param zf
	 *            The zip file reference
	 * @param number_of_recs
	 *            The number of files contained within the specific fzip file
	 * @param zipentries
	 *            References to the zipped files
	 */
	private HttpRetriever() {
	
	}

	
	
	/**
	 * @param url
	 * @param dest
	 * @return
	 * @throws IOException
	 */
	public synchronized static  HttpRetriever createInstance(URL url,String dest)
			throws IOException {
		HttpRetriever.dest = dest;
		HttpRetriever ret = createInstance(url);

		
		return ret;
	}
	
	
	/**
	 * Static synchronized factory method that returns an instance of this
	 * class. It first copies the remote file locally and then instantiates the
	 * iterator.
	 * 
	 * @param url
	 *            The url from which to fetch the file
	 * @return an instance of this class
	 * @throws IOException
	 */
	public synchronized static HttpRetriever createInstance(URL url)
			throws IOException {
		
		HttpRetriever retriever = new HttpRetriever();
		
		if(generator == null){
			generator =  new Random();
		}
		
		File dest;
		
		//First copy the remote URI to a 
		if(HttpRetriever.dest == null){
		   dest = new File(PropertyReader.getProperty(UimConfigurationProperty.UIM_STORAGE_LOCATION)+new Integer(generator.nextInt()).toString());
		}
		else{
		   dest = new File(HttpRetriever.dest);
		}

		FileUtils.copyURLToFile(url, dest, 100, 100000);
		TarArchiveInputStream tarfile;

			tarfile =  new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(dest)));

			tarfile.available();
			
			TarArchiveEntry  entry;
			
			while( (entry = tarfile.getNextTarEntry()) != null){
				
				if(entry.isDirectory()){

				}
				else
				{	
					retriever.number_of_recs++;
				}
			}

			tarfile.close();
			
			TarArchiveInputStream iterabletarfile = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(dest)));
			
			iterabletarfile.available();
			
			retriever.tarInputstream = iterabletarfile;
			
			
		return retriever;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if(number_of_recs <= records_read ){
			return false;
		}
		else{
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public String next() {
		
		TarArchiveEntry entry;
		try {
			entry = tarInputstream.getNextTarEntry();
			
			while(entry.isDirectory()){
				entry = tarInputstream.getNextTarEntry();
			}
			
			byte[] content = new byte[(int) entry.getSize()];
			
			tarInputstream.read(content, 0, (int) entry.getSize());

			String xml = new String(content,"UTF-8");
			records_read ++;
			return xml;
		} catch (IOException e) {
			records_read ++;
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Operation not supported");
	}

	// Getters & Setters

	/**
	 * @return the number_of_recs
	 */
	public int getNumber_of_recs() {
		return number_of_recs;
	}

	/**
	 * @param number_of_recs
	 *            the number_of_recs to set
	 */
	public void setNumber_of_recs(int number_of_recs) {
		this.number_of_recs = number_of_recs;
	}
}
