package eu.europeana.europeanauim.publish;

import eu.europeana.europeanauim.publish.service.PublishService;
import eu.europeana.europeanauim.publish.utils.PropertyReader;
import eu.europeana.europeanauim.publish.utils.UimConfigurationProperty;
import eu.europeana.uim.neo4jplugin.impl.EDMRepositoryOSGIServiceProvider;
import eu.europeana.uim.neo4jplugin.impl.EDMRepositoryService;
import eu.europeana.uim.neo4jplugin.service.GraphImporterPlugin;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

public class PublishWorkflow extends AbstractWorkflow {


  public PublishWorkflow(PublishService publishService) {
    super("H: Publish Data",
        "Workflow that optimizes the Solr Index, cuilds uggesters and spellchecking");
    setStart(new BatchWorkflowStart());
    addStep(new PublishPlugin(publishService, "Publish Plugin", "Publish Plugin"));

    String serverUrl = PropertyReader.getProperty(UimConfigurationProperty.NEO4JPRODUCTIONPATH);
    String serverIndex = PropertyReader.getProperty(UimConfigurationProperty.NEO4JPRODUCTIONINDEX);
    EDMRepositoryOSGIServiceProvider provider =
        new EDMRepositoryOSGIServiceProvider(new EDMRepositoryService(serverUrl, serverIndex));
    GraphImporterPlugin graphPlugin = new GraphImporterPlugin(provider);
    addStep(graphPlugin);
  }

  public boolean isSavepoint(String pluginIdentifier) {
    return false;
  }

  public boolean isMandatory(String pluginIdentifier) {
    return false;
  }

}
