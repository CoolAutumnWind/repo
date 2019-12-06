package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

/**
 * 文件上传控制器
 */
@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL; //从配置文件中取文件服务器地址

    @RequestMapping("/upload")
    public Result uploadFile(MultipartFile file){
        //取文件扩展名
        String originalFilename = file.getOriginalFilename();   //1.获得文件全名
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        try {
            //创建一个fastDFS客户端
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //执行上传处理
            String fileId = fastDFSClient.uploadFile(file.getBytes(), extName);
            String url = FILE_SERVER_URL+fileId;
            return new Result(true, url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败！");
        }

    }
}
