package eu.europeana.uim.gui.cp.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import eu.europeana.uim.gui.cp.shared.CRFFailedRecordReportDTO;
import eu.europeana.uim.gui.cp.shared.CRFFailedTaskDTO;
import eu.europeana.uim.gui.cp.shared.CRFReplyDTO;
import eu.europeana.uim.gui.cp.shared.CRFTaskDTO;

import java.util.List;

public interface CrfReportProxyAsync {
    void getAllActive(AsyncCallback<List<CRFReplyDTO>> async);

    void getByProvider(CRFTaskDTO crfTask, AsyncCallback<List<CRFReplyDTO>> async);

    void getByCollection(CRFTaskDTO crfTask, AsyncCallback<List<CRFReplyDTO>> async);

    void getFailedByCollection(CRFTaskDTO crfTask, AsyncCallback<List<CRFFailedRecordReportDTO>> async);

    void getFailedByExecution(CRFFailedTaskDTO crfFailedTask, AsyncCallback<List<CRFFailedRecordReportDTO>> async);
}
