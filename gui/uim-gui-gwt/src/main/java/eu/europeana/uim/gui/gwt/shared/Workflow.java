package eu.europeana.uim.gui.gwt.shared;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class Workflow extends DataSource {

    private String name;
    private String description;

    public Workflow(Long id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public Workflow() {
        super();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
