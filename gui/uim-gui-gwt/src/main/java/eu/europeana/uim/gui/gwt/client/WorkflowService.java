package eu.europeana.uim.gui.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import eu.europeana.uim.gui.gwt.shared.Workflow;

import java.util.List;

/**
 * Service to get the available workflows
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
@RemoteServiceRelativePath("workflow")
public interface WorkflowService extends RemoteService {

    List<Workflow> getWorkflows();

}
