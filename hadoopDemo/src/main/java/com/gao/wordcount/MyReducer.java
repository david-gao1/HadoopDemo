package com.gao.wordcount;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * 创建自定义的reducer类
 *
 * @author v_liangggao
 */
public class MyReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

    /**
     * 接收map端穿过来的值<k2,v2>,进行聚合，最后输出<k3,v3>到本地或是hdfs等其他地方
     *
     * @param k2：一组中任意一个key
     * @param v2s： 相同key下的所有values：
     *           Array、Map和Set都属于iterable类型。
     *           具有iterable类型的集合可以通过新的for ... of循环来遍历。
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void reduce(Text k2, Iterable<LongWritable> v2s, Context context)
            throws IOException, InterruptedException {
        //计算key下的所有value值，进行聚合。
        long sum = 0L;
        for (LongWritable v2 : v2s) {
            sum += v2.get();
        }

        //序列化准备
        Text k3 = k2;
        LongWritable v3 = new LongWritable(sum);
        context.write(k3, v3);
    }
}
