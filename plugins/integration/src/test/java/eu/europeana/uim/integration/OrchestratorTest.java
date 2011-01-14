package eu.europeana.uim.integration;

import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import org.apache.karaf.testing.Helper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.scanFeatures;

/**
 * Integration test for the Orchestrator, using the MemoryStorageEngine
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
@RunWith(JUnit4TestRunner.class)

public class OrchestratorTest extends AbstractUIMIntegrationTest {

    @Configuration
    public static Option[] configuration() throws Exception {
        return combine(
                Helper.getDefaultOptions(
                        systemProperty("karaf.name").value("junit"),
                        systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("FINE")),

                        // rhaa
                        systemProperty("integrationDir").value(System.getProperty("integrationDir")),

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
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-plugin-dummy").versionAsInProject(),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-workflow-dummy").versionAsInProject(),

                felix(),

                waitForFrameworkStartup()
        );
    }

    @Test
    public void processSampleData() throws Exception {
        Registry registry = getOsgiService(Registry.class);

        StorageEngine storage = null;
        while (storage == null) {
            storage = registry.getStorage();
            Thread.sleep(500);
        }

        // load the provider data
        getCommandResult("uim:store -o loadSampleData");
        Thread.sleep(3000);

        // load the actual MDRs
        getCommandResult(String.format("uim:file -c 2 %s/common/src/test/resources/readingeurope.xml", findRootPath(".")));
        Thread.sleep(10000);

        assertEquals("Wrong count of imported test MDRs", 999, storage.getTotalForAllIds());

        // run the workflow
        Workflow w = registry.getWorkflows().get(0);
        Provider p = registry.getStorage().getProvider(0);
        Collection c = registry.getStorage().getCollections(p).get(0);
        assertEquals("Wrong count of imported test MDRs", 999, storage.getTotalByCollection(c));

//        for(Collection col:registry.getStorage().getCollections(p)) {
//            System.out.println(col.getName());
//            if(col.getName().equals("Test: Reading Europe")) {
//                c = col;
//            };
//        }

        Orchestrator o = getOsgiService(Orchestrator.class);
        TestProgressMonitor monitor = new TestProgressMonitor();
        o.executeWorkflow(w, c, monitor);

        Thread.sleep(5000);

        assertEquals("Wrong count of processed MDRs", 999, monitor.worked);



    }

    private String findRootPath(String current) {
        String integrationDir = System.getProperty("integrationDir");
        if(integrationDir == null) {
            throw new RuntimeException("Could not find integrationDir environment variable. If you run this test from an IDE, make sure you pass -DintegrationDir=<path to the integration test project> as VM arg");
        }
        return integrationDir + File.separatorChar + "../../";
    }

    private static class TestProgressMonitor implements ProgressMonitor {

        protected int worked = 0;

        @Override
        public void beginTask(String task, int work) {
        }

        @Override
        public void worked(int work) {
            worked += work;
            System.out.println("Blip.");
        }

        @Override
        public void done() {
        }

        @Override
        public void subTask(String subtask) {
        }

        @Override
        public void setCancelled(boolean cancelled) {
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }

}
