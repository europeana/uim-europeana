package eu.europeana.uim.command;

import java.util.Collections;
import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.osgi.service.command.CommandSession;
import org.osgi.service.command.Function;
import org.springframework.beans.factory.annotation.Autowired;

import eu.europeana.uim.api.Registry;


@Command(name = "uim", scope = "info")
public class UIMInfo implements Function, Action {

    @Autowired
    private Registry registry;

    public UIMInfo() {
	}

	@Override
	public Object execute(CommandSession session) throws Exception {
		return execute(session, Collections.emptyList());
	}

	@Override
    public Object execute(CommandSession commandSession, List<Object> objects) throws Exception {
        System.out.println("UIM Registry: " + registry.toString());
        return null;
    }

	
	/**
	 * @return the registry
	 */
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * @param registry the registry to set
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	
	
}
