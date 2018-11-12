package com.aotain.zongfen.model.device;

import java.io.Serializable;
import java.util.Date;

public class IdcHouse implements Serializable {

    private String houseId;

    private String houseName;

    private Date createTime;

    public IdcHouse(){
        super();
    }

    public IdcHouse(String houseId,String houseName){
        super();
        this.houseId =houseId;
        this.houseName =houseName;
        this.createTime = new Date();
    }

    public IdcHouse(String houseId,String houseName,Date createTime){
        super();
        this.houseId =houseId;
        this.houseName =houseName;
        this.createTime = createTime;
    }

    private static final long serialVersionUID = 1L;

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId == null ? null : houseId.trim();
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName == null ? null : houseName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        if (createTime==null){
            this.createTime = new Date();
        }
        this.createTime = createTime;
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
        IdcHouse other = (IdcHouse) that;
        return (this.getHouseId() == null ? other.getHouseId() == null : this.getHouseId().equals(other.getHouseId()))
            && (this.getHouseName() == null ? other.getHouseName() == null : this.getHouseName().equals(other.getHouseName()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getHouseId() == null) ? 0 : getHouseId().hashCode());
        result = prime * result + ((getHouseName() == null) ? 0 : getHouseName().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }
}