package com.gao.optimization;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 需求：读取SequenceFile文件
 * Created by xuwei
 */
public class WordCountJobSeq {
    /**
     * Map阶段
     */
    public static class MyMapper extends Mapper<Text, Text, Text, LongWritable> {
        Logger logger = LoggerFactory.getLogger(MyMapper.class);

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
        protected void map(Text k1, Text v1, Context context)
                throws IOException, InterruptedException {
            System.out.println("<k1,v1>=<" + k1.toString() + "," + v1.toString() + ">");
            //logger.info("<k1,v1>=<"+k1.get()+","+v1.toString()+">");

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
                //System.out.println("<k2,v2>=<"+k2.toString()+","+v2.get()+">");
                //logger.info("<k2,v2>=<"+k2.toString()+","+v2.get()+">");
                sum += v2.get();
            }

            //组装 k3,v3
            Text k3 = k2;
            LongWritable v3 = new LongWritable(sum);
            //System.out.println("<k3,v3>=<"+k3.toString()+","+v3.get()+">");
            //logger.info("<k3,v3>=<"+k3.toString()+","+v3.get()+">");
            context.write(k3, v3);
        }
    }

    /**
     * 组装Job=Map+Reduce
     */
    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                //如果传递的参数不够，程序直接退出
                System.exit(100);
            }

            //创建job实例
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf);
            job.setJarByClass(WordCountJobSeq.class);

            FileInputFormat.setInputPaths(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));

            //指定map相关的代码
            job.setMapperClass(MyMapper.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(LongWritable.class);

            //设置输入数据处理类
            job.setInputFormatClass(SequenceFileInputFormat.class);


            //指定reduce相关的代码
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
