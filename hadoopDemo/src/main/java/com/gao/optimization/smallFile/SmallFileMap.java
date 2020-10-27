package com.gao.optimization.smallFile;


import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import java.io.File;

/**
 * 小文件解决方案之MapFile
 * Created by xuwei
 */
public class SmallFileMap {

    public static void main(String[] args) throws Exception {
        //生成MapFile文件
        write("C:\\Users\\v_liangggao\\Desktop\\mapSeq\\", "/mapFile");
        //读取MapFile文件
        read("/mapFile");
    }

    /**
     * 生成MapFile文件
     *
     * @param inputDir  输入目录-windows目录
     * @param outputDir 输出目录-hdfs目录
     * @throws Exception
     */
    private static void write(String inputDir, String outputDir)
            throws Exception {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://192.168.229.101:9000");
        FileSystem fileSystem = FileSystem.get(conf);

        //删除输出目录
        fileSystem.delete(new Path(outputDir), true);


        //构造opts数组，有两个元素
        /*
        第一个是key类型
        第二个是value类型
         */
        SequenceFile.Writer.Option[] opts = new SequenceFile.Writer.Option[]{
                MapFile.Writer.keyClass(Text.class),
                MapFile.Writer.valueClass(Text.class)};

        //创建一个Map的writer实例
        MapFile.Writer writer = new MapFile.Writer(conf, new Path(outputDir), opts);
        File inputDirPath = new File(inputDir);
        if (inputDirPath.isDirectory()) {
            File[] files = inputDirPath.listFiles();
            for (File file : files) {
                String content = FileUtils.readFileToString(file, "UTF-8");
                Text key = new Text(file.getName());
                Text value = new Text(content);
                writer.append(key, value);
            }
        }
        writer.close();
    }

    /**
     * 读取MapFile文件
     *
     * @param inputDir MapFile文件路径
     * @throws Exception
     */
    private static void read(String inputDir)
            throws Exception {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://192.168.229.101:9000");

        //创建阅读器
        MapFile.Reader reader = new MapFile.Reader(new Path(inputDir), conf);
        Text key = new Text();
        Text value = new Text();
        while (reader.next(key, value)) {
            System.out.print("文件名:" + key.toString() + ",");
            System.out.println("文件内容:" + value.toString());
        }
        reader.close();
    }


}
