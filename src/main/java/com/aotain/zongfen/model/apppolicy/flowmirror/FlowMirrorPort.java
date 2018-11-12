package com.aotain.zongfen.model.apppolicy.flowmirror;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/09
 */
@Getter
@Setter
@Table(name="zf_v2_policy_flowmirror_port")
public class FlowMirrorPort {
    @Column(name="POLICY_ID")
    private Long policyId;

    @Column(name="PORTTYPE")
    private Integer portType;

    @Column(name="PORTSTART")
    private Integer portStart;

    @Column(name="PORTEND")
    private Integer portEnd;
}
