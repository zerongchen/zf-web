package com.aotain.zongfen.dto.general.user;

import com.aotain.zongfen.annotation.ExpSheet;
import com.aotain.zongfen.annotation.Export;
import com.aotain.zongfen.model.general.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@ExpSheet(name="用户列表")
public class UserDTO {

    private Long messageNo;

    private Long userGroupId;

    @Export(title="所属分组", id=1)
    private String userGroupName;

    @Export(title="用户账号", id=3)
    private String userName;

    private Integer userType;

    @Export(title="用户类型", id=2)
    private String userTypeDesc;

    private Integer operateType;

    private Date createTime;
    private Date modifyTime;

    private String createOper;
    private String modifyOper;

    private static final long serialVersionUID = 1L;

}
