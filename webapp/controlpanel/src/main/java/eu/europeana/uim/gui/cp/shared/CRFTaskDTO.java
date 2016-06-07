/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europeana.uim.gui.cp.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DTO for retrieving statistics for datasets
 * @author yorgos.mamakis@ europeana.eu
 */
public class CRFTaskDTO implements IsSerializable{
    
    private String providerId;
    private String datasetId;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }


}
