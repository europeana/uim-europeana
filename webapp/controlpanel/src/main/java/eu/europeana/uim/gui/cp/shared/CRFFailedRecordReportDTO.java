/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europeana.uim.gui.cp.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DTO for failed records report 
 * @author yorgos.mamakis@ europeana.eu
 */
public class CRFFailedRecordReportDTO extends CRFTaskDTO implements IsSerializable{
    

    private String recordId;
    private long processingDate;
    private String url;
    private String httpStatusCode;
    private String edmField;
    private String typeOfJob;

    public String getEdmField() {
        return edmField;
    }

    public void setEdmField(String edmField) {
        this.edmField = edmField;
    }

    public String getTypeOfJob() {
        return typeOfJob;
    }

    public void setTypeOfJob(String typeOfJob) {
        this.typeOfJob = typeOfJob;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public long getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(long processingDate) {
        this.processingDate = processingDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(String httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
    
    
}
