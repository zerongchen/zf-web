package com.aotain.zongfen.model.sankey;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Links {

    private String sourceVal;
    private String source;
    private String targetVal;
    private String target;
    private Double value;
}
