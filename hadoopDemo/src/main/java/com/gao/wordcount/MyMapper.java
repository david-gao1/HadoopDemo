package com.gao.wordcount;


import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 创建自定义mapper类
 */
public class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

    /**
     * 进行map
     *
     * @param k1：代表每一行的行首在文件中的行首偏移量，
     * @param v1：代表每一行的内容
     * @param context：上下对象，传递给reduce的内容
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable k1, Text v1, Context context)
            throws IOException, InterruptedException {
        String[] words = v1.toString().split(" ");
        for (String word : words) {
            //1、传递的逻辑：(单词，词频)
            //2、数据序列化：数据类型处理为context可接受参数类型
            Text k2 = new Text(word);
            LongWritable v2 = new LongWritable(1L);
            context.write(k2, v2);
        }
    }

}

