package com.geqian.structure.word;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author geqian
 * @date 22:38 2023/5/31
 */
public class WordDownloadUtils {

    /**
     * 下载word到浏览器
     *
     * @param bytes    word字节数组
     * @param response 响应
     */
    public static void download(byte[] bytes, String fileName, HttpServletResponse response) {
        //out为OutputStream，需要写出到的目标流
        try (ServletOutputStream out = response.getOutputStream()) {
            //二进制数组写入输出流
            out.write(bytes);

            response.setHeader("filename", URLEncoder.encode(fileName + ".docx", "UTF-8"));

            response.setHeader("Access-Control-Expose-Headers", "filename");

            response.setHeader("content-type", "application/octet-stream");

            //文件设置为附件
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".docx", "UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 下载word到本地
     *
     * @param bytes      word字节数组
     * @param targetPath 保存路径
     * @throws Exception
     */
    public static void download(byte[] bytes, String targetPath) {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetPath))) {
            bos.write(bytes);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
