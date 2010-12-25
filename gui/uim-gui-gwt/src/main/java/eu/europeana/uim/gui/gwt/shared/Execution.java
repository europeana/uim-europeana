package eu.europeana.uim.gui.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class Execution implements IsSerializable {

    private Long id;

    public Execution() {
    }

    public Execution(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
