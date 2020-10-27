package com.gao.optimization.dataSkews;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;

/**
 * 数据倾斜-把倾斜的数据打散
 */
public class WCJobSkewRandKey {
    /**
     * Map阶段
     */
    public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
        Logger logger = LoggerFactory.getLogger(MyMapper.class);
        Random random = new Random();

        /**
         * 需要实现map函数
         * 这个map函数就是可以接收<k1,v1>，产生<k2，v2>
         *
         * @param k1
         * @param v1
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void map(LongWritable k1, Text v1, Context context)
                throws IOException, InterruptedException {
            String[] words = v1.toString().split(" ");
            String key = words[0];
            if ("5".equals(key)) {
                //把倾斜的key打散，分成10份
                key = "5" + "_" + random.nextInt(10);
            }

            Text k2 = new Text(key);
            LongWritable v2 = new LongWritable(1L);
            context.write(k2, v2);
        }
    }


    /**
     * Reduce阶段
     */
    public static class MyReducer extends Reducer<Text, LongWritable, Text, LongWritable> {
        Logger logger = LoggerFactory.getLogger(MyReducer.class);

        /**
         * 针对<k2,{v2...}>的数据进行累加求和，并且最终把数据转化为k3,v3写出去
         *
         * @param k2
         * @param v2s
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void reduce(Text k2, Iterable<LongWritable> v2s, Context context)
                throws IOException, InterruptedException {
            long sum = 0L;
            for (LongWritable v2 : v2s) {
                sum += v2.get();
                //模拟Reduce的复杂计算消耗的时间
                if (sum % 200 == 0) {
                    Thread.sleep(1);
                }
            }

            Text k3 = k2;
            LongWritable v3 = new LongWritable(sum);
            // 把结果写出去
            context.write(k3, v3);
        }
    }

    /**
     * 组装Job=Map+Reduce
     */
    public static void main(String[] args) {
        try {
            if (args.length != 3) {
                //如果传递的参数不够，程序直接退出
                System.exit(100);
            }

            //指定Job需要的配置参数
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf);
            job.setJarByClass(WCJobSkewRandKey.class);


            FileInputFormat.setInputPaths(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));

            //指定map相关的代码
            job.setMapperClass(MyMapper.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(LongWritable.class);
            job.setReducerClass(MyReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(LongWritable.class);

            //设置reduce任务个数
            job.setNumReduceTasks(Integer.parseInt(args[2]));

            //提交job
            job.waitForCompletion(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
