/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.plugin.objects.queries;

import java.util.List;

import eu.europeana.uim.sugarcrmclient.plugin.objects.data.SugarCrmField;

/**
 * 
 * 
 * @author Georgios Markakis
 */
public interface SugarCrmQuery {

	public void setFieldsToRetrieve(List<SugarCrmField> fieldsToRetrieve);
	
	public List<SugarCrmField> getFieldsToRetrieve();
	
	public void setOrderBy(SugarCrmField orderBy);
	
	public SugarCrmField getOrderBy();
	
	public void setMaxResults(int maxResults);
	
	public int getMaxResults();
	
	public void setOffset(int offset);
	
	public int getOffset(); 
	
}
