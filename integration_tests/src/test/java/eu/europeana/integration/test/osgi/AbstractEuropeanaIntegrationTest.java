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
package eu.europeana.integration.test.osgi;

import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.scanFeatures;

import org.apache.karaf.testing.AbstractIntegrationTest;
import org.apache.karaf.testing.Helper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;


/**
 * Abstract configuration Class for pax exam tests.
 * 
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since Jan 30, 2012
 */
public abstract class AbstractEuropeanaIntegrationTest extends
		AbstractIntegrationTest {

	final static long time2w84service = 10000;
	
    /**
     * This is the configuration section of the "virtual" Karaf container during the tests execution. It sets 
     * all the dependencies required for the installation of the UIM Core & Repox Plugin modules 
     * @return
     * @throws Exception
     */
    @Configuration
    public static Option[] configuration() throws Exception {
      
    	return combine(
 				Helper.getDefaultOptions(
						systemProperty("karaf.name").value("junit"),
						systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("DEBUG")),

				
			    //This corresponds to the "jibx" feature in uim-features.xml
		        mavenBundle().groupId("org.jibx").artifactId("jibx-run").version("1.2.3").startLevel(10),
		        mavenBundle().groupId("org.jibx").artifactId("jibx-extras").version("1.2.3").startLevel(10),
	
		        
				//This corresponds to the "uim-core-dev" feature in uim-features.xml
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-common").version("2.0.0-SNAPSHOT").startLevel(20),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-api").version("2.0.0-SNAPSHOT").startLevel(20),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-orchestration-basic").version("2.0.0-SNAPSHOT").startLevel(20),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-external-sugar-api").version("2.0.0-SNAPSHOT").startLevel(20),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-external-repox-api").version("2.0.0-SNAPSHOT").startLevel(20),
                mavenBundle().groupId("eu.europeana.europeanauim").artifactId("europeana-datamodel-definitions").versionAsInProject().startLevel(20),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-storage-memory").version("2.0.0-SNAPSHOT").startLevel(20),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-logging-memory").version("2.0.0-SNAPSHOT").startLevel(20),
                
				//This corresponds to the "uim-integration-plugins" feature in uim-features.xml
                mavenBundle().groupId("eu.europeana.europeanauim").artifactId("europeana-uim-dedup").versionAsInProject().startLevel(30),
                mavenBundle().groupId("eu.europeana.europeanauim").artifactId("europeana-uim-plugin-sugarcrmclient").versionAsInProject().startLevel(40),
                mavenBundle().groupId("eu.europeana.europeanauim").artifactId("europeana-uim-plugin-repox").versionAsInProject().startLevel(40),
                mavenBundle().groupId("eu.europeana.europeanauim").artifactId("europeana-uim-plugin-mint").versionAsInProject().startLevel(40),
                
				//This corresponds to the "uim-europeana-workflows-plugins" feature in uim-features.xml
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-plugin-solr4").version("2.0.0-SNAPSHOT").startLevel(20),
                mavenBundle().groupId("eu.europeana.europeanauim").artifactId("europeana-uim-plugin-linkchecker").versionAsInProject().startLevel(30),
                mavenBundle().groupId("eu.europeana.europeanauim").artifactId("europeana-uim-plugin-solrosgi").versionAsInProject().startLevel(40),
                mavenBundle().groupId("eu.europeana.europeanauim").artifactId("europeana-uim-plugin-enrichment").versionAsInProject().startLevel(40),
                //mavenBundle().groupId("eu.europeana.europeanauim").artifactId("europeana-uim-plugin-deactivate").versionAsInProject().startLevel(40),
                
				//This corresponds to the "uim-europeana-workflows" feature in uim-features.xml
                mavenBundle().groupId("eu.europeana.europeanauim").artifactId("europeana-workflows-startups").versionAsInProject().startLevel(40),
                mavenBundle().groupId("eu.europeana.europeanauim").artifactId("europeana-uim-workflows-basic").versionAsInProject().startLevel(40),
                
                felix(),
                
                waitForFrameworkStartup()
        );
    }
	
	
	
}
