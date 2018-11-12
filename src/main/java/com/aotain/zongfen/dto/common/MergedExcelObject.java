package com.aotain.zongfen.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MergedExcelObject {

    private int firstRow;
    private int lastRow;
    private int firstColumn;
    private int lastColumn;
    private String value;

}
