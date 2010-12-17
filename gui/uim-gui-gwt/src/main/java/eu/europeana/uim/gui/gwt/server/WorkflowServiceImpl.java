package eu.europeana.uim.gui.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.gui.gwt.client.WorkflowService;
import eu.europeana.uim.gui.gwt.shared.Workflow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class WorkflowServiceImpl extends RemoteServiceServlet implements WorkflowService {

    private final Registry registry;

    public WorkflowServiceImpl() {
        this.registry = RemoteServiceDependenciesActivator.getRegistry();
    }

    @Override
    public List<Workflow> getWorkflows() {
        System.out.println("I got a freaking registry: " + registry);

        List<Workflow> res = new ArrayList<Workflow>();
        List<eu.europeana.uim.api.Workflow> workflows = registry.getWorkflows();
        for(eu.europeana.uim.api.Workflow w : workflows) {
            res.add(new Workflow(w.getName(), w.getDescription()));
        }
        return res;
    }

    @Override
    protected void doUnexpectedFailure(Throwable e) {
        e.printStackTrace();
    }
}
