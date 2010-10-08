package eu.europeana.uim;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.karaf.shell.console.OsgiCommandSupport;



@Command(scope = "uim", name = "info", description="Get informations about the UIM.")
public class UIMInfo extends OsgiCommandSupport {

	
	public UIMInfo() {
	}

	protected Object doExecute() throws Exception {
		System.out.println("UIM Registry: " + UIMRegistry.getInstance().toString());
		System.out.println("In storage:" + UIMRegistry.getInstance().getFirstStorage().size());
		return null;
	}

}
