package eu.europeana.uim.common;

/** Simple memory based implementation of a ProgressMonitor. This class just
 * holds all information in fields and exposes the field values through getter methods.
 * 
 * @author andreas.juffinger@kb.nl
 */
public class MemoryProgressMonitor implements ProgressMonitor {

	private String task ="Not defined";
	private String subtask ="Not defined";
	
	private int work =0;
	private int worked =0;
	
	private boolean canceled = false;
	

	/** public standard constructor.
	 */
	public MemoryProgressMonitor() {
	}
	
	
	@Override
	public void beginTask(String task, int work) {
		this.task = task;
		this.work = work;
	}

	@Override
	public void worked(int work) {
		this.worked += work;
	}

	@Override
	public void done() {
		this.worked = work;
	}

	@Override
	public void subTask(String subtask) {
		this.subtask = subtask;
	}

	@Override
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * @return the task
	 */
	public String getTask() {
		return task;
	}

	/**
	 * @return the subtask
	 */
	public String getSubtask() {
		return subtask;
	}

	/**
	 * @return the work
	 */
	public int getWork() {
		return work;
	}

	/**
	 * @return the worked
	 */
	public int getWorked() {
		return worked;
	}
}
