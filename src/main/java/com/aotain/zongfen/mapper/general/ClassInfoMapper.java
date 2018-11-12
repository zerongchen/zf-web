package com.aotain.zongfen.mapper.general;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.general.FileDetailListDTO;
import com.aotain.zongfen.model.general.ClassInfo;

@MyBatisDao
public interface ClassInfoMapper {

	int insert(ClassInfo record);

    int insertSelective(ClassInfo record);

    List<ClassInfo> getClassInfos(ClassInfo record);

    /**
     * 根据多个classID删除策略
     * @param array
     * @return
     */
//    int deleteClassInfos(Integer[] array);

//    Integer getMaxClassId();

//    int selectCount(ClassInfo record);

    /**
     * 根据ID或者classType删除当个策略
     * @param record
     * @return
     */
//    int deleteSingleClassInfo(ClassInfo record);

//    int CountClassName(ClassInfo record);

    int updateInfo(ClassInfo record);
    
    ClassInfo getByType(@Param("messageType")Integer messageType);
    
    List<FileDetailListDTO> getFileDetailByType(@Param("messageType")Integer messageType);

}