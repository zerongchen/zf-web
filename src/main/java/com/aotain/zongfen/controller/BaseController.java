package com.aotain.zongfen.controller;


import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.config.pagehelper.PageResult;
import com.aotain.zongfen.model.BaseEntity;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.utils.SpringUtil;
import org.apache.poi.ss.formula.functions.T;


import java.util.Date;
import java.util.List;

/**
 * 公共Controller服务类，用于提供一些controller相关的公共方法
 *
 * @author daiyh@aotain.com
 * @date 2018/02/27
 */
public abstract class BaseController {

    /**
     * 插入或者更新时设置一些公共的值
     * @param baseEntity
     * @param update
     */
    public void setCommonPropertiesBeforeInsertOrUpdate(BaseEntity baseEntity, boolean update){
        //TODO 用户名需要修改
        String userName = SpringUtil.getSysUserName();
        // 修改
        if (update) {
            baseEntity.setModifyTime(new Date());
            baseEntity.setModifyOper(userName);
        } else {
            baseEntity.setCreateOper(userName);
            baseEntity.setCreateTime(new Date());
            baseEntity.setModifyTime(new Date());
            baseEntity.setModifyOper(userName);
        }

    }

    /**
     * 根据分页数据生成返回给前端的数据
     * @param page
     * @param returnData
     * @return
     */
    public PageResult wrapResultData(Page page, List returnData){
        PageResult pageResult = new PageResult();
        pageResult.setRows(returnData);
        pageResult.setTotal(Long.valueOf(page.getTotalRecord()));
        return pageResult;
    }

    public ResponseResult wrapReturnDate(){
        ResponseResult responseResult  = new ResponseResult();
        responseResult.setMessage("success");
        responseResult.setResult(0);
        return responseResult;
    }

    public ResponseResult wrapReturnDate(int status){
        ResponseResult responseResult  = new ResponseResult();
        responseResult.setMessage("success");
        responseResult.setResult(status);
        return responseResult;
    }

    public ResponseResult wrapReturnDate(String message,int status) {
        ResponseResult responseResult  = new ResponseResult();
        responseResult.setMessage(message);
        responseResult.setResult(status);
        return responseResult;
    }

    public ResponseResult wrapReturnDate( List<BaseKeys> kys) {
        ResponseResult responseResult  = new ResponseResult();
        responseResult.setMessage("success");
        responseResult.setResult(1);
        responseResult.setKeys(kys);
        return responseResult;
    }

    public <T> ResponseResult<T> wrapReturnDate( T data ,List<BaseKeys> kys) {
        ResponseResult responseResult  = new ResponseResult();
        responseResult.setMessage("success");
        responseResult.setResult(1);
        responseResult.setKeys(kys);
        responseResult.setData(data);
        return responseResult;
    }
}
