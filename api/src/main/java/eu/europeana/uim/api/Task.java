package eu.europeana.uim.api;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface Task extends Runnable {

    TaskStatus getStatus();
        
    void markDone();
}
