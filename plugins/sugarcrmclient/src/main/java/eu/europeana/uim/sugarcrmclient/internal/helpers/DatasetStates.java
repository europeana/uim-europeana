/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.internal.helpers;

/**
 * This enumeration holds the State Descriptions for Datasets in Sugar CRM
 * @author Georgios Markakis
 */
public enum DatasetStates {

	OAI_PMH_TESTING("Prospecting","OAI-PHM testing"),
	OAI_PMH_SENT_TO_ORG("Qualification","OAI-PHM test sent to Organisation"),
	READY_FOR_HARVESTING("Needs Analysis","Ready for Harvesting"),
	MAPPING_AND_NORMALIZATION("Value Proposition","Mapping and Normalization"),
	READY_FOR_REPLICATION("Id. Decision Makers","Ready for Replication"),
	ONGOING_SCHEDULED_UPDATES("Updates","Ongoing scheduled updates"),
	INGESTION_COMPLETE("Closed Won","Ingestion complete"),
	DISABLED_AND_REPLACED("Replaced","Disabled and Replaced"),
	HARVESTING_PENDING("Harvesting Pending","Harvesting Pending"),
	;

	private final String sysId;
	private final String description;	
	
	DatasetStates(String sysId, String description){
		this.sysId = sysId;
		this.description = description;
	}

	public String getSysId() {
		return sysId;
	}

	public String getDescription() {
		return description;
	}
	
	
}
