package org.example.controller;

import org.example.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping()
public class FileController {

    private static final String BASE_PATH = System.getProperty("user.dir") + "/file/";

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam("type") String type) {
        if (file.isEmpty()) {
            return Result.fail("文件不能为空");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !(fileName.endsWith(".jpg")
                || fileName.endsWith(".png")
                || fileName.endsWith(".jpeg"))) {
            return Result.fail("只支持 JPG/PNG/JPEG 图片");
        }

        String dirPath = BASE_PATH + type + "/";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String newFileName = dateStr + "_" + uuid + suffix;

        File dest = new File(dirPath + newFileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("文件保存失败");
        }

        String returnPath = type + "/" + newFileName;
        return Result.success(returnPath);
    }
}