package com.aotain.zongfen.mapper.general;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.general.ClassFileInfo;
import com.aotain.zongfen.model.general.ClassFileInfoKey;

import java.util.List;

@MyBatisDao
public interface ClassFileInfoMapper {
    
    int deleteByPrimaryKey(ClassFileInfoKey key);

    int insert(ClassFileInfo record);

    int insertSelective(ClassFileInfo record);

    ClassFileInfo selectByPrimaryKey(ClassFileInfoKey key);

    List<ClassFileInfo> selectListByPrimaryKey( ClassFileInfoKey key);

    int updateByPrimaryKeySelective(ClassFileInfo record);

    int updateByPrimaryKey(ClassFileInfo record);

}