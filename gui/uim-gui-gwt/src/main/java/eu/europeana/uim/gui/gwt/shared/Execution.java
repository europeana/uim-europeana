package eu.europeana.uim.gui.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class Execution implements IsSerializable {

    private Long id;

    private String name;

    private Integer progress;

    private Integer total;

    public Execution() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public boolean isDone() {
        return progress.equals(total);
    }
}
