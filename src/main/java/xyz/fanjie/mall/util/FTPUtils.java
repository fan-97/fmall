package xyz.fanjie.mall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtils {
    private static String fip = PropertiesUtil.getProperty("ftp.server.ip");
    private static String fuser = PropertiesUtil.getProperty("ftp.user");
    private static String fpass = PropertiesUtil.getProperty("ftp.pass");

    private static Logger logger = LoggerFactory.getLogger(FTPUtils.class);

    private String ip;
    private String user;
    private String pass;
    private int port;
    private FTPClient ftpClient;

    public FTPUtils(String ip, String user, String pass, int port) {
        this.ip = ip;
        this.user = user;
        this.pass = pass;
        this.port = port;
    }

    public static boolean uploadFile(List<File> fileList) {
        FTPUtils ftpUtils = new FTPUtils(fip, fuser, fpass, 21);
        //
        logger.info("开始上传图片{}到FTP");
        boolean flag = ftpUtils.uploadFile("img", fileList);
        if(flag) {
            logger.info("上传图片成功");
        }else {
            logger.error("上传图片到FTP失败");
        }
        return flag;
    }

    private boolean uploadFile(String remotePath, List<File> fileList) {
        boolean uploaded = true;
        FileInputStream fis = null;
        //连接FTP服务器
        if (connctionFTP(this.ip, this.port, this.user, this.pass)) {
            //上传文件
            try {
                ftpClient.setBufferSize(1024);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.enterLocalPassiveMode();
                for (File file : fileList) {
                    fis = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), fis);
                }
            } catch (IOException e) {
                logger.error("上传文件异常", e);
                uploaded = false;
            }
        }else {
            logger.error("登录FTP服务失败");
            uploaded = false;
        }
        return uploaded;
    }

    private boolean connctionFTP(String ip, int port, String user, String pass) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            //连接服务器
            ftpClient.connect(ip);
            //登录服务器
            isSuccess = ftpClient.login(user, pass);
        } catch (IOException e) {
            isSuccess = false;
            logger.error("连接服务器异常");
        }
        return isSuccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
