package com.aotain.zongfen.mapper.general;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.IpAddressArea;
import com.aotain.common.policyapi.model.IpAddressAreaKey;
/**
 * 
* @ClassName: IpAddressAreaMapper 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author DongBoye
* @date 2018年3月1日 下午5:04:40 
*
 */
@MyBatisDao
public interface IpAddressAreaMapper {
	
    int deleteByPrimaryKey(IpAddressAreaKey key);

    int insert(IpAddressArea record);

    int insertSelective(IpAddressArea record);

    IpAddressArea selectByPrimaryKey(IpAddressAreaKey key);

    int updateByPrimaryKeySelective(IpAddressArea record);

    int updateByPrimaryKey(IpAddressArea record);
    
    int insertList(List<IpAddressArea> list);
    
    int deleteAll();
    
    List<IpAddressArea> selectAll();
    /**
     * 
    * @Title: selectByType 
    * @Description: 根据类型获取树形目录，type：2=城域网，3=IDC, (这里用一句话描述这个方法的作用) 
    * @param @param areaType
    * @param @return    设定文件 
    * @return List<IpAddressArea>    返回类型 
    * @throws
     */
    List<IpAddressArea> selectByType(@Param("areaType")Byte areaType);
    
    String selectByName(String name);
    
}