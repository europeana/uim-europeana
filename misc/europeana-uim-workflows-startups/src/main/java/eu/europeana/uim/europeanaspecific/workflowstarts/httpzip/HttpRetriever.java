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
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.*;


/**
 * Retrieves the specified zip file over the remote http location
 * and performs an iteration within the zipped file contents 
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 5 Mar 2012
 */
public class HttpRetriever implements Iterator<String>{

	private ZipFile zf; 
	private Enumeration<? extends ZipEntry>  zipentries;
	private int number_of_recs;
	
	


	/**
	 * Private Class constructor (can be instantiated only with factory method) 
	 * 
	 * @param zf The zip file reference
	 * @param number_of_recs The number of files contained within the specific fzip file
	 * @param zipentries References to the zipped files
	 */
	private HttpRetriever(ZipFile zf,int number_of_recs, Enumeration<? extends ZipEntry>  zipentries){
		this.zf = zf;
		this.number_of_recs = number_of_recs;
		this.zipentries = zipentries;
	}
	
	
	
	/**
	 * Static synchronized factory method that returns an instance of this class.
	 * It first copies the remote file locally and then instantiates the iterator.
	 * 
	 * @param url The url from which to fetch the file
	 * @return an instance of this class
	 * @throws IOException
	 */
	public synchronized static HttpRetriever createInstance(URL url) throws IOException{
		
		File dest = new File("tmp");
		FileUtils.copyURLToFile(url, dest, 100, 1000);

		ZipFile zipfile = new ZipFile(dest.getAbsolutePath());
		Enumeration<? extends ZipEntry>  entries = zipfile.entries();
		
		return new HttpRetriever(zipfile,zipfile.size(),entries);
	}
	
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return zipentries.hasMoreElements();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public String next() {
		ZipEntry zentry = zipentries.nextElement();
		
		String resp = null;
		
		try {
			resp = IOUtils.toString(zf.getInputStream(zentry), "UTF-8");
		} catch (IOException e) {
			resp = "";
		}

		return resp;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Operation not supported");
	}

	
	// Getters & Setters
	
	/**
	 * @return the zf
	 */
	public ZipFile getZf() {
		return zf;
	}

	/**
	 * @param zf the zf to set
	 */
	public void setZf(ZipFile zf) {
		this.zf = zf;
	}

	/**
	 * @return the zipentries
	 */
	public Enumeration<? extends ZipEntry> getZipentries() {
		return zipentries;
	}

	/**
	 * @param zipentries the zipentries to set
	 */
	public void setZipentries(Enumeration<? extends ZipEntry> zipentries) {
		this.zipentries = zipentries;
	}

	/**
	 * @return the number_of_recs
	 */
	public int getNumber_of_recs() {
		return number_of_recs;
	}

	/**
	 * @param number_of_recs the number_of_recs to set
	 */
	public void setNumber_of_recs(int number_of_recs) {
		this.number_of_recs = number_of_recs;
	}
}
