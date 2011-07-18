package eu.europeana.uim.plugin.linkchecker;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import eu.europeana.uim.plugin.linkchecker.errorcodes.LinkStatus;
import eu.europeana.uim.plugin.linkchecker.errorcodes.ThumbnailStatus;
import eu.europeana.uim.plugin.linkchecker.exceptions.FileStorageException;
import eu.europeana.uim.plugin.linkchecker.exceptions.HttpAccessException;


public class CheckUrl {
    protected String uri;
    private Integer redirectDepth;
    protected ByteArrayOutputStream orgFileConent = null; // storage for downloaded item, not used when just linkchecking


    /**
     * @param uri - link to check
     */
    public CheckUrl(String uri) {
        this.uri = uri;
        initiateParams(5);
    }

    /**
     * @param uri - link to check
     * @param redirectDepth   - to avoid endless redirect loops...
     */
    public CheckUrl(String uri, Integer redirectDepth) {
        this.uri = uri;
        initiateParams(redirectDepth);
    }


    

    public void isResponding() throws HttpAccessException, FileStorageException {
        doConnection(this.redirectDepth, false);
    }
    
    
    public void isResponding(boolean saveItem) throws HttpAccessException, FileStorageException {
        doConnection(this.redirectDepth, saveItem);
    }
    
    
    public void isResponding(Integer redirectDepth) throws HttpAccessException, FileStorageException {
        doConnection(redirectDepth, false);
    }



    private void doConnection(Integer redirectDepth, boolean saveItem) throws HttpAccessException, FileStorageException{
        HttpURLConnection urlConnection;
        URL url;
        BufferedInputStream in;

        //What exactly are you trying to do here?
        if (redirectDepth < 0) {
        	throw new HttpAccessException(LinkStatus.REDIRECT_DEPTH_EXCEEDED);
        }

        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
        	throw new HttpAccessException(LinkStatus.BAD_URL);
        }

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
        	throw new HttpAccessException(LinkStatus.FAILED_TO_OPEN_CONNECTION);
        }
        //urlConnection.setRequestMethod("HEAD");
        
        //This should not be hardwired
        urlConnection.setConnectTimeout(5000); /* timeout after 5s if can't connect */

        try {
            urlConnection.connect();
        } catch (IOException e) {
        	throw new HttpAccessException(LinkStatus.FAILED_TO_CONNECT);
        }

        String redirectLink = urlConnection.getHeaderField("Location");
        if (redirectLink != null && !uri.equals(redirectLink)) {
            isResponding(redirectDepth - 1);
        }

        Integer respCode;
        try {
            respCode = urlConnection.getResponseCode();
        } catch (IOException e) {
        	throw new HttpAccessException(LinkStatus.FAILED_TO_OPEN_CONNECTION);	
        }
        if (respCode != HttpURLConnection.HTTP_OK) {
        	throw new HttpAccessException(LinkStatus.HTTP_ERROR);
        }


        if (saveItem) {
            // since the link is open read it now and save the item into orgFile to avoid additional connects
            try {
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
            	throw new FileStorageException(ThumbnailStatus.FAILED_TO_BIND_TO_INPUT_STREAM);
            }
            orgFileConent = new ByteArrayOutputStream();
            int c;
            try {
                while ((c = in.read()) != -1) {
                    orgFileConent.write(c);
                }
                orgFileConent.close();
            } catch (IOException e) {
                orgFileConent = null;
            	throw new FileStorageException(ThumbnailStatus.FAILED_TO_READ_ORIG);

            }
        }
        urlConnection.disconnect();
    }



    private void initiateParams(Integer redirectDepth) {
        this.redirectDepth = redirectDepth;
    }
}
