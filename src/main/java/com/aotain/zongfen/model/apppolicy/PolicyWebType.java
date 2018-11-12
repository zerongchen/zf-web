package com.aotain.zongfen.model.apppolicy;

import java.io.Serializable;

public class PolicyWebType implements Serializable {
    private Long webflowId;

    private Integer cwebtype;

    private static final long serialVersionUID = 1L;

    public Long getWebflowId() {
        return webflowId;
    }

    public void setWebflowId(Long webflowId) {
        this.webflowId = webflowId;
    }

    public Integer getCwebtype() {
        return cwebtype;
    }

    public void setCwebtype(Integer cwebtype) {
        this.cwebtype = cwebtype;
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
        PolicyWebType other = (PolicyWebType) that;
        return (this.getWebflowId() == null ? other.getWebflowId() == null : this.getWebflowId().equals(other.getWebflowId()))
            && (this.getCwebtype() == null ? other.getCwebtype() == null : this.getCwebtype().equals(other.getCwebtype()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getWebflowId() == null) ? 0 : getWebflowId().hashCode());
        result = prime * result + ((getCwebtype() == null) ? 0 : getCwebtype().hashCode());
        return result;
    }
}