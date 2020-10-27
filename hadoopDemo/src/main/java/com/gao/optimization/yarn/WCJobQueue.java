package com.gao.optimization.yarn;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;

/**
 * 指定队列名称
 * <p>
 * Created by xuwei
 */
public class WCJobQueue {
    /**
     * Map阶段
     */
    public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
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
            for (String word : words) {
                Text k2 = new Text(word);
                LongWritable v2 = new LongWritable(1L);
                context.write(k2, v2);
            }
        }
    }


    /**
     * Reduce阶段
     */
    public static class MyReducer extends Reducer<Text, LongWritable, Text, LongWritable> {
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
            }


            Text k3 = k2;
            LongWritable v3 = new LongWritable(sum);
            context.write(k3, v3);
        }
    }

    /**
     * 组装Job=Map+Reduce
     */
    public static void main(String[] args) {
        try {
            Configuration conf = new Configuration();
            //解析命令行中-D后面传递过来的参数，添加到conf中
            String[] remainingArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

            //创建一个Job
            Job job = Job.getInstance(conf);
            job.setJarByClass(WCJobQueue.class);

            FileInputFormat.setInputPaths(job, new Path(remainingArgs[0]));
            FileOutputFormat.setOutputPath(job, new Path(remainingArgs[1]));

            job.setMapperClass(MyMapper.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(LongWritable.class);
            job.setReducerClass(MyReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(LongWritable.class);

            //提交job
            job.waitForCompletion(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
