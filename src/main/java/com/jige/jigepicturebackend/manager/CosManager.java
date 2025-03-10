package com.jige.jigepicturebackend.manager;

import cn.hutool.core.io.FileUtil;
import com.jige.jigepicturebackend.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    // ... 一些操作 COS 的方法

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 删除对象
     *
     * @param key 文件 key
     */
    public void deleteObject(String key) throws CosClientException {
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }

    /*    *//**
     * 上传对象（附带图片信息）
     *
     * @param key  唯一键
     * @param file 文件
     *//*
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        // 构造处理参数
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }*/

    /**
     * 将图片后缀转为 webp
     *
     * @param key  指定原始图片的存储路径
     * @param file 需要上传的本地文件对象（java.io.File 类型）
     * @return
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        // 添加图片处理规则
        List<PicOperations.Rule> rules = new ArrayList<>();
        // 图片压缩（转成 webp 格式）
        String webpKey = FileUtil.mainName(key) + ".webp";
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setFileId(webpKey);
        compressRule.setRule("imageMogr2/format/webp");
        rules.add(compressRule);
        // 规则2：图片压缩成 jpg 格式
        String jpgKey = FileUtil.mainName(key) + ".png";
        PicOperations.Rule jpgRule = new PicOperations.Rule();
        jpgRule.setBucket(cosClientConfig.getBucket());
        jpgRule.setRule("imageMogr2/format/png"); // 转换为 png 格式
        jpgRule.setFileId(jpgKey);
        rules.add(jpgRule);
        // 缩略图处理，仅对 > 10 KB 的图片生成缩略图    10*1024字节=10KB
        if (file.length() > 10 * 1024) {
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            thumbnailRule.setBucket(cosClientConfig.getBucket());
//            String thumbnailKey = FileUtil.mainName(file) + "_thumbnail." + FileUtil.getSuffix(key);
            String thumbnailKey = FileUtil.mainName(file) + "_thumbnail.webp";
            thumbnailRule.setFileId(thumbnailKey);
            // 缩放规则 /thumbnail/<Width>x<Height>>（如果生成的缩略图大于原图宽高，则不处理）
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s", 128, 128));
            rules.add(thumbnailRule);
        }
        // 构造处理参数
        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }


}
