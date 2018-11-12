package com.aotain.zongfen.service.analysis;

import com.aotain.zongfen.dto.analysis.ShareKWDTO;
import com.aotain.zongfen.utils.PageResult;

import java.util.List;

public interface ShareKWService {

    public PageResult<ShareKWDTO> getSharePageResult( ShareKWDTO d);
    public List<ShareKWDTO> getShareList( ShareKWDTO d);
}
