package com.aotain.zongfen.model.general.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class IpUser implements Serializable {
    private Long userId;

    @NonNull private Long messageNo;

    @NonNull private String userName;

    private static final long serialVersionUID = 1L;


}