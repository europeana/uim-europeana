package eu.europeana.uim.common;

import java.util.logging.Level;
import java.util.logging.Logger;

/** Simple memory based implementation of a ProgressMonitor. This class just
 * holds all information in fields and exposes the field values through getter methods.
 * 
 * @author andreas.juffinger@kb.nl
 */
public class LoggingProgressMonitor implements ProgressMonitor {

	private final static Logger log = Logger.getLogger(LoggingProgressMonitor.class.getName());
	
	private Level level;
	private int logfrq = 100;
	
	private String task ="Not defined";
	private String subtask ="Not defined";
	
	private int work =0;
	private int worked =0;
	
	private boolean cancelled = false;
	

	/**
	 * Creates a new instance of this class logging progress with 
	 * the defined logging level.
	 * 
	 * @param level
	 */
	public LoggingProgressMonitor(Level level) {
		this.level = level;
	}
	
	
	/**
	 * Creates a new instance of this class with the given 
	 * log level and log frequency
	 * @param level
	 * @param logfrq
	 */
	public LoggingProgressMonitor(Level level, int logfrq) {
		this.level = level;
		this.logfrq = logfrq;
	}
	
	
	@Override
	public void beginTask(String task, int work) {
		this.task = task;
		this.work = work;
		log.log(level, "Begin task: <" + task + "> " + work + " units of work.");
	}

	@Override
	public void worked(int work) {
		this.worked += work;
		
		if (this.worked % logfrq == 0) {
			log.log(level, String.format("%d units of worked. So far %d done.", logfrq, worked));
		}
	}

	@Override
	public void done() {
		log.log(level, String.format("%d units done.", worked));
		this.worked = work;
	}

	@Override
	public void subTask(String subtask) {
		this.subtask = subtask;
		log.log(level, String.format("%d units of worked. Start subtask: <" + subtask +">", worked));
	}

	@Override
	public void setCancelled(boolean canceled) {
		this.cancelled = canceled;

		log.log(level, String.format("%d units of worked. CANCEL: <" + canceled +">", worked));
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

}
