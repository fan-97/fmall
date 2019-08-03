package xyz.fanjie.mall.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    String uploadFile(String path, MultipartFile file);
}
