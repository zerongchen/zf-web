package com.aotain.zongfen.service.general.bras;

import com.aotain.zongfen.dto.general.BrasDTO;
import com.aotain.zongfen.mapper.general.BrasMapper;
import com.aotain.zongfen.model.general.Bras;
import com.aotain.zongfen.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrasServiceImpl implements BrasService {

    @Autowired
    private BrasMapper brasMapper;

    @Override
    public List<BrasDTO> getBrase( Bras bras ) {

        List<BrasDTO> list = brasMapper.getBrase(bras);
        for (BrasDTO dto:list) {
            dto.setFirstTimeStr(DateUtils.getDateTime(dto.getFirstTime()));
            dto.setLastTimeStr(DateUtils.getDateTime(dto.getLastTime()));
        }
        return list;
    }
}
