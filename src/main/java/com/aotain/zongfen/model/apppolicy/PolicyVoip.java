package com.aotain.zongfen.model.apppolicy;

import java.io.Serializable;
import java.util.Date;

public class PolicyVoip implements Serializable {
    private Integer voipId;

    private Integer interfereType;

    private Integer interfereDir;

    private Long cTime;

    private Short operatetype;

    private String createOper;

    private String modifyOper;

    private Date createTime;

    private Date modifyTime;

    private Integer status;

    private Long messageNo;

    private static final long serialVersionUID = 1L;

    public Integer getVoipId() {
        return voipId;
    }

    public void setVoipId(Integer voipId) {
        this.voipId = voipId;
    }

    public Integer getInterfereType() {
        return interfereType;
    }

    public void setInterfereType(Integer interfereType) {
        this.interfereType = interfereType;
    }

    public Integer getInterfereDir() {
        return interfereDir;
    }

    public void setInterfereDir(Integer interfereDir) {
        this.interfereDir = interfereDir;
    }

    public Long getcTime() {
        return cTime;
    }

    public void setcTime(Long cTime) {
        this.cTime = cTime;
    }

    public Short getOperatetype() {
        return operatetype;
    }

    public void setOperatetype(Short operatetype) {
        this.operatetype = operatetype;
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
        PolicyVoip other = (PolicyVoip) that;
        return (this.getVoipId() == null ? other.getVoipId() == null : this.getVoipId().equals(other.getVoipId()))
            && (this.getInterfereType() == null ? other.getInterfereType() == null : this.getInterfereType().equals(other.getInterfereType()))
            && (this.getInterfereDir() == null ? other.getInterfereDir() == null : this.getInterfereDir().equals(other.getInterfereDir()))
            && (this.getcTime() == null ? other.getcTime() == null : this.getcTime().equals(other.getcTime()))
            && (this.getOperatetype() == null ? other.getOperatetype() == null : this.getOperatetype().equals(other.getOperatetype()))
            && (this.getCreateOper() == null ? other.getCreateOper() == null : this.getCreateOper().equals(other.getCreateOper()))
            && (this.getModifyOper() == null ? other.getModifyOper() == null : this.getModifyOper().equals(other.getModifyOper()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getModifyTime() == null ? other.getModifyTime() == null : this.getModifyTime().equals(other.getModifyTime()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getMessageNo() == null ? other.getMessageNo() == null : this.getMessageNo().equals(other.getMessageNo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getVoipId() == null) ? 0 : getVoipId().hashCode());
        result = prime * result + ((getInterfereType() == null) ? 0 : getInterfereType().hashCode());
        result = prime * result + ((getInterfereDir() == null) ? 0 : getInterfereDir().hashCode());
        result = prime * result + ((getcTime() == null) ? 0 : getcTime().hashCode());
        result = prime * result + ((getOperatetype() == null) ? 0 : getOperatetype().hashCode());
        result = prime * result + ((getCreateOper() == null) ? 0 : getCreateOper().hashCode());
        result = prime * result + ((getModifyOper() == null) ? 0 : getModifyOper().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getModifyTime() == null) ? 0 : getModifyTime().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getMessageNo() == null) ? 0 : getMessageNo().hashCode());
        return result;
    }
}