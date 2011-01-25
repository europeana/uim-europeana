package eu.europeana.uim.gui.gwt.server;

import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.WorkflowStepStatus;
import eu.europeana.uim.gui.gwt.client.OrchestrationService;
import eu.europeana.uim.gui.gwt.shared.Collection;
import eu.europeana.uim.gui.gwt.shared.Execution;
import eu.europeana.uim.gui.gwt.shared.Provider;
import eu.europeana.uim.gui.gwt.shared.StepStatus;
import eu.europeana.uim.gui.gwt.shared.Workflow;
import eu.europeana.uim.store.UimEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class OrchestrationServiceImpl extends AbstractOSGIRemoteServiceServlet implements OrchestrationService {

    private static Logger log = Logger.getLogger(OrchestrationServiceImpl.class.getName());

    public OrchestrationServiceImpl() {
        super();
    }

    private Map<Long, Provider> wrappedProviders = new HashMap<Long, Provider>();

    private Map<Long, Execution> wrappedExecutions = new HashMap<Long, Execution>();

    @Override
    public List<Workflow> getWorkflows() {
        List<Workflow> res = new ArrayList<Workflow>();
        List<eu.europeana.uim.api.Workflow> workflows = getEngine().getRegistry().getWorkflows();
        if (workflows != null) {
            for (eu.europeana.uim.api.Workflow w : workflows) {
                res.add(new Workflow(w.getId(), w.getName(), w.getDescription()));
            }
        }
        return res;
    }

    @Override
    public List<Provider> getProviders() {
        List<Provider> res = new ArrayList<Provider>();
        List<eu.europeana.uim.store.Provider> providers = getEngine().getRegistry().getStorage().getProvider();
        if (providers != null) {
            for (eu.europeana.uim.store.Provider p : providers) {
                Provider provider = getWrappedProvider(p.getId());
                res.add(provider);
            }
        }
        return res;
    }

    @Override
    public List<Collection> getCollections(Long provider) {
        List<Collection> res = new ArrayList<Collection>();
        eu.europeana.uim.store.Provider p = getEngine().getRegistry().getStorage().getProvider(provider);
        List<eu.europeana.uim.store.Collection> cols = getEngine().getRegistry().getStorage().getCollections(p);
        for (eu.europeana.uim.store.Collection col : cols) {
            res.add(new Collection(col.getId(), col.getName(), getWrappedProvider(provider), getEngine().getRegistry().getStorage().getTotalByCollection(col)));
        }
        return res;
    }

    @Override
    public List<Collection> getAllCollections() {
        List<Collection> res = new ArrayList<Collection>();
        List<eu.europeana.uim.store.Collection> cols = getEngine().getRegistry().getStorage().getAllCollections();
        for (eu.europeana.uim.store.Collection col : cols) {
            res.add(new Collection(col.getId(), col.getName(), getWrappedProvider(col.getProvider().getId()), getEngine().getRegistry().getStorage().getTotalByCollection(col)));
        }
        return res;
    }

    @Override
    public List<Execution> getActiveExecutions() {
        List<Execution> r = new ArrayList<Execution>();
        for (eu.europeana.uim.store.Execution execution : getEngine().getRegistry().getStorage().getExecutions()) {
            if (execution.isActive()) {
                r.add(getWrappedExecution(execution.getId(), execution));
            }
        }
        return r;
    }

    @Override
    public List<Execution> getPastExecutions() {
        List<Execution> r = new ArrayList<Execution>();
        for (eu.europeana.uim.store.Execution execution : getEngine().getRegistry().getStorage().getExecutions()) {
            if (!execution.isActive()) {
                r.add(getWrappedExecution(execution.getId(), execution));
            }
        }
        return r;
    }

    @Override
    public Execution startCollection(Long workflow, Long collection) {
        eu.europeana.uim.store.Collection c = getEngine().getRegistry().getStorage().getCollection(collection);
        if (c == null) {
            throw new RuntimeException("Error: cannot find collection " + collection);
        }
        eu.europeana.uim.api.Workflow w = getWorkflow(workflow);
        Execution execution = new Execution();
        GWTProgressMonitor monitor = new GWTProgressMonitor(execution);
        ActiveExecution ae = getEngine().getOrchestrator().executeWorkflow(w, c, monitor);
        populateWrappedExecution(execution, ae, w, c);

        return execution;

    }

    @Override
    public Execution startProvider(Long workflow, Long provider) {
        eu.europeana.uim.store.Provider p = getEngine().getRegistry().getStorage().getProvider(provider);
        if (p == null) {
            throw new RuntimeException("Error: cannot find provider " + provider);
        }
        eu.europeana.uim.api.Workflow w = getWorkflow(workflow);
        Execution execution = new Execution();
        GWTProgressMonitor monitor = new GWTProgressMonitor(execution);
        ActiveExecution ae = getEngine().getOrchestrator().executeWorkflow(w, p, monitor);
        populateWrappedExecution(execution, ae, w, p);
        return execution;
    }

    private void populateWrappedExecution(Execution execution, ActiveExecution ae, eu.europeana.uim.api.Workflow w, UimEntity dataset) {
         execution.setId(ae.getId());
         execution.setName(w.getName() + " on " + dataset.toString());
         execution.setWorkflow(w.getId());
         execution.setTotal(getEngine().getOrchestrator().getTotal(ae));
         execution.setStartTime(ae.getStartTime());
         wrappedExecutions.put(ae.getId(), execution);
     }

    @Override
    public Execution getExecution(Long id) {
        return wrappedExecutions.get(id);
    }

    private Provider getWrappedProvider(Long provider) {
        Provider wrapped = wrappedProviders.get(provider);
        if (wrapped == null) {
            eu.europeana.uim.store.Provider p = getEngine().getRegistry().getStorage().getProvider(provider);
            wrapped = new Provider(p.getId(), p.getName());
            wrappedProviders.put(provider, wrapped);
        }
        return wrapped;
    }

    private Execution getWrappedExecution(Long execution, eu.europeana.uim.store.Execution e) {
        Execution wrapped = wrappedExecutions.get(execution);
        if (wrapped == null) {
            wrapped = new Execution();
            wrapped.setId(execution);
            wrapped.setActive(e.isActive());
            wrapped.setStartTime(e.getStartTime());
            wrapped.setEndTime(e.getEndTime());
            wrapped.setName(e.getWorkflowIdentifier() + " on " + e.getDataSet().toString());
            wrappedExecutions.put(execution, wrapped);
        } else {
            // update what may have changed
            wrapped.setActive(e.isActive());
            wrapped.setEndTime(e.getEndTime());
        }
        return wrapped;
    }

    private eu.europeana.uim.api.Workflow getWorkflow(Long id) {
        eu.europeana.uim.api.Workflow workflow = getEngine().getRegistry().getWorkflow(id);
        if (workflow == null) {
            throw new RuntimeException("Error: cannot find workflow " + workflow);
        }
        return workflow;
    }

    @Override
    public Integer getCollectionTotal(Long collection) {
        return getEngine().getRegistry().getStorage().getTotalByCollection(getEngine().getRegistry().getStorage().getCollection(collection));
    }

    @Override
    public List<StepStatus> getStatus(Long workflow) {
        List<StepStatus> res = new ArrayList<StepStatus>();
        List<WorkflowStepStatus> runtimeStatus = getEngine().getOrchestrator().getRuntimeStatus(getWorkflow(workflow));
        for (WorkflowStepStatus wss : runtimeStatus) {
            StepStatus ss = new StepStatus(wss.getStep().getIdentifier(), (wss.getParent() != null ? wss.getParent().getIdentifier() : null), wss.queueSize(), wss.successes(), wss.failures());
            res.add(ss);
        }
        return res;
    }

}

