package com.aotain.zongfen.model.apppolicy;

import java.io.Serializable;
import java.util.Date;

public class PolicyWebPush implements Serializable {
    private Integer advId;

    private Short advType;

    private Long advWhitehostlistid;

    private String triggerHostlistid;

    private String triggerKwlistid;

    private String advframeUrl;

    private Long advToken;

    private Long advDelay;

    private Short operatetype;

    private String messageName;

    private Date createTime;

    private Date modifyTime;

    private String createOper;

    private String modifyOper;

    private Integer status;

    private Long messageNo;

    private static final long serialVersionUID = 1L;

    public Integer getAdvId() {
        return advId;
    }

    public void setAdvId(Integer advId) {
        this.advId = advId;
    }

    public Short getAdvType() {
        return advType;
    }

    public void setAdvType(Short advType) {
        this.advType = advType;
    }

    public Long getAdvWhitehostlistid() {
        return advWhitehostlistid;
    }

    public void setAdvWhitehostlistid(Long advWhitehostlistid) {
        this.advWhitehostlistid = advWhitehostlistid;
    }

    public String getTriggerHostlistid() {
        return triggerHostlistid;
    }

    public void setTriggerHostlistid(String triggerHostlistid) {
        this.triggerHostlistid = triggerHostlistid == null ? null : triggerHostlistid.trim();
    }

    public String getTriggerKwlistid() {
        return triggerKwlistid;
    }

    public void setTriggerKwlistid(String triggerKwlistid) {
        this.triggerKwlistid = triggerKwlistid == null ? null : triggerKwlistid.trim();
    }

    public String getAdvframeUrl() {
        return advframeUrl;
    }

    public void setAdvframeUrl(String advframeUrl) {
        this.advframeUrl = advframeUrl == null ? null : advframeUrl.trim();
    }

    public Long getAdvToken() {
        return advToken;
    }

    public void setAdvToken(Long advToken) {
        this.advToken = advToken;
    }

    public Long getAdvDelay() {
        return advDelay;
    }

    public void setAdvDelay(Long advDelay) {
        this.advDelay = advDelay;
    }

    public Short getOperatetype() {
        return operatetype;
    }

    public void setOperatetype(Short operatetype) {
        this.operatetype = operatetype;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName == null ? null : messageName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getCreateOper() {
        return createOper;
    }

    public void setCreateOper(String createOper) {
        this.createOper = createOper == null ? null : createOper.trim();
    }

    public String getModifyOper() {
        return modifyOper;
    }

    public void setModifyOper(String modifyOper) {
        this.modifyOper = modifyOper == null ? null : modifyOper.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getMessageNo() {
        return messageNo;
    }

    public void setMessageNo(Long messageNo) {
        this.messageNo = messageNo;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        PolicyWebPush other = (PolicyWebPush) that;
        return (this.getAdvId() == null ? other.getAdvId() == null : this.getAdvId().equals(other.getAdvId()))
            && (this.getAdvType() == null ? other.getAdvType() == null : this.getAdvType().equals(other.getAdvType()))
            && (this.getAdvWhitehostlistid() == null ? other.getAdvWhitehostlistid() == null : this.getAdvWhitehostlistid().equals(other.getAdvWhitehostlistid()))
            && (this.getTriggerHostlistid() == null ? other.getTriggerHostlistid() == null : this.getTriggerHostlistid().equals(other.getTriggerHostlistid()))
            && (this.getTriggerKwlistid() == null ? other.getTriggerKwlistid() == null : this.getTriggerKwlistid().equals(other.getTriggerKwlistid()))
            && (this.getAdvframeUrl() == null ? other.getAdvframeUrl() == null : this.getAdvframeUrl().equals(other.getAdvframeUrl()))
            && (this.getAdvToken() == null ? other.getAdvToken() == null : this.getAdvToken().equals(other.getAdvToken()))
            && (this.getAdvDelay() == null ? other.getAdvDelay() == null : this.getAdvDelay().equals(other.getAdvDelay()))
            && (this.getOperatetype() == null ? other.getOperatetype() == null : this.getOperatetype().equals(other.getOperatetype()))
            && (this.getMessageName() == null ? other.getMessageName() == null : this.getMessageName().equals(other.getMessageName()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getModifyTime() == null ? other.getModifyTime() == null : this.getModifyTime().equals(other.getModifyTime()))
            && (this.getCreateOper() == null ? other.getCreateOper() == null : this.getCreateOper().equals(other.getCreateOper()))
            && (this.getModifyOper() == null ? other.getModifyOper() == null : this.getModifyOper().equals(other.getModifyOper()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getMessageNo() == null ? other.getMessageNo() == null : this.getMessageNo().equals(other.getMessageNo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAdvId() == null) ? 0 : getAdvId().hashCode());
        result = prime * result + ((getAdvType() == null) ? 0 : getAdvType().hashCode());
        result = prime * result + ((getAdvWhitehostlistid() == null) ? 0 : getAdvWhitehostlistid().hashCode());
        result = prime * result + ((getTriggerHostlistid() == null) ? 0 : getTriggerHostlistid().hashCode());
        result = prime * result + ((getTriggerKwlistid() == null) ? 0 : getTriggerKwlistid().hashCode());
        result = prime * result + ((getAdvframeUrl() == null) ? 0 : getAdvframeUrl().hashCode());
        result = prime * result + ((getAdvToken() == null) ? 0 : getAdvToken().hashCode());
        result = prime * result + ((getAdvDelay() == null) ? 0 : getAdvDelay().hashCode());
        result = prime * result + ((getOperatetype() == null) ? 0 : getOperatetype().hashCode());
        result = prime * result + ((getMessageName() == null) ? 0 : getMessageName().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getModifyTime() == null) ? 0 : getModifyTime().hashCode());
        result = prime * result + ((getCreateOper() == null) ? 0 : getCreateOper().hashCode());
        result = prime * result + ((getModifyOper() == null) ? 0 : getModifyOper().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getMessageNo() == null) ? 0 : getMessageNo().hashCode());
        return result;
    }
}