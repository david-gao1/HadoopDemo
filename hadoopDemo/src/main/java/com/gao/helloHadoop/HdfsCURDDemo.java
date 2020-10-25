package com.gao.helloHadoop;

import org.apache.hadoop.fs.FileSystem;

import static com.gao.helloHadoop.HDFSBasic.*;

/**
 * Java代码操作HDFS
 * 文件操作：上传文件、下载文件、删除文件
 */
public class HdfsCURDDemo {
    public static void main(String[] args) throws Exception {
        FileSystem fileSystem = getFileSystem();
        uploadFile(fileSystem);
        //getfileFromHDFS(fileSystem);
        //deleteFile(fileSystem);
        getFilesFromPath(fileSystem);
    }
}
