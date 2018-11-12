package com.aotain.zongfen.dto.general;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileDetailListDTO implements Serializable {
    private Long messageNo;

    private String server;

    private String fileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
    public Date createTime;

    private Integer messageType;
    
    private Integer zongfenId;
    
    private Integer fileType;
    
    private Long versionNo;
    
    private Long messageSequenceno;
    
    private String mainPolicy;
    
    private static final long serialVersionUID = 1L;
}