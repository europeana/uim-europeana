/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europeana.uim.gui.cp.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DTO for the initial CRF statistics reply
 * @author yorgos.mamakis@ europeana.eu
 */
public class CRFReplyDTO extends CRFFailedTaskDTO implements IsSerializable{
  
    private long pending;
    private long successful;
    private long failed;
    private long totalJobs;
    private long totalRecords;
    private long withIsShownAt;
    private long withSuccessfulIsShownAt;
    private long startDate;
    private long endDate;
    private long withThumbnails;
    private long withSuccessfulThumbnails;
    private long withMedia;
    private long withSuccessfulMedia;
    private long withIsShownBy;
    private long withSuccessfulIsShownBy;
    private long withHasView;
    private long withSuccessfulHasView;

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getWithThumbnails() {
        return withThumbnails;
    }

    public void setWithThumbnails(long withThumbnails) {
        this.withThumbnails = withThumbnails;
    }

    public long getWithSuccessfulThumbnails() {
        return withSuccessfulThumbnails;
    }

    public void setWithSuccessfulThumbnails(long withSuccessfulThumbnails) {
        this.withSuccessfulThumbnails = withSuccessfulThumbnails;
    }

    public long getWithMedia() {
        return withMedia;
    }

    public void setWithMedia(long withMedia) {
        this.withMedia = withMedia;
    }

    public long getWithSuccessfulMedia() {
        return withSuccessfulMedia;
    }

    public void setWithSuccessfulMedia(long withSuccessfulMedia) {
        this.withSuccessfulMedia = withSuccessfulMedia;
    }

    public long getWithIsShownBy() {
        return withIsShownBy;
    }

    public void setWithIsShownBy(long withIsShownBy) {
        this.withIsShownBy = withIsShownBy;
    }

    public long getWithSuccessfulIsShownBy() {
        return withSuccessfulIsShownBy;
    }

    public void setWithSuccessfulIsShownBy(long withSuccessfulIsShownBy) {
        this.withSuccessfulIsShownBy = withSuccessfulIsShownBy;
    }

    public long getWithHasView() {
        return withHasView;
    }

    public void setWithHasView(long withHasView) {
        this.withHasView = withHasView;
    }

    public long getWithSuccessfulHasView() {
        return withSuccessfulHasView;
    }

    public void setWithSuccessfulHasView(long withSuccessfulHasView) {
        this.withSuccessfulHasView = withSuccessfulHasView;
    }

    public long getWithIsShownAt() {
        return withIsShownAt;
    }

    public void setWithIsShownAt(long withIsShownAt) {
        this.withIsShownAt = withIsShownAt;
    }

    public long getWithSuccessfulIsShownAt() {
        return withSuccessfulIsShownAt;
    }

    public void setWithSuccessfulIsShownAt(long withSuccessfulIsShownAt) {
        this.withSuccessfulIsShownAt = withSuccessfulIsShownAt;
    }
    

    public long getPending() {
        return pending;
    }

    public void setPending(long pending) {
        this.pending = pending;
    }

    public long getSuccessful() {
        return successful;
    }

    public void setSuccessful(long successful) {
        this.successful = successful;
    }

    public long getFailed() {
        return failed;
    }

    public void setFailed(long failed) {
        this.failed = failed;
    }

    public long getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(long totalJobs) {
        this.totalJobs = totalJobs;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    
}
