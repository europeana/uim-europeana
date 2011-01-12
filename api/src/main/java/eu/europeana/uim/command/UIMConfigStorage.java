package eu.europeana.uim.command;

import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.osgi.service.command.CommandSession;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */

@Command(name = "uimconfig", scope = "storage")
public class UIMConfigStorage implements Action {

    @Argument
    String storage;

    private Registry registry;

    public UIMConfigStorage(Registry registry) {
        this.registry = registry;
    }

    @Override
    public Object execute(CommandSession session) throws Exception {

        if (storage == null) {
            session.getConsole().println("Available storage engines: ");
            // list available storage engines
            for(StorageEngine s : registry.getStorages()) {
                session.getConsole().println(" * " + s.getIdentifier());
            }
        } else {
            StorageEngine selected = null;
            for(StorageEngine s : registry.getStorages()) {
                if(s.getIdentifier().equals(storage)) {
                    selected = s;
                    break;
                }
            }
            if(selected != null) {
                registry.setActiveStorage(selected);
                session.getConsole().println("Activated storage engine '" + selected.getIdentifier() + "'");
            } else {
                session.getConsole().println("Could not find storage engine with identifier '" + storage + "'");
            }
        }
        return null;
    }
}
