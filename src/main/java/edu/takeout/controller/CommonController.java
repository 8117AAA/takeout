package edu.takeout.controller;

import edu.takeout.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
@Api(tags = "通用接口")
public class CommonController {

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    Result<String> upload(MultipartFile file, HttpServletRequest request) throws FileNotFoundException {
        String basepath = ResourceUtils.getURL("classpath:").getPath() + "uploaded";
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + suffix;
        File dir = new File(basepath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(basepath+File.separator+filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.success(filename);
    }

    @ApiOperation("文件下载")
    @GetMapping("/download")
    void download(String name, HttpServletResponse response, HttpServletRequest request) throws FileNotFoundException {
        final String basePath = ResourceUtils.getURL("classpath:").getPath() + "uploaded";
        try {
            FileInputStream inputStream = new FileInputStream(basePath + File.separator + name);
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
