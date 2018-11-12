package com.aotain.zongfen.model.common;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DictFactory implements Serializable {
    private String facotryCode;

    private String facotryName;

    private static final long serialVersionUID = 1L;

}