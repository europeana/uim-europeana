package eu.europeana.uim.gui.gwt.server;

import eu.europeana.uim.api.Registry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * This bundle activator serves as a dependency provisioning mechanism to the GWT RemoteServices. We need this mechanism
 * since our servlets aren't being managed by the OSGI framework but by the servlet container
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class RemoteServiceDependenciesActivator implements BundleActivator {

    private static Registry registry = null;

    /**
     * Get the UIM Registry
     */
    public static Registry getRegistry() {
        return registry;
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        ServiceReference registryRef = bundleContext.getServiceReference("eu.europeana.uim.api.Registry");
        if(registryRef != null) {
            registry = (Registry) bundleContext.getService(registryRef);
        }
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        registry = null;
    }


}

