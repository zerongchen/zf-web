package com.aotain.zongfen.model.system;

import com.aotain.zongfen.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * 系统参数实体类
 *
 * @author daiyh@aotain.com
 * @date 2018/02/26
 */
@Getter
@Setter
public class SystemParameter extends BaseEntity implements Serializable{

    private Integer configId;

    private String configKey;

    private String configValue;

    private String configDesc;

    private Integer inputType;

    private String inputItems;

    private Integer modelType;

    //用作保存时使用
    private String inputItem;

    public void setInputItems(String inputItems) throws UnsupportedEncodingException {
        String blob = "";
        blob = new String(inputItems.getBytes("iso-8859-1"),"UTF-8");
        this.inputItems = blob;
    }
}
