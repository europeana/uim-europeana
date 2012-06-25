package eu.europeana.uim.gui.cp.shared.validation;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Transfer object for links in a record.
 * 
 * @author Rene Wiermer (rene.wiermer@kb.nl)
 * @date Aug 22, 2011
 */
public class LinksResultDTO implements IsSerializable {
    private ArrayList<LinkDTO> links;

    /**
     * Creates a new instance of this class.
     */
    public LinksResultDTO() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param links
     */
    public LinksResultDTO(ArrayList<LinkDTO> links) {
        this.links = links;
    }

    /**
     * @param links
     */
    public void setLinks(ArrayList<LinkDTO> links) {
        this.links = links;
    }

    /**
     * @return links
     */
    public ArrayList<LinkDTO> getLinks() {
        return links;
    }
}