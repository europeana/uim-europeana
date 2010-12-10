package eu.europeana.uim.integration;

import org.apache.karaf.testing.AbstractIntegrationTest;
import org.apache.karaf.testing.Helper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.service.command.CommandProcessor;
import org.osgi.service.command.CommandSession;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.scanFeatures;


/**
 * Integration test for UIM commands<br/>
 * Warning: /!\ if you do not want to be driven insane, do check -- twice -- if you
 * do NOT have a running Karaf instance somewhere on your system<br/>
 *
 * @author Manuel Bernhardt
 */
@RunWith(JUnit4TestRunner.class)
public class CommandTest extends AbstractIntegrationTest {

    @Configuration
    public static Option[] configuration() throws Exception {
        return combine(
                Helper.getDefaultOptions(),
                // this is how you set the default log level when using pax logging (logProfile)
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("INFO"),
                scanFeatures(
                        maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("xml").classifier("features").versionAsInProject(),
                        "spring"),

                // our modules. Karaf / Pax Exam don't fare well together in regards to feature descriptors
                // so until they do have these, we need to specify the OSGIfied maven bundles by hand here
                // this should be in sync with the feature descriptor at /etc/uim-features.xml

                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-common").versionAsInProject(),
                
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-api").versionAsInProject(),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-storage-memory").versionAsInProject(),

                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-plugin-basic").versionAsInProject(),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-plugin-fileimp").versionAsInProject(),

                felix(),

                waitForFrameworkStartup()
        );
    }

    @Test
    public void testUIInfo() throws Exception {

        // we have still to wait, in order to give the framework a chance to start up
        Thread.sleep(10000);

        assertEquals("UIM Registry: No plugins. MemoryStorageEngine.", getCommandResult("uim:info"));
    }

    private String getCommandResult(String command) {
        String res = new String();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        CommandProcessor cp = getOsgiService(CommandProcessor.class);
        CommandSession cs = cp.createSession(System.in, ps, System.err);
        try {
            cs.execute(command);
            res = baos.toString("UTF-8").trim();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cs != null)
                cs.close();
        }
        return res;

    }

}
