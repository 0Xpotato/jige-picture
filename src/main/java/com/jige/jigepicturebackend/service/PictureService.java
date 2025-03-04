package com.jige.jigepicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jige.jigepicturebackend.model.dto.picture.*;
import com.jige.jigepicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jige.jigepicturebackend.model.entity.User;
import com.jige.jigepicturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-02-22 11:24:13
*/
public interface PictureService extends IService<Picture> {


    /**
     * 上传图片
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 将查询请求转换为QueryWrapper对象
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取图片封装的方法，可以为原有的图片关联创建用户的信息
     * 获取单个图片封装
     * @param picture
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片封装
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 校验图片
     * @param picture
     */
    void validPicture(Picture picture);

    /**
     * 图片审核
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest,User loginUser);

    /**
     * 管理员自动过审并且填充审核参数
     */
    void fillReviewParams(Picture picture,User loginUser);

    /**
     * 批量抓取和创建图片
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    int uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest,User loginUser);

    /**
     * 清理图片文件
     * @param oldPicture
     */
    void clearPictureFile(Picture oldPicture);

    /**
     * 权限校验逻辑
     * @param picture
     * @param loginUser
     */
    void checkPictureAuth(Picture picture, User loginUser);

    /**
     * 删除图片逻辑
     * @param pictureId
     * @param loginUser
     */
    void deletePicture(long pictureId, User loginUser);

    /**
     * 编辑图片逻辑
     * @param pictureEditRequest
     * @param loginUser
     */
    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);
}
