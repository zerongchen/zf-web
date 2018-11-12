package com.aotain.zongfen.model.system;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * 操作日志实体类
 *
 * @author daiyh@aotain.com
 * @date 2018/02/26
 */
@Getter
@Setter
@Table(name="zf_v2_operation_log")
public class OperationLog implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    private Date operTime;

    private String operUser;

    private Integer operModel;

    private Integer operType;

    private String clientIp;

    private Integer clientPort;

    private String serverName;

    private String dataJson;

    private String inputParam;

    private String outputParam;

    @Transient
    private String blobDataJson;


    public void setDataJson(String dataJson) {

        this.dataJson = dataJson;
        String blob = "";
        try {
            blob = new String(dataJson.getBytes("iso-8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.blobDataJson = blob;
    }

}
