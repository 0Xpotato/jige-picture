package com.jige.jigepicturebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jige.jigepicturebackend.exception.ErrorCode;
import com.jige.jigepicturebackend.exception.ThrowUtils;
import com.jige.jigepicturebackend.manager.FileManager;
import com.jige.jigepicturebackend.model.dto.picture.PictureUploadRequest;
import com.jige.jigepicturebackend.model.dto.file.UploadPictureResult;
import com.jige.jigepicturebackend.model.entity.Picture;
import com.jige.jigepicturebackend.model.entity.User;
import com.jige.jigepicturebackend.model.vo.PictureVO;
import com.jige.jigepicturebackend.service.PictureService;
import com.jige.jigepicturebackend.mapper.PictureMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author Administrator
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-02-22 11:24:13
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Resource
    private FileManager fileManager;

    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {
        ThrowUtils.throwIf(loginUser==null, ErrorCode.NOT_LOGIN_ERROR);
        //用于判断是新增还是更新图片
        Long pictureId=null;
        if (pictureUploadRequest!=null){
            pictureId = pictureUploadRequest.getId();
        }
        //如果是更新图片，则需要有图片id，需要校验图片是否存在
        if (pictureId!=null){
            boolean exists = this.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .exists();
            ThrowUtils.throwIf(!exists,ErrorCode.NOT_FOUND_ERROR,"图片不存在");
        }
        //（新增）上传图片，得到信息
        //按照用户id，划分目录
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);
        //构造要入库的图片信息
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        //如果pictureId不为空，表示更新，否则是新增
        if (pictureId!=null){
            //是更新，需要补充id和编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR,"图片上传失败");
        return PictureVO.objToVo(picture);
    }
}




