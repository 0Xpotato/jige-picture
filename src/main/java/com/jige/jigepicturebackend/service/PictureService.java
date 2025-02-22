package com.jige.jigepicturebackend.service;

import com.jige.jigepicturebackend.model.dto.picture.PictureUploadRequest;
import com.jige.jigepicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jige.jigepicturebackend.model.entity.User;
import com.jige.jigepicturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
* @author Administrator
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-02-22 11:24:13
*/
public interface PictureService extends IService<Picture> {


    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);
}
