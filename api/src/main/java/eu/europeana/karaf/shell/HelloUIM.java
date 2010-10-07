package eu.europeana.karaf.shell;

import eu.europeana.uim.Orchestrator;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.karaf.shell.console.OsgiCommandSupport;


@Command(scope = "test", name = "uim", description="Get informations about the UIM.")
public class HelloUIM extends OsgiCommandSupport {

	private Orchestrator orchestrator;
	
	public HelloUIM() {
		// TODO Auto-generated constructor stub
	}

	protected Object doExecute() throws Exception {
		System.out.println("UIM Information:" + orchestrator);
		return null;
	}

	public Orchestrator getOrchestrator() {
		return orchestrator;
	}

	public void setOrchestrator(Orchestrator orchestrator) {
		this.orchestrator = orchestrator;
	}

}
