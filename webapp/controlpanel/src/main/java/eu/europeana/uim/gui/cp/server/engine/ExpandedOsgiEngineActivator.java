/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.gui.cp.server.engine;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.europeana.dedup.osgi.service.DeduplicationService;
import eu.europeana.uim.Registry;
import eu.europeana.uim.repoxclient.rest.RepoxUIMServiceT;
import eu.europeana.uim.sugar.SugarCrmService;


/**
 * Expanded Bundle Activator with support for SugarCrmService, RepoxUIMService, DeduplicationService
 * 
 * @author Georgios Markakis
 */
public class ExpandedOsgiEngineActivator implements BundleActivator {

    private static ExpandedOsgiEngine expengine = null;

    
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
        Registry registry = null;
        RepoxUIMServiceT repoxService = null;
        SugarCrmService sugarCrmService = null;
        DeduplicationService dedupService = null;
        int wait = 0;
        while (registry == null && wait++ < 10) {
            ServiceReference registryRef = bundleContext.getServiceReference("eu.europeana.uim.Registry");
            if (registryRef != null) {
                registry = (Registry)bundleContext.getService(registryRef);
            }
            Thread.sleep(1000);
        }
        
        while (sugarCrmService == null && wait++ < 10) {
            ServiceReference sugarCrmRef = bundleContext.getServiceReference("eu.europeana.uim.sugar.SugarCrmService");
            if (sugarCrmRef != null) {
            	sugarCrmService = (SugarCrmService)bundleContext.getService(sugarCrmRef);
            }
            Thread.sleep(1000);
        }
        
        
        while (repoxService == null && wait++ < 10) {
            ServiceReference repoxRef = bundleContext.getServiceReference("eu.europeana.uim.repoxclient.rest.RepoxUIMServiceT");
            if (repoxRef != null) {
            	repoxService = (RepoxUIMServiceT)bundleContext.getService(repoxRef);
            }
            Thread.sleep(1000);
        }

        while(dedupService == null && wait++ <10){
        	ServiceReference dedupRef = bundleContext.getServiceReference("eu.europeana.dedup.osgi.service.DeduplicationService");
        	if(dedupRef !=null){
        		dedupService = (DeduplicationService) bundleContext.getService(dedupRef);
        	}
        }
        expengine = new ExpandedOsgiEngine(registry,repoxService,sugarCrmService,dedupService);
        ExpandedOsgiEngine.setEngine(expengine);
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {

		expengine = null;

	}

}
