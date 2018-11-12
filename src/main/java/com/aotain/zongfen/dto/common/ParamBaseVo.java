package com.aotain.zongfen.dto.common;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParamBaseVo {
	
   private Logger logger = LoggerFactory.getLogger(this.getClass());

   private Integer pageIndex;
   
   private Integer pageSize;
   
   private String stateTime;
   
   private int exportType;
   
   private int dateType;
   
   private Integer startTime;
   
   private Integer endTime;
	   
	 //格式化日期
    public boolean resetStateTime() {
        if(StringUtils.isEmpty(this.stateTime)) return true;
        String stateFormula = null;
        try {
            switch (this.dateType){
                case 1: //H
                    Calendar cal1 = DateUtils.toCalendar(DateUtils.parseDate(this.stateTime,DateFormatConstant.DATETIME_WITHOUT_HOUR));
                    stateFormula = DateUtils.formatDateyyyyMMddHH(cal1.getTime());
                    break;
                case 2:
                    Calendar cal2 = DateUtils.toCalendar(DateUtils.parseDate(this.stateTime,DateFormatConstant.DATE_CHS_HYPHEN));
                    stateFormula = DateUtils.formatDateyyyyMMdd(cal2.getTime());
                    break;
                case 3:
                    Calendar cal3 = DateUtils.toCalendar(DateUtils.parseDate(this.stateTime,DateFormatConstant.DATE_CHS_HYPHEN));
                    cal3.set(Calendar.DAY_OF_WEEK,1);
                    cal3.add(Calendar.DATE, 1);
                    stateFormula = DateUtils.formatDateyyyyMMdd(cal3.getTime());
                    break;
                case 4:
                    Date date = DateUtils.parse(DateFormatConstant.DATE_CHS_MONTH,this.stateTime);
                    Calendar cal = DateUtils.toCalendar(date);
                    cal.set(Calendar.DAY_OF_MONTH,1);
                    stateFormula = DateUtils.formatDateyyyyMMdd(cal.getTime());
                    break;
            }
            this.stateTime = stateFormula;
        }catch (Exception e){
            logger.error("resetStateTime error"+e);
            return false;
        }
        return true;
    }
}
