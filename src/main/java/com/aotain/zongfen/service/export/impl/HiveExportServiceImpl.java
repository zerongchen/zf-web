package com.aotain.zongfen.service.export.impl;

import com.aotain.zongfen.mapper.export.HiveExportMapper;
import com.aotain.zongfen.service.export.HiveExportService;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HiveExportServiceImpl implements HiveExportService {

    private static final Logger LOG = LoggerFactory.getLogger(HiveExportServiceImpl.class);

    @Autowired
    private HiveExportMapper hiveExportMapper;

    @Override
    public boolean addExportTask(Map<String, Object> params) {
        try {
            int b = hiveExportMapper.addExportTask(params);
            if (b > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LOG.error(" ", e);
        }
        return false;
    }

    @Override
    public boolean deleteExportTask(Map<String, Object> params) {
        try {
            int b = hiveExportMapper.deleteExportTask(params);
            if (b > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LOG.error(" ", e);
        }
        return false;
    }

    @Override
    public boolean updateDownloadFile(Map<String, Object> params) {
        try {
            int b = hiveExportMapper.updateDownloadFile(params);
            if (b > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LOG.error(" ", e);
        }
        return false;
    }

    @Override
    public List<Map<String, String>> selectExportTask(int pageIndex, int pageSize, Map<String, String> params) {
        try {
            PageHelper.startPage(pageIndex,pageSize);
            List<Map<String,String>> list = hiveExportMapper.selectExportTask(params);
/*            if(list.size()>0){
                for(Map<String,String> m:list){
                    String hive_sql = m.get("hive_sql").replaceAll("'","\\'");
                    m.put("hive_sql",hive_sql);
                }
            }*/
            return list;
        } catch (Exception e) {
            LOG.error(" ",e);
        }
        return null;
    }
}
