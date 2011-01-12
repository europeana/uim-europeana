package eu.europeana.uim.gui.gwt.server;

import eu.europeana.uim.gui.gwt.client.OrchestrationService;
import eu.europeana.uim.gui.gwt.shared.Collection;
import eu.europeana.uim.gui.gwt.shared.Execution;
import eu.europeana.uim.gui.gwt.shared.Provider;
import eu.europeana.uim.gui.gwt.shared.Workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class OrchestrationServiceImpl extends AbstractOSGIRemoteServiceServlet implements OrchestrationService {

    public OrchestrationServiceImpl() {
        super();
    }

    private Map<Long, Provider> wrappedProviders = new HashMap<Long, Provider>();

    private Map<Long, Execution> wrappedExecutions = new HashMap<Long, Execution>();

    @Override
    public List<Workflow> getWorkflows() {
        List<Workflow> res = new ArrayList<Workflow>();
        List<eu.europeana.uim.api.Workflow> workflows = getEngine().getRegistry().getWorkflows();
        for(eu.europeana.uim.api.Workflow w : workflows) {
            res.add(new Workflow(w.getId(), w.getName(), w.getDescription()));
        }
        return res;
    }

    @Override
    public List<Provider> getProviders() {
        List<Provider> res = new ArrayList<Provider>();
        List<eu.europeana.uim.store.Provider> providers = getEngine().getRegistry().getStorage().getProvider();
        for(eu.europeana.uim.store.Provider p : providers) {
            Provider provider = new Provider(p.getId(), p.getName());
            wrappedProviders.put(provider.getId(), provider);
            res.add(provider);
        }
        return res;
    }

    @Override
    public List<Collection> getCollections(Long provider) {
        List<Collection> res = new ArrayList<Collection>();
        eu.europeana.uim.store.Provider p = getEngine().getRegistry().getStorage().getProvider(provider);
        List<eu.europeana.uim.store.Collection> cols = getEngine().getRegistry().getStorage().getCollections(p);
        for (eu.europeana.uim.store.Collection col : cols) {
            res.add(new Collection(col.getId(), col.getName(), wrappedProviders.get(provider)));
        }
        return res;
    }

    @Override
    public Execution startCollection(Long workflow, Long collection) {
        eu.europeana.uim.store.Collection c = getEngine().getRegistry().getStorage().getCollection(collection);
        if(c == null) {
            throw new RuntimeException("Error: cannot find collection " + collection);
        }
        eu.europeana.uim.api.Workflow w = getWorkflow(workflow);
        Execution execution = new Execution();
        GWTProgressMonitor monitor = new GWTProgressMonitor(execution);
        eu.europeana.uim.store.Execution e = getEngine().getOrchestrator().executeWorkflow(w, c, monitor);
        execution.setId(e.getId());
        wrappedExecutions.put(e.getId(), execution);

        return execution;

    }

    @Override
    public Execution startProvider(Long workflow, Long provider) {
        eu.europeana.uim.store.Provider p = getEngine().getRegistry().getStorage().getProvider(provider);
        if(p == null) {
            throw new RuntimeException("Error: cannot find provider " + provider);
        }
        eu.europeana.uim.api.Workflow w = getWorkflow(workflow);
        Execution execution = new Execution();
        GWTProgressMonitor monitor = new GWTProgressMonitor(execution);
        eu.europeana.uim.store.Execution e = getEngine().getOrchestrator().executeWorkflow(w, p, monitor);
        execution.setId(e.getId());
        wrappedExecutions.put(e.getId(), execution);

        return execution;
    }

    @Override
    public Execution getExecution(Long id) {
        return wrappedExecutions.get(id);
    }

    private eu.europeana.uim.api.Workflow getWorkflow(Long id) {
        eu.europeana.uim.api.Workflow workflow = getEngine().getRegistry().getWorkflow(id);
        if(workflow == null) {
            throw new RuntimeException("Error: cannot find workflow " + workflow);
        }
        return workflow;
    }
}

