/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europeana.uim.gui.cp.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import eu.europeana.uim.gui.cp.shared.CRFFailedRecordReportDTO;
import eu.europeana.uim.gui.cp.shared.CRFFailedTaskDTO;
import eu.europeana.uim.gui.cp.shared.CRFReplyDTO;
import eu.europeana.uim.gui.cp.shared.CRFTaskDTO;
import java.util.List;

/**
 *
 * @author ymamakis
 */
@RemoteServiceRelativePath("media")
public interface CrfReportProxy extends RemoteService{
    List<CRFReplyDTO> getAllActive();
    List<CRFReplyDTO> getByProvider(CRFTaskDTO crfTask);
    List<CRFReplyDTO> getByCollection(CRFTaskDTO crfTask);
    List<CRFFailedRecordReportDTO> getFailedByCollection(CRFTaskDTO crfTask);
    List<CRFFailedRecordReportDTO> getFailedByExecution(CRFFailedTaskDTO crfFailedTask);
}
