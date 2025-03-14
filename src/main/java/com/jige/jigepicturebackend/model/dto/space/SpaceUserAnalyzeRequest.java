package com.jige.jigepicturebackend.model.dto.space;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserAnalyzeRequest extends SpaceAnalyzeRequest{

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 时间维度：day / week / month / year
     */
    private String timeDimension;
}
