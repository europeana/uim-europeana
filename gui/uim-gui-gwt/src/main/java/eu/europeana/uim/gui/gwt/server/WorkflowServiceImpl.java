package eu.europeana.uim.gui.gwt.server;

import eu.europeana.uim.gui.gwt.client.WorkflowService;
import eu.europeana.uim.gui.gwt.shared.Workflow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class WorkflowServiceImpl extends AbstractOSGIRemoteServiceServlet implements WorkflowService {

    public WorkflowServiceImpl() {
        super();
    }

    @Override
    public List<Workflow> getWorkflows() {
        System.out.println("I got a freaking engine: " + engine);

        List<Workflow> res = new ArrayList<Workflow>();
        List<eu.europeana.uim.api.Workflow> workflows = engine.getRegistry().getWorkflows();
        for(eu.europeana.uim.api.Workflow w : workflows) {
            res.add(new Workflow(w.getName(), w.getDescription()));
        }
        return res;
    }

}
