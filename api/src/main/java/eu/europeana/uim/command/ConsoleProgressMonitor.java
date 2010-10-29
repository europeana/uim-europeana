package eu.europeana.uim.command;

import org.osgi.service.command.CommandSession;

import eu.europeana.uim.common.ProgressMonitor;

public class ConsoleProgressMonitor implements ProgressMonitor {
	
	private final CommandSession session;
	
	private boolean cancelled = false;
	private int worked = 0;

	public ConsoleProgressMonitor(CommandSession session) {
		super();
		this.session = session;
	}
	
	@Override
	public void beginTask(String task, int work) {
		session.getConsole().print("Starting:" + task + ", " + work + " units of work. [");
	}

	@Override
	public void worked(int work) {
		session.getConsole().print(".");
		worked += work;
		if (worked % 10 == 0) {
			session.getConsole().print("|");
		}
	}

	@Override
	public void done() {
		session.getConsole().println("]");
	}

	@Override
	public void subTask(String subtask) {
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

}
