package com.caipiao.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/20 16:40
 */
@Slf4j
public class CloudFileUtil {
    /**
     * 生成文件存储路径
     * @param filename
     * @return
     */
    public static String makePath(String filename) {
        //得到文件名的hashCode的值，得到的就是filename这个字符串对象在内存中的地址
        int hashcode = filename.hashCode();
        int dir1 = hashcode & 0xf;  //0--15
        int dir2 = (hashcode & 0xf0) >> 4;  //0-15
        //构造新的保存目录
        String dir = dir1 + "/" + dir2 + "/";

        return dir;
    }

    /**
     * 删除目录下的所有文件
     * @param file
     * @return
     */
    public static int deleteFile(File file){
        int flag = 0;
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()){
            flag = 0;
            log.debug("文件删除失败,请检查文件路径是否正确");
            return flag;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f: files){
            //打印文件名
            String name = file.getName();
            log.debug("待删除文件名：{}", name);
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()){
                deleteFile(f);
            }else {
                f.delete();
            }
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。
        file.delete();
        return 1;
    }
}
