package eu.europeana.uim.plugin.linkchecker.test;

import org.junit.Test;
import org.junit.runner.RunWith;


import eu.europeana.uim.plugin.linkchecker.CacheItem;
import eu.europeana.uim.plugin.linkchecker.CheckUrl;
import eu.europeana.uim.plugin.linkchecker.FileTree;

import static org.junit.Assert.assertEquals;



public class LinkCheckerTest {
	
	@Test
	public void checklinkTest() throws Exception{
        String msg;

        String[] uris = {"http://www.sunet.se/hjkhjk",
                        "http://www.google.com",
                        "http://www.badlink2.com",
                        };

        // test checking links

        for (String s : uris) {
            CheckUrl cu = new CheckUrl(s);
            if (cu.isResponding()) {
                msg = "ok";
            }
            else {
                msg = cu.getState().toString() + " " + cu.getErrorMessage();
            }
            System.out.println(s + "\t" + msg);
        }

    }
	
	
	
	@Test
	public void cacheTest() throws Exception{
		
        String msg;
        String[] imgs = {"http://ogimages.bl.uk/images/019/019ADDOR0000002U00000000[SVC1].jpg",};

        FileTree fileTree = new FileTree("/tmp/thumbler_files");
        if (!fileTree.ensureFileTreeIsOk(false)) {    // param false = quick scan
            System.out.println("Filetree not in an acceptable state");
        }

        // test handling images for caching
        for (String s : imgs) {
            CacheItem ci = new CacheItem(fileTree, s);
            if (ci.createCacheFiles()) {
                msg = "ok";
            }
            else {
                msg = ci.getState().toString();
            }
            System.out.println(s + "\t" + msg);
        }
		
	}
	
	
	
	}
	


