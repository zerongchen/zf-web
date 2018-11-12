package com.aotain.zongfen.dto.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KeyValueObj<T> {
    private String key;
    private T value;
}
