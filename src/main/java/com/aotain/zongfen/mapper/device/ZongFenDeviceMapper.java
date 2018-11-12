package com.aotain.zongfen.mapper.device;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.model.device.ZongFenDeviceUser;
import com.aotain.zongfen.model.device.ZongFenRel;

@MyBatisDao
public interface ZongFenDeviceMapper {

	int insert(ZongFenDevice record);

	ZongFenDevice getZongfenDevByPrimary( Integer zongfenId);
	
	List<ZongFenDevice> getListByName(@Param("zongFenName")String zongFenName);
	/**
	 * 根据综分设备类型获取综分设备
	 * @param zongfenType
	 * @return
	 */
	List<ZongFenDevice> getZongfenDevByType(Map<String,Integer> query);
	
	int deleteById(Integer deviceId);
	
	int updateById(ZongFenDevice record);
	
	/**
	 * 
	* @Title: getSameIpCount
	* @Description: 相同ip和端口的设备数量
	* @param @param record
	* @param @return
	* @return int
	* @throws
	 */
	int getSameIpCount(ZongFenDevice record);
	
	/**
	 * 
	* @Title: selectIsUploadUsed
	* @Description: 查询存在关联的综分设备（流量上报策略）
	* @param @param deviceId
	* @param @return
	* @return int
	* @throws
	 */
	int selectIsUploadUsed(Map<String, String> query);
	
	/**
	 * 
	* @Title: selectIsUploadUsed
	* @Description: 查询存在关联的综分设备（分类库下发策略）
	* @param @param deviceId
	* @param @return
	* @return int
	* @throws
	 */
	int selectIsCategoryUsed(Integer deviceId);
	
	/**
	 * 
	* @Title: getDeviceUser 
	* @Description: 查询设备的ftp的用户名密码(这里用一句话描述这个方法的作用) 
	* @param @param zongfenId
	* @param @return    设定文件 
	* @return ZongFenDeviceUser    返回类型 
	* @throws
	 */
	ZongFenDeviceUser getDeviceUser(Integer zongfenId);
	/**
	 * 
	* @Title: getDeviceUserByType 
	* @Description: 根据类型获取ftp的用户名密码(这里用一句话描述这个方法的作用) 
	* @param @param packetType
	* @param @param packetSubType
	* @param @return    设定文件 
	* @return List<ZongFenDeviceUser>    返回类型 
	* @throws
	 */
	List<ZongFenDeviceUser> getDeviceUserByType(@Param("packetType")Integer packetType,@Param("packetSubType")Integer packetSubType);
	
	int getExistIp(List<ZongFenRel> list);
}
