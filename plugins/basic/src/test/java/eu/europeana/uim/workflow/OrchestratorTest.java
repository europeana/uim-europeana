package eu.europeana.uim.workflow;

import eu.europeana.uim.api.Registry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/META-INF/spring/test-bundle-context.xml")
public class OrchestratorTest {

    @Autowired
    private Registry registry;

    @Test
    public void someTest() {

    }

    
}
