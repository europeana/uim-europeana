package eu.europeana.uim.plugin.linkchecker;

import java.io.File;
import eu.europeana.uim.plugin.linkchecker.CacheItem;

public class CacheTester {
    public static void main(String... args) {
        String msg;
        String[] imgs = {"http://ogimages.bl.uk/images/019/019ADDOR0000002U00000000[SVC1].jpg",};

        FileTree fileTree = new FileTree("/tmp/thumbler_files");
        if (!fileTree.ensureFileTreeIsOk(false)) {    // param false = quick scan
            System.out.println("Filetree not in an acceptable state");
            System.exit(1);
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
