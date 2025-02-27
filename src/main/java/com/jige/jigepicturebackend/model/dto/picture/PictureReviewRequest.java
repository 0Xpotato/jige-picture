package com.jige.jigepicturebackend.model.dto.picture;

import com.jige.jigepicturebackend.common.PageRequest;
import com.jige.jigepicturebackend.model.entity.Picture;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 请求包装类
 */
@Data
public class PictureReviewRequest implements Serializable {

    private static final long serialVersionUID = 1280317340196291247L;
    /**
     * id
     */
    private Long id;

    /**
     * 状态：0--待审核，1--通过，2--拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

}
