package com.aotain.zongfen.dto.general;

import java.io.Serializable;
import java.sql.Timestamp;

import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.general.ClassInfoType;

public class ClassInfoDTO implements Serializable {
    private String className;

    private Integer classId;

    private Integer classType;

    private String classFileName;

    private Integer classFileTypeId;

    private String serverIp;

    private Integer serverPort;

    private String sftpUsername;

    private String sftpPassword;

    private Integer bindUserType;

    private Integer operateType;

    private String statusDec;//状态，用于前端展示

    private Timestamp modifyTime;

    private String modifyTimeStr; //时间字符串，用于前端展示

    private Timestamp createTime;

    private String createTimeStr; //时间字符串，用于前端展示

    private String createOper;

    private String modifyOper;

    private static final long serialVersionUID = 1L;

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getClassType() {
        return classType;
    }

    public void setClassType(Integer classType) {
        this.classType = classType;
    }

    public String getClassFileName() {
        return classFileName;
    }

    public void setClassFileName(String classFileName) {
        this.classFileName = classFileName == null ? null : classFileName.trim();
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp == null ? null : serverIp.trim();
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getSftpUsername() {
        return sftpUsername;
    }

    public void setSftpUsername(String sftpUsername) {
        this.sftpUsername = sftpUsername == null ? null : sftpUsername.trim();
    }

    public String getSftpPassword() {
        return sftpPassword;
    }

    public void setSftpPassword(String sftpPassword) {
        this.sftpPassword = sftpPassword == null ? null : sftpPassword.trim();
    }

    public Integer getBindUserType() {
        return bindUserType;
    }

    public void setBindUserType(Integer bindUserType) {
        this.bindUserType = bindUserType;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
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

    public String getStatusDec() {
        if(getOperateType()==0){
            return "生效";
        }
        return "失效";
    }

    public void setStatusDec( String statusDec ) {
        this.statusDec = statusDec;
    }

    public String getModifyTimeStr() {
        return DateUtils.getDateTime(getModifyTime());
    }

    public void setModifyTimeStr( String modifyTimeStr ) {
        this.modifyTimeStr = modifyTimeStr;
    }

    public String getCreateTimeStr() {
        return DateUtils.getDateTime(getCreateTime());
    }

    public void setCreateTimeStr( String createTimeStr ) {
        this.createTimeStr = createTimeStr;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    public Integer getClassFileTypeId() {
        return ClassInfoType.getclassInfoTypeIdByName(getClassFileName());
    }

    public void setClassFileTypeId( Integer classFileTypeId ) {
        this.classFileTypeId = classFileTypeId;
    }

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (!(o instanceof ClassInfoDTO)) return false;

        ClassInfoDTO that = (ClassInfoDTO) o;

        if (!getClassId().equals(that.getClassId())) return false;
        if (!getClassType().equals(that.getClassType())) return false;
        if (!getClassFileName().equals(that.getClassFileName())) return false;
        if (!getServerIp().equals(that.getServerIp())) return false;
        if (!getServerPort().equals(that.getServerPort())) return false;
        if (!getSftpUsername().equals(that.getSftpUsername())) return false;
        if (!getSftpPassword().equals(that.getSftpPassword())) return false;
        if (!getBindUserType().equals(that.getBindUserType())) return false;
        if (!getOperateType().equals(that.getOperateType())) return false;
        if (!getStatusDec().equals(that.getStatusDec())) return false;
        if (!getModifyTime().equals(that.getModifyTime())) return false;
        if (!getModifyTimeStr().equals(that.getModifyTimeStr())) return false;
        if (!getCreateTime().equals(that.getCreateTime())) return false;
        if (!getCreateTimeStr().equals(that.getCreateTimeStr())) return false;
        if (!getCreateOper().equals(that.getCreateOper())) return false;
        if (!getClassName().equals(that.getClassName())) return false;
        if (!getClassFileTypeId().equals(that.getClassFileTypeId())) return false;
        return getModifyOper().equals(that.getModifyOper());
    }

    @Override
    public int hashCode() {
        int result = getClassId().hashCode();
        result = 31 * result + getClassType().hashCode();
        result = 31 * result + getClassFileName().hashCode();
        result = 31 * result + getServerIp().hashCode();
        result = 31 * result + getServerPort().hashCode();
        result = 31 * result + getSftpUsername().hashCode();
        result = 31 * result + getSftpPassword().hashCode();
        result = 31 * result + getBindUserType().hashCode();
        result = 31 * result + getOperateType().hashCode();
        result = 31 * result + getStatusDec().hashCode();
        result = 31 * result + getModifyTime().hashCode();
        result = 31 * result + getModifyTimeStr().hashCode();
        result = 31 * result + getCreateTime().hashCode();
        result = 31 * result + getCreateTimeStr().hashCode();
        result = 31 * result + getCreateOper().hashCode();
        result = 31 * result + getModifyOper().hashCode();
        result = 31 * result + getClassName().hashCode();
        result = 31 * result + getClassFileTypeId().hashCode();
        return result;
    }
}