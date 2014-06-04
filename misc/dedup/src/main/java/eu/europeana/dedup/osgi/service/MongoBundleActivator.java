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
package eu.europeana.dedup.osgi.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
/**
 * Activator for Mongo DB
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public class MongoBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {

	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}

	public static ClassLoader getBundleClassLoader() {
		return MongoBundleActivator.class.getClassLoader();
	}

}