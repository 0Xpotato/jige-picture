package com.jige.jigepicturebackend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceUsageAnalyzeResponse implements Serializable {

    /**
     * 已使用大小
     */
    private Long usedSize;

    /**
     * 总大小
     */
    private Long maxSize;

    /**
     * 已使用数量
     */
    private Long usedCount;

    /**
     * 总数量
     */
    private Long maxCount;

    /**
     * 空间使用比例
     */
    private Double sizeUsageRatio;

    /**
     * 图片数量比例
     */
    private Double countUsageRatio;

    private static final long serialVersionUID = 5505502786842051202L;

}
