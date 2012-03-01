/**
 * 
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
 * 
 * @author Georgios Markakis 
 */
public class HttpRetriever implements Iterator<String>{

	private ZipFile zf; 
	private Enumeration<? extends ZipEntry>  zipentries;
	private Long number_of_recs;
	
	


	private HttpRetriever(ZipFile zf,Long number_of_recs, Enumeration<? extends ZipEntry>  zipentries){
		this.zf = zf;
		this.number_of_recs = number_of_recs;
		this.zipentries = zipentries;
	}
	
	public static HttpRetriever createInstance(URL url) throws IOException{
		
		File dest = new File("tmp");
		FileUtils.copyURLToFile(url, dest, 100, 1000);

		ZipFile zipfile = new ZipFile(dest.getAbsolutePath());
		Enumeration<? extends ZipEntry>  entries = zipfile.entries();
		
		return new HttpRetriever(zipfile,new Long(zipfile.size()),entries);
	}
	
	
	@Override
	public boolean hasNext() {
		return zipentries.hasMoreElements();
	}

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

	@Override
	public void remove() {
		
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
	public Long getNumber_of_recs() {
		return number_of_recs;
	}

	/**
	 * @param number_of_recs the number_of_recs to set
	 */
	public void setNumber_of_recs(Long number_of_recs) {
		this.number_of_recs = number_of_recs;
	}
}
