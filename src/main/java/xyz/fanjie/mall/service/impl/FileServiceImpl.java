package xyz.fanjie.mall.service.impl;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.fanjie.mall.service.IFileService;
import xyz.fanjie.mall.util.FTPUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    public String uploadFile(String path, MultipartFile file) {
        String filename = file.getOriginalFilename();
        Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
        String realFileName = UUID.randomUUID() +"."+ filename.substring(filename.lastIndexOf(".") + 1);//新生成的文件名后缀名

        //目录
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(path, realFileName);
        logger.info("开始上传文件{},目录{}",realFileName,path);
        //上传文件
        try {
            file.transferTo(targetFile);
            //上传成功
            // todo 将图片上传到ftp中
            boolean uploaded = FTPUtils.uploadFile(Lists.newArrayList(targetFile));
            // todo 删除当前目录下的文件
            targetFile.delete();
            if (uploaded){
                return realFileName;
            }
        } catch (IOException e) {
            logger.error("上传图片失败",e);
        }
        return null;
    }
}
