package eu.europeana.uim.workflow.dummy;

import eu.europeana.uim.UIMError;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.api.WorkflowStep;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class DummyWorkflow implements Workflow {

    private final Long id;

    private final List<WorkflowStep> definition = new ArrayList<WorkflowStep>();

    private final Registry registry;

    public DummyWorkflow(Registry registry) {
        this.registry = registry;
        this.id = 0l; // doh. do we need an ID at this stage anyhow?

        // that's a very exciting worklow
        definition.add(registry.getPlugin("eu.europeana.uim.plugin.dummy.DummyPlugin"));
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return "Dummy workflow";
    }

    @Override
    public String getDescription() {
        return "This awesome workflow demonstrates the capabilities of the UIM";
    }

    @Override
    public void addStep(WorkflowStep step) {
        throw new UIMError("Can't modify the dummy workflow");
    }

    @Override
    public List<WorkflowStep> getSteps() {
        return this.definition;
    }
}
