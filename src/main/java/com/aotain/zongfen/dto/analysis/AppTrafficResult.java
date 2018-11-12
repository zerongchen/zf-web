package com.aotain.zongfen.dto.analysis;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AppTrafficResult implements Serializable {

    private String sourceVal;
    private String source;
    private String targetVal;
    private String target;


    private Double totalFlow;
    private Double flowUp;
    private Double flowDn;



    private static final long serialVersionUID = 1L;


}