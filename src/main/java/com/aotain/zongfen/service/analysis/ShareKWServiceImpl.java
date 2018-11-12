package com.aotain.zongfen.service.analysis;

import com.aotain.zongfen.dto.analysis.ShareKWDTO;
import com.aotain.zongfen.mapper.analysis.ShareKWDao;
import com.aotain.zongfen.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShareKWServiceImpl implements ShareKWService {

    @Autowired
    private ShareKWDao dao;

    @Override
    public PageResult<ShareKWDTO> getSharePageResult( ShareKWDTO d ) {
        return dao.getData(d);
    }
    @Override
    public List<ShareKWDTO> getShareList( ShareKWDTO d ) {
        return dao.getList(d);
    }
}
