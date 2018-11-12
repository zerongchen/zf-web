package com.aotain.zongfen.model.useranalysis;

import com.aotain.zongfen.utils.CacheUtil;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DDoS异常流量攻击详单实体类
 *
 * @author daiyh@aotain.com
 * @date 2018/06/22
 */
@Getter
@Setter
public class DDoSUbasDetail {
    private Long attackStartTime;
    private Long attackEndTime;
    private Long userGroupNo;
    private Integer appAttackType;
    private Long appAttackTraffic;
    private Long appAttackRate;
    private String attackAreaName;
    private Integer attackAreaNum;

    private Integer sourceIpNum;
    private Integer probeType;
    private String areaId;
    private Long receivedTime;
    private String receivedIp;
    private String sendIp;
    private String softwareProvider;

    private String userGroupName;
    private String appAttackTypeName;

    private String formatAttackStartTime;
    private String formatAttackEndTime;

    public void setAttackStartTime(long attackStartTime) {
        this.attackStartTime = attackStartTime;
        Date date = new Date(attackStartTime*1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formatTime = simpleDateFormat.format(date);
        this.formatAttackStartTime = formatTime;
    }

    public void setAttackEndTime(long attackEndTime) {
        this.attackEndTime = attackEndTime;
        Date date = new Date(attackEndTime*1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formatTime = simpleDateFormat.format(date);
        this.formatAttackEndTime = formatTime;
    }

    public void setUserGroupNo(long userGroupNo){
        this.userGroupNo = userGroupNo;
        this.userGroupName = CacheUtil.getGroupNameByNo(userGroupNo);
    }

    public void setAppAttackType(int appAttackType){
        this.appAttackType = appAttackType;
        if (appAttackType==1){
            appAttackTypeName = "ICMP Redirection";
        } else if (appAttackType==2){
            appAttackTypeName = "ICMP Unreacheble";
        } else if (appAttackType==3){
            appAttackTypeName = "TCP anomaly Connection";
        } else if (appAttackType==4){
            appAttackTypeName = "DNS Query Application Attack";
        } else if (appAttackType==5){
            appAttackTypeName = "DNS Reply Application Attack";
        } else if (appAttackType==6){
            appAttackTypeName = "SIP Application Attack";
        } else if (appAttackType==7){
            appAttackTypeName = "HTTPS Application Attack";
        } else if (appAttackType==8){
            appAttackTypeName = "HTTP Get Application Attack";
        } else if (appAttackType==9){
            appAttackTypeName = "Challenge Collapsar Attack";
        }
    }

    public static void main(String[] args) {
        Date date = new Date(1529855999000L);
        System.out.println(date);
    }
}
