import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {
    public static class WordMapper extends Mapper<Object, Text, Text, IntWritable> {
	    
        private final static IntWritable one = new IntWritable(1);
        private final int N = 2; // N for N-grams

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if (line == "\n") { //skip empty lines
                return;
            }
            line = line.toLowerCase(); // make lower case
            line = line.replaceAll("[^A-Za-z ]", ""); // get rid of non-alphabetic characters
            String[] all_words = line.split(" ");
            for (int i = 0; i <= all_words.length - N; ++i) {
                // concatenate N words
                String ngram = "";
                for (int j = 0; j < N; ++j) {
                    ngram += all_words[i+j];
                    ngram += " ";
                }
                ngram = ngram.substring(0, ngram.length() - 1); // take out extra space  			
                context.write(new Text(ngram), one);
            }
        }
    }

    public static class WordReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0; 
            for (IntWritable val : values) {
                sum += 1;
            }
            IntWritable res = new IntWritable();
            res.set(sum);
            context.write(key, res);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "n-gram counter");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(WordMapper.class);
        //job.setCombinerClass();
        job.setReducerClass(WordReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
