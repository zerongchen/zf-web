package com.aotain.zongfen.service.common;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.mapper.common.DictFactoryMapper;
import com.aotain.zongfen.mapper.device.DpiUploadDeviceMapper;
import com.aotain.zongfen.model.common.CommonTree;
import com.aotain.zongfen.model.common.DictFactory;
import com.aotain.zongfen.model.device.DpiUploadDevice;

@Service
public class CommonTreeServiceImpl implements CommonTreeService {
	
	@Autowired
	private DpiUploadDeviceMapper dpiUploadDeviceMapper;
	
	@Override
	public List<CommonTree> getTree() {
		List<DpiUploadDevice> list = dpiUploadDeviceMapper.selectAllTree();
		
		Set<CommonTree> treeSet = new LinkedHashSet<CommonTree>();
		List<CommonTree> tree = new ArrayList<CommonTree>();
		//id name pid level isparent
		CommonTree root =  new CommonTree("-9","全省","-9",0, (byte)-1,true);
		CommonTree dpi =  new CommonTree("-2","DPI","-9",1,(byte)0, true);
		CommonTree eu =  new CommonTree("-1","EU","-9",1,(byte)1, true);
		treeSet.add(root);
		treeSet.add(dpi);
		treeSet.add(eu);
		
		CommonTree area = null;
		CommonTree horse = null;
		CommonTree factory = null;
		for(DpiUploadDevice dev : list) {
			if(dev.getProbeType() == 0) {//dpi
				area = new CommonTree();
				factory =  new CommonTree();
				area.setId(dev.getAreaId());
				area.setName(dev.getAreaName());
				area.setPid("-2");//DPI
				area.setLevel(2);
				area.setProbeType((byte)0);
				area.setIsParent(true);
				
				factory.setId(dev.getAreaId()+"_"+dev.getSoftwareProvider());
				factory.setName(dev.getFactoryName());
				factory.setLevel(3);
				factory.setPid(dev.getAreaId());//area_id
				factory.setProbeType((byte)0);
				factory.setIsParent(false);
				
				treeSet.add(area);
				treeSet.add(factory);
			}else if(dev.getProbeType() == 1) {//eu
				horse = new CommonTree();
				factory =  new CommonTree();
				
				horse.setId(dev.getAreaId());
				horse.setName(dev.getAreaName());
				horse.setPid("-1");//EU
				horse.setLevel(2);
				horse.setProbeType((byte)1);
				horse.setIsParent(true);
				
				factory.setId(dev.getAreaId()+"_"+dev.getSoftwareProvider());
				factory.setName(dev.getFactoryName());
				factory.setLevel(3);
				factory.setPid(dev.getAreaId());//area_id
				factory.setProbeType((byte)1);
				factory.setIsParent(false);
				
				treeSet.add(horse);
				treeSet.add(factory);
			}
		}
		tree.addAll(treeSet);
		return tree;
	}

}
