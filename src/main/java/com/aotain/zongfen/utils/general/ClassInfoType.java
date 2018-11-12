package com.aotain.zongfen.utils.general;

public enum  ClassInfoType {

//    1=协议特征库，2=URL分类库，3=IP地址库，4=应用名称对应表，5=httpget报文域名白名单，6=httpget报文域名黑名单，7=httpget报文url黑名单
    AGREEMENT_CLASS(1,"协议特征库","协议特征库.txt"),
    URL_CLASS(2,"WEB分类库","WEB分类库.txt"),
    IPADDRESS_CLASS(3,"IP地址库","IP地址库.txt"),
    APP_CLASS(4,"应用名称对应表","应用名称对应表.txt"),
    HTTPGET_WRITE(5,"HTTP GET 报文清洗域名白名单","HTTP GET 报文清洗域名白名单.txt"),
    HTTPGET_BLACK(6,"HTTP GET 报文清洗域名黑名单","HTTP GET 报文清洗域名黑名单.txt"),
    HTTPGET_URL_BLACK(7,"HTTP GET 报文清洗URL黑名单","HTTP GET 报文清洗URL黑名单.txt");


    private int classInfoId;
    private String classInfoIdDesc;
    private String classFileName;

    ClassInfoType(int classInfoId,String classInfoIdDesc,String classFileName){
        this.classInfoId = classInfoId;
        this.classInfoIdDesc = classInfoIdDesc;
        this.classFileName = classFileName;
    }

    public int getClassInfoId() {
        return classInfoId;
    }

    public void setClassInfoId( int classInfoId ) {
        this.classInfoId = classInfoId;
    }

    public String getClassInfoIdDesc() {
        return classInfoIdDesc;
    }

    public void setClassInfoIdDesc( String classInfoIdDesc ) {
        this.classInfoIdDesc = classInfoIdDesc;
    }

    public String getClassFileName() {
        return classFileName;
    }

    public void setClassFileName( String classFileName ) {
        this.classFileName = classFileName;
    }

    /**
     * return ClassInfo chinese desc
     * @param value
     * @return
     */
    public static String getClassInfoDesc(int value){
        for(ClassInfoType classInfoType: ClassInfoType.values()){
            if(classInfoType.getClassInfoId()==value){
                return classInfoType.getClassInfoIdDesc();
            }
        }
        return null;
    }
    /**
     * return classFileName
     * @param value
     * @return
     */
    public static String getclassFileName(int value){
        for(ClassInfoType classInfoType: ClassInfoType.values()){
            if(classInfoType.getClassInfoId()==value){
                return classInfoType.getClassFileName();
            }
        }
        return null;
    }


    /**
     * get class file Id
     * @param value
     * @return
     */
    public static int getclassInfoTypeIdByName(String value){
        for(ClassInfoType classInfoType: ClassInfoType.values()){
            if(value.equals(classInfoType.getClassFileName())){
                return classInfoType.getClassInfoId();
            }
        }
        return 0;
    }


}
