package com.aotain.zongfen.service.general.userstaticip;

import com.aotain.zongfen.dto.general.user.IpUserDTO;
import com.aotain.zongfen.dto.general.user.UserStaticIPDTO;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.model.general.user.UserStaticIP;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface UserStaticIpService {

    List<IpUserDTO> getList( IpUserDTO record);

    ResponseResult deleteByUserIds( String[] ids);

    ResponseResult deleteByIpIds( String[] ids);

    ResponseResult insert( UserStaticIPDTO record , Integer operateType);

    ResponseResult update( UserStaticIP record );

    ImportResultList handleImport( HttpServletRequest request ) throws ImportException;

    Map<String,String> uploadFile(HttpServletRequest request);

    boolean resendPolicy(long l, Long messageNo, boolean b, String s);
}
