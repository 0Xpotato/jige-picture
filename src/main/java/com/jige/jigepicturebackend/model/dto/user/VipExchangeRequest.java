package com.jige.jigepicturebackend.model.dto.user;

import lombok.Data;
import java.io.Serializable;

/**
 * 会员兑换请求
 */
@Data
public class VipExchangeRequest implements Serializable {

    private static final long serialVersionUID = -5393851438230922406L;
    // 兑换码
    private String vipCode;
}