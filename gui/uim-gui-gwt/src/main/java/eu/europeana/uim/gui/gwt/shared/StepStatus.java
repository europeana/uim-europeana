package eu.europeana.uim.gui.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class StepStatus implements IsSerializable {

    private String stepName;
    private int queueSize, successes, failures;

    public StepStatus() {

    }

    public StepStatus(String stepName, int queueSize, int successes, int failures) {
        this.stepName = stepName;
        this.queueSize = queueSize;
        this.successes = successes;
        this.failures = failures;
    }

    public String getStep() {
        return stepName;
    }

    public int queueSize() {
        return queueSize;
    }

    public int successes() {
        return successes;
    }

    public int failures() {
        return failures;
    }
}
