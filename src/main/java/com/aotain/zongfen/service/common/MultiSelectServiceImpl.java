package com.aotain.zongfen.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.constant.RedisKeyConstant;
import com.aotain.zongfen.dto.common.Multiselect;
import com.aotain.zongfen.mapper.common.MultiSelectMapper;
import com.aotain.zongfen.model.common.ChinaArea;

@Service
@Transactional(readOnly = true)
public class MultiSelectServiceImpl implements MultiSelectService {

    @Autowired
    private MultiSelectMapper multiSelectMapper;

    @Override
    public List<Multiselect> getWSOption() {
        return multiSelectMapper.getWSOption();
    }

    @Override
    public List<Multiselect> getKWOption() {
        return multiSelectMapper.getKWOption();
    }

    @Override
    public List<Multiselect> getWLOption() {
        return multiSelectMapper.getWLOption();
    }

    @Override
    public List<Multiselect> getAppType() {
        List<Multiselect> list = multiSelectMapper.getAppType();
        return list;
    }

    @Override
    public List<Multiselect> getAppByType( Integer appType ) {
        List<Multiselect> list =  multiSelectMapper.getAppByType(appType);
        return list;
    }

    @Override
    public List<Multiselect> getUserGroup() {
        return multiSelectMapper.getUserGroup();
    }

    @Override
    public List<Multiselect> getClassFileInfo(Integer classType) {
        return multiSelectMapper.getClassFileInfo(classType);
    }

    @Override
    public List<Multiselect> getZongfenDev( Map<String, Object> map ) {
        return multiSelectMapper.getZongfenDev(map);
    }

	@Override
	public List<Multiselect> getWebType() {
		return multiSelectMapper.getWebType();
	}

	@Override
	public List<Multiselect> getAreaGroupPolicy() {
		return multiSelectMapper.selectAreaGroupPolicy();
	}

    @Override
    public List<Multiselect> getDdosManagePolicy() {
        return multiSelectMapper.selectDdosManagePolicy();
    }

	@Override
	public List<Multiselect> getWebPushManagePolicy() {
		return multiSelectMapper.getWebPushManagePolicy();
	}


    @Override
    public List<Multiselect> getMessageTypeName() {
        return multiSelectMapper.getMessageTypeName();
    }

    @Override
    public List<Multiselect> getAppUserPolicy(){
        return multiSelectMapper.getAppUserPolicy();
    }

	@Override
	public List<Multiselect> getAreaList() {
		List<Multiselect> areaList = new ArrayList<Multiselect>();
		String parentCode = LocalConfig.getInstance().getHashValueByHashKey(RedisKeyConstant.PROVINCE_AREA);
		String deployMode = LocalConfig.getInstance().getHashValueByHashKey(RedisKeyConstant.SYSTEM_DEPLOY_MODE);
		if(parentCode==null || "".equals(parentCode)) {
			return null;
		}else {
			Map<String,Object> query = new HashMap<String, Object>();
			query.put("type",deployMode);
			query.put("parentCode",parentCode);
			areaList = multiSelectMapper.getAreaList(query);
		}
		return areaList;
	}

    @Override
    public List<Multiselect> getIdcHouseList() {
        return multiSelectMapper.getIdcHouseList();
    }

    @Override
    public List<Multiselect> getCarrieroperators() {
        return multiSelectMapper.getCarrieroperators();
    }
}
