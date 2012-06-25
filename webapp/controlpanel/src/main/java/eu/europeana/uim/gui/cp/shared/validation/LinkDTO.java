package eu.europeana.uim.gui.cp.shared.validation;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a single link
 * 
 * @author Rene Wiermer (rene.wiermer@kb.nl)
 * @date Aug 22, 2011
 */
public class LinkDTO implements IsSerializable {
    private String url;
    private String description;

    /**
     * Creates a new instance of this class.
     */
    public LinkDTO() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param url
     * @param description
     */
    public LinkDTO(String url, String description) {
        this.setUrl(url);
        this.setDescription(description);
    }

    /**
     * Sets the url to the given value.
     * 
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }
}