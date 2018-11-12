package com.aotain.zongfen.model.apppolicy;

import java.io.Serializable;

public class VoipIp implements Serializable {
    private Long voipipSeq;

    private Long voipId;

    private String voipIp;

    private Short voipType;

    private static final long serialVersionUID = 1L;

    public Long getVoipipSeq() {
        return voipipSeq;
    }

    public void setVoipipSeq(Long voipipSeq) {
        this.voipipSeq = voipipSeq;
    }

    public Long getVoipId() {
        return voipId;
    }

    public void setVoipId(Long voipId) {
        this.voipId = voipId;
    }

    public String getVoipIp() {
        return voipIp;
    }

    public void setVoipIp(String voipIp) {
        this.voipIp = voipIp == null ? null : voipIp.trim();
    }

    public Short getVoipType() {
        return voipType;
    }

    public void setVoipType(Short voipType) {
        this.voipType = voipType;
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
        VoipIp other = (VoipIp) that;
        return (this.getVoipipSeq() == null ? other.getVoipipSeq() == null : this.getVoipipSeq().equals(other.getVoipipSeq()))
            && (this.getVoipId() == null ? other.getVoipId() == null : this.getVoipId().equals(other.getVoipId()))
            && (this.getVoipIp() == null ? other.getVoipIp() == null : this.getVoipIp().equals(other.getVoipIp()))
            && (this.getVoipType() == null ? other.getVoipType() == null : this.getVoipType().equals(other.getVoipType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getVoipipSeq() == null) ? 0 : getVoipipSeq().hashCode());
        result = prime * result + ((getVoipId() == null) ? 0 : getVoipId().hashCode());
        result = prime * result + ((getVoipIp() == null) ? 0 : getVoipIp().hashCode());
        result = prime * result + ((getVoipType() == null) ? 0 : getVoipType().hashCode());
        return result;
    }
}