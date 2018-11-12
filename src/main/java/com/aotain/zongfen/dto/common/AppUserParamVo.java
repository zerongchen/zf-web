package com.aotain.zongfen.dto.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppUserParamVo extends ParamBaseVo{
   private String order;
   
   private String sort;
   
   private Integer areaCode;
   
   private Integer appType;
   
   private Integer appName; 
}
