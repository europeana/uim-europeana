package eu.europeana.uim.command;

import org.osgi.service.command.CommandSession;

import eu.europeana.uim.common.ProgressMonitor;

public class ConsoleProgressMonitor implements ProgressMonitor {
	
	private final CommandSession session;

	public ConsoleProgressMonitor(CommandSession session) {
		super();
		this.session = session;
	}

	
	@Override
	public void beginTask(String task, int work) {
		session.getConsole().println("Starting:" + task + ", " + work + " units of work. [");
	}

	@Override
	public void worked(int work) {
		session.getConsole().print(".");
	}

	@Override
	public void done() {
		session.getConsole().println("]");
	}

	@Override
	public void subTask(String subtask) {
	}

	@Override
	public void setCanceled(boolean canceled) {
	}

	@Override
	public boolean isCanceled() {
		return false;
	}

}
