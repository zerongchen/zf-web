package com.aotain.zongfen.model.upload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class FlowUpload implements Serializable {

    private Integer packetType;

    private Integer packetSubtype;

    private Long rStarttime;

    private Long rEndtime;

    private Integer rFreq;

    private Integer zongfenId;

    private Integer rMethod;

    private Long messageNo;

    private static final long serialVersionUID = 1L;

}