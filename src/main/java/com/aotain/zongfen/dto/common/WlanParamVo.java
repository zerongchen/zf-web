package com.aotain.zongfen.dto.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WlanParamVo extends ParamBaseVo{
   private String order;
   
   private String sort;
   
   private Integer areaCode;
   
   private Integer userGroups;
   
   private String accounts; 
}
