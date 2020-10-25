package com.gao.helloHadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HDFSBasic {
    /**
     * 获取某一个文件夹下的所有信息
     * 1、没有递归遍历，只是看此文件夹下
     * 2、没有任何文件，fileStatuses的长度为0
     *
     * @param fileSystem
     * @throws IOException
     */
    public static void getFilesFromPath(FileSystem fileSystem) throws IOException {
        final FileStatus[] fileStatuses = fileSystem.listStatus(new Path("/"));
        System.out.println(fileStatuses.length);
        for (FileStatus fileStatus : fileStatuses) {
            System.out.println("isFile :" + fileStatus.isFile() + ",the path is:" + fileStatus.getPath() + ",length:" + fileStatus.getLen());
        }
    }

    /**
     * 删除
     *可以递归删除
     *
     * @param fileSystem
     * @throws IOException
     */
    public static void deleteFile(FileSystem fileSystem) throws IOException {
        boolean isDeleted = fileSystem.delete(new Path("/user.txt"), false);
        if (isDeleted) {
            System.out.println("删除成功");
        } else {
            System.out.println("删除失败");
        }
    }

    /**
     * 下载文件
     *
     * @param fileSystem
     * @throws IOException
     */
    public static void getfileFromHDFS(FileSystem fileSystem) throws IOException {
        FSDataInputStream fsDataInputStream = fileSystem.open(new Path("/dd/user.txt"));
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\v_liangggao\\Desktop\\hadoop_test2.txt");
        IOUtils.copyBytes(fsDataInputStream, fileOutputStream, 1024, true);//使用下载
    }

    /**
     * 获取hdfs操作实例
     *
     * @return
     * @throws IOException
     */
    public static FileSystem getFileSystem() throws IOException {
        Configuration conf = new Configuration();// 配置信息类，任何作用的配置信息必须通过Configuration传递，因为通过Configuration可以实现在多个mapper和多个reducer任务之间共享信息。
        conf.set("fs.defaultFS", "hdfs://192.168.229.101:9000");//conf 中的fs.defaultFS参数告诉具体化客户端类是什么
        return FileSystem.get(conf);//获取操作hdfs的对象
    }

    /**
     * 上传文件
     * 工具类也可以实现下载
     *
     * @param fileSystem
     * @throws IOException
     */
    public static void uploadFile(FileSystem fileSystem) throws IOException {
        FileInputStream fis = new FileInputStream("C:\\Users\\v_liangggao\\Desktop\\wc_test.txt");
        FSDataOutputStream fos = fileSystem.create(new Path("/gao1/user.txt"));
        IOUtils.copyBytes(fis, fos, 1024, true); //使用工具类上传文件
    }

}
