package com.aotain.zongfen.utils.general;


public enum UserType {

    /**
     * 跟前端保持一致
     */
    ACCOUNT_USER(1,"账号用户"),
    IPADDRESS_USER(2,"IP地址段用户"),
    DPIGROUP_USER(3,"按DPI链路分组"),
    BRAS_IP_USER(4,"BRAS IP 地址段用户组");


    private int value;
    private String desc;
    UserType(int value,String desc){
        this.value=value;
        this.desc=desc;
    }

    public int getValue() {
        return value;
    }

    public void setValue( int value ) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc( String desc ) {
        this.desc = desc;
    }

    /**
     * return usertype chinese desc
     * @param value
     * @return
     */
    public static String getUserTypeDesc(int value){
        for(UserType user: UserType.values()){
            if(user.getValue()==value){
                return user.getDesc();
            }
        }
    return null;
    }

    /**
     * return usertype
     * @param value
     * @return
     */
    public static Integer getUserType(String value){
        for(UserType user: UserType.values()){
            if(value.equals(user.getDesc())){
                return user.getValue();
            }
        }
        return 0;
    }

}
