package com.aotain.zongfen.service.indexpage.imp;

import com.aotain.zongfen.dto.analysis.AppTrafficResult;
import com.aotain.zongfen.mapper.indexpage.IndexPageMapper;
import com.aotain.zongfen.model.useranalysis.AppTraffic;
import com.aotain.zongfen.service.indexpage.IndexPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IndexPageServiceImpl implements IndexPageService {
    private static Logger LOG = LoggerFactory.getLogger(IndexPageServiceImpl.class);

    @Autowired
    private IndexPageMapper indexPageMapper;
    @Override
    public List<AppTrafficResult> getIdcTraffic(Map<String, String> paramMap) {
        try {
            return indexPageMapper.getIdcTraffic(paramMap);
        } catch (Exception e) {
            LOG.error("",e);
        }
        return null;
    }

    @Override
    public List<AppTrafficResult> getDpiTraffic(Map<String, String> paramMap) {
        try {
            return indexPageMapper.getDpiTraffic(paramMap);
        } catch (Exception e) {
            LOG.error("",e);
        }
        return null;
    }


    @Override
    public List<AppTraffic> getDpiAppflow(Map<String, String> paramMap) {
        try {
            return indexPageMapper.getDpiAppflow(paramMap);
        } catch (Exception e) {
            LOG.error("",e);
        }
        return null;
    }


    @Override
    public List<AppTraffic> getAppIdAppflow(Map<String, String> paramMap) {
        try {
            return indexPageMapper.getAppIdAppflow(paramMap);
        } catch (Exception e) {
            LOG.error("",e);
        }
        return null;
    }
}
