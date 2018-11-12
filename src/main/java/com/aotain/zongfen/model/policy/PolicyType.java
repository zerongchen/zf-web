package com.aotain.zongfen.model.policy;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class PolicyType implements Serializable {

	@NonNull private Long messageSequenceno;

	@NonNull private Integer messageType;

	@NonNull private String messageTitle;

	@NonNull private Integer flag;

    private static final long serialVersionUID = 1L;

}