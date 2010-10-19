package eu.europeana.uim;

import org.osgi.service.command.CommandSession;
import org.osgi.service.command.Function;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class UIMInfo implements Function {

    @Autowired
    private Registry registry;

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public UIMInfo() {
	}

    @Override
    public Object execute(CommandSession commandSession, List<Object> objects) throws Exception {
        System.out.println("UIM Registry: " + registry.toString());
        System.out.println("In storage:" + registry.getFirstStorage().size());
        return null;
    }
}
