/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.europeana.uim.sugarcrmclient.plugin.objects.queries;

import java.util.List;

import eu.europeana.uim.sugarcrmclient.plugin.objects.data.DatasetStates;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.SugarCrmField;


/**
 * 
 * @author Georgios Markakis
 */
public class SimpleSugarCrmQuery implements SugarCrmQuery{

	private List<SugarCrmField> fieldsToRetrieve;
	private DatasetStates status;
	
	private SugarCrmField orderBy;
	private int maxResults;
	private int offset;
	
	@Override
	public String toString(){
		String requestQuery = "(opportunities.sales_stage LIKE '" + status.getSysId() +"')";
		
		return requestQuery;
	}

	/**
	 * @param fieldsToRetrieve the fieldsToRetrieve to set
	 */
	public void setFieldsToRetrieve(List<SugarCrmField> fieldsToRetrieve) {
		this.fieldsToRetrieve = fieldsToRetrieve;
	}

	/**
	 * @return the fieldsToRetrieve
	 */
	public List<SugarCrmField> getFieldsToRetrieve() {
		return fieldsToRetrieve;
	}

	/**
	 * @param orderBy the orderBy to set
	 */
	public void setOrderBy(SugarCrmField orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * @return the orderBy
	 */
	public SugarCrmField getOrderBy() {
		return orderBy;
	}

	/**
	 * @param maxResults the maxResults to set
	 */
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * @return the maxResults
	 */
	public int getMaxResults() {
		return maxResults;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(DatasetStates status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public DatasetStates getStatus() {
		return status;
	}
}
