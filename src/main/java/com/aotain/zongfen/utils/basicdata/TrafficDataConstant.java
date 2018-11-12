package com.aotain.zongfen.utils.basicdata;

public enum  TrafficDataConstant {

    //时间粒度和表的选择
    APPTRAFIC_TABLE_H(1,1,"zf_v2_ubas_traffic_h"),
    APPTRAFIC_TABLE_D(1,2,"zf_v2_ubas_traffic_d"),
    APPTRAFIC_TABLE_W(1,3,"zf_v2_ubas_traffic_w"),
    APPTRAFIC_TABLE_M(1,4,"zf_v2_ubas_traffic_m"),

    //源数据类别（3bits）
    //电信网内
    INNER_NET_DATASOURCE(2,1,"001"),
    //互联互通
    INTERWORKING_NET_DATASOURCE(2,2,"010"),
    //国际
    INTERNATION_NET_DATASOURCE(2,3,"011"),
    //区域类别（2,6,8 bits）
    //省份
    INNER_NET_AREA(3,1,"01"),
    //IDC
    INNER_NET_IDC(3,2,"10");
    //运营商ID（6bits）

    //前端传值
    private Integer type;
    private Integer refer;
    private String value;

    TrafficDataConstant( int type, int refer, String value ) {
        this.type=type;
        this.refer=refer;
        this.value=value;
    }

    public static String getValueByTypeAndRefer(int type,int refer){
        for(TrafficDataConstant item : TrafficDataConstant.values()){
            if(item.getType() == type ) {
                if(item.getRefer()==refer){
                    return item.getValue();
                }
            }
        }
        return null;
    }


    public Integer getType() {
        return type;
    }

    public void setType( Integer type ) {
        this.type = type;
    }

    public Integer getRefer() {
        return refer;
    }

    public void setRefer( Integer refer ) {
        this.refer = refer;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }


}
