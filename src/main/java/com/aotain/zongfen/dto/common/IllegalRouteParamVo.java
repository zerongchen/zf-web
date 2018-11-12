package com.aotain.zongfen.dto.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IllegalRouteParamVo extends ParamBaseVo{
   private String order;
   
   private String sort;
   
   private Integer areaCode;
   
   private String carrieroperators;
   
   private String cloum;
   
   private String imgUrl;
}
