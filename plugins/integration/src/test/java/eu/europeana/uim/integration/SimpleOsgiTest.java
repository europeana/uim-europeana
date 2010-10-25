package eu.europeana.uim.integration;

import org.osgi.framework.Constants;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.test.platform.Platforms;

/**
 * Based on http://static.springsource.org/osgi/docs/2.0.0.M1/reference/html-single/#testing<br/>
 * 
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class SimpleOsgiTest extends AbstractConfigurableBundleCreatorTests {

    public void testOsgiPlatformStarts() throws Exception {
	System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
	System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
	System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
	}

    @Override
    protected String getPlatformName() {
        return Platforms.FELIX;

    }
}
