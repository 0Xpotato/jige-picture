package com.jige.jigepicturebackend.model.dto.space;

import lombok.Data;

import java.io.Serializable;


@Data
public class SpaceRankAnalyzeRequest implements Serializable {

    /**
     * 排名   前  N   的空间
     */
    private Integer topN = 10;

    private static final long serialVersionUID = -5241136264225366852L;

}
