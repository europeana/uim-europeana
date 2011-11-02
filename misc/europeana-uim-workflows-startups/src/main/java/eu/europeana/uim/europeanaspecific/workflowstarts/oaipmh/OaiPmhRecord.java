package eu.europeana.uim.europeanaspecific.workflowstarts.oaipmh;

import org.w3c.dom.Element;

/**
 * Models a metadata record harvested by OAI-PMH
 * 
 */
public class OaiPmhRecord {
    /** String identifier */
    protected String  identifier;
    /** Document metadataDom */
    protected Element metadataDom;
    /** boolean deleted */
    protected boolean deleted = false;

//    protected Element oaiDom;
    
    /** OaiPmhRecord provenanceDom */
    protected Element provenanceDom;
    
    // provenance is not used at the moment

    /**
     * Creates a new instance of this class.
     */
    public OaiPmhRecord() {
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param metadataDom
     * @param deleted
     */
    public OaiPmhRecord(Element metadataDom, boolean deleted) {
        super();
        this.metadataDom = metadataDom;
        this.deleted = deleted;
    }

    /**
     * @return true if the record was deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @return the metadata DOM
     */
    public Element getMetadata() {
        return metadataDom;
    }

    /**
     * @param metadataDom
     */
    public void setMetadata(Element metadataDom) {
        this.metadataDom = metadataDom;
    }

    /**
     * @param deleted
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @return the OAI identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the provenanceDom.
     * @return the provenanceDom
     */
    public Element getProvenance() {
        return provenanceDom;
    }

    /**
     * Sets the provenanceDom to the given value.
     * @param provenanceDom the provenanceDom to set
     */
    public void setProvenance(Element provenanceDom) {
        this.provenanceDom = provenanceDom;
    }
    
}
