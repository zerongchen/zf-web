package com.aotain.zongfen.model.device;

import java.io.Serializable;

public class DpiRecHouse implements Serializable {
    private Integer dpiId;

    private String houseId;

    private static final long serialVersionUID = 1L;

    public Integer getDpiId() {
        return dpiId;
    }

    public void setDpiId(Integer dpiId) {
        this.dpiId = dpiId;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId == null ? null : houseId.trim();
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
        DpiRecHouse other = (DpiRecHouse) that;
        return (this.getDpiId() == null ? other.getDpiId() == null : this.getDpiId().equals(other.getDpiId()))
            && (this.getHouseId() == null ? other.getHouseId() == null : this.getHouseId().equals(other.getHouseId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDpiId() == null) ? 0 : getDpiId().hashCode());
        result = prime * result + ((getHouseId() == null) ? 0 : getHouseId().hashCode());
        return result;
    }
}