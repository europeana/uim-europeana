package eu.europeana.uim.gui.gwt.shared;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class Collection extends DataSource {

    private String name;
    private Provider provider;

    public Collection(Long id, String name, Provider provider) {
        super(id);
        this.name = name;
        this.provider = provider;
    }

    public Collection() {
        super();
    }

    public String getName() {
        return name;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

}
