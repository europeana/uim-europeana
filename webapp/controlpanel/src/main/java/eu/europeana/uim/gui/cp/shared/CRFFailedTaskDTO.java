/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europeana.uim.gui.cp.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Request for failed Jobs
 * @author yorgos.mamakis@ europeana.eu
 */
public class CRFFailedTaskDTO extends CRFTaskDTO implements IsSerializable{
    
    private String executionId;

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
    
}
