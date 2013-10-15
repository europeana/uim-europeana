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
package eu.europeana.integration.test.nonosgi;

import javax.annotation.Resource;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import eu.europeana.uim.repoxclient.plugin.RepoxRestClient;
import eu.europeana.uim.repoxclient.test.AbstractRepoxClientTest;



/**
 * Repox integration tests (executed against an actual Repox instance)
 * 
 * @author Georgios Markakis
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/repoxTestContext.xml"})
public class RepoxClientIntegrationTest extends AbstractRepoxClientTest{

/**
 * Injected from spring
 */
@Resource
RepoxRestClient repoxRestClient;	

/**
 * Initialize superclass fields
 */
@Before
public void setupClient() {
	super.repoxRestClient = repoxRestClient;
}

}
