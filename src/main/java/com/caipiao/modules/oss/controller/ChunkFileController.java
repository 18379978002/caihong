package com.caipiao.modules.oss.controller;

import com.caipiao.common.utils.R;
import com.caipiao.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("manage/chunk/file")
@Api(tags = "分片上传管理")
public class ChunkFileController extends AbstractController {


    @ApiOperation(value = "上传文件(分片)", notes = "上传文件(分片)")
    @PostMapping(value = "/uploadChunk")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id"),
            @ApiImplicitParam(name = "file", value = "文件"),
            @ApiImplicitParam(name = "chunks", value = "分片数量"),
            @ApiImplicitParam(name = "chunk", value = "第几片")
    })
    public R uploadVideo(@RequestParam(value = "file", required = false) MultipartFile file,
                         @RequestParam(value = "id") String id,
                         @RequestParam(value = "chunks", required = false) Integer chunks,
                         @RequestParam(value = "chunk", required = false) Integer chunk) {
        String path = "/Users/yujun/tmp/" + id + getUserId() + "/";
        File dirfile = new File(path);
        if (!dirfile.exists()) {//目录不存在，创建目录
            dirfile.mkdirs();
        }
        String chunkName;
        if (chunk == null) {//表示是小文件，还没有一片
            chunkName = "0.1";
        } else {
            chunkName = chunk + "." + chunks;
        }
        String filePath = path + chunkName;
        File savefile = new File(filePath);

        try {
            if (!savefile.exists()) {
                savefile.createNewFile();//文件不存在，则创建
            }
            file.transferTo(savefile);//将文件保存
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok();
    }

    @ApiOperation(value = "合成分片", notes = "合成分片")
    @PostMapping("merge")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id"),
            @ApiImplicitParam(name = "name", value = "文件名称")
    })
    public R merge(@RequestParam(value = "id") String id,
                   @RequestParam(value = "name") String name) {
        File file1 = new File("/Users/yujun/tmp/" + id + getUserId() + "/" + name);
        FileOutputStream fileOutputStream = null;
        File file = new File("/Users/yujun/tmp/" + id + getUserId() + "/");
        String[] list = file.list();
        try{
            //合成后的文件
            Integer chunks = Integer.parseInt(list[0].substring(list[0].lastIndexOf(".") + 1));
            byte[] buf = new byte[1024];
            fileOutputStream = new FileOutputStream(file1);
            for (int i = 0; i < chunks; i++) {
                File origin = new File("/Users/yujun/tmp/" + id + getUserId() + "/" + i + "." + chunks);
                InputStream inputStream = new FileInputStream(origin);
                int len = 0;
                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }
                inputStream.close();
                origin.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null != fileOutputStream){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ok();
    }

}
