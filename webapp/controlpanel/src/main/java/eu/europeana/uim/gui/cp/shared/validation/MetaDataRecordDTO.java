/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.uim.gui.cp.shared.validation;

import com.google.gwt.view.client.ProvidesKey;
import eu.europeana.uim.gui.cp.shared.DataSourceDTO;

/**
 * Collection data object used in GWT for visualization.
 * 
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since Apr 28, 2011
 */
public class MetaDataRecordDTO extends DataSourceDTO {
    /**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<MetaDataRecordDTO> KEY_PROVIDER = new ProvidesKey<MetaDataRecordDTO>() {
                                                                        @Override
                                                                        public Object getKey(
                                                                                MetaDataRecordDTO item) {
                                                                            return item == null
                                                                                    ? null
                                                                                    : item.getId();
                                                                        }
                                                                    };

    private String                                     title;

    private String                                     importuimdate;
    
    private String                                     updateuimdate;
    
    private String                                     firstingestiondate;
    
    private String                                     ingestionupdatedate;

    private boolean                                    deleted;
    
    /**
     * Creates a new instance of this class.
     */
    public MetaDataRecordDTO() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param id
     * @param title
     * @param creator
     * @param contributor
     * @param publicationYear
     * @param workLanguage
     * @param publicationPlace
     */
    public MetaDataRecordDTO(Long id, String title) {
        super(id);
        this.title = title;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }



	/**
	 * @return the deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return the importuimdate
	 */
	public String getImportuimdate() {
		return importuimdate;
	}

	/**
	 * @param importuimdate the importuimdate to set
	 */
	public void setImportuimdate(String importuimdate) {
		this.importuimdate = importuimdate;
	}

	/**
	 * @return the updateuimdate
	 */
	public String getUpdateuimdate() {
		return updateuimdate;
	}

	/**
	 * @param updateuimdate the updateuimdate to set
	 */
	public void setUpdateuimdate(String updateuimdate) {
		this.updateuimdate = updateuimdate;
	}

	/**
	 * @return the firstingestiondate
	 */
	public String getFirstingestiondate() {
		return firstingestiondate;
	}

	/**
	 * @param firstingestiondate the firstingestiondate to set
	 */
	public void setFirstingestiondate(String firstingestiondate) {
		this.firstingestiondate = firstingestiondate;
	}

	/**
	 * @return the ingestionupdatedate
	 */
	public String getIngestionupdatedate() {
		return ingestionupdatedate;
	}

	/**
	 * @param ingestionupdatedate the ingestionupdatedate to set
	 */
	public void setIngestionupdatedate(String ingestionupdatedate) {
		this.ingestionupdatedate = ingestionupdatedate;
	}


}