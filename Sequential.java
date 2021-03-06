import java.util.*;
import java.io.File;
import java.io.*;
public class Sequential {
    private static final int N = 2;
    public static void main(String[] args) throws IOException {
        String file = "100_bibles.txt";
/*        if (args[0] != null) {
            file = args[0];
        } */
        long start = System.currentTimeMillis();
        Scanner scanner = new Scanner( new File(file));
        HashMap<String, Integer> word_count = new HashMap<String, Integer>();

        do {
            String text = scanner.nextLine();
            if (text == "\n") { //skip empty lines
                continue;
            }
            text = text.toLowerCase();
            text = text.replaceAll("[^A-Za-z ]", "");
            String[] all_words = text.split(" ");
            for (int i = 0; i <= all_words.length - N; ++i) {
                // concatenate N words
                String ngram = "";
                for (int j = 0; j < N; ++j) {
                    ngram += all_words[i+j];
                    ngram += " ";
            }
            ngram = ngram.substring(0, ngram.length() - 1); // take out extra space             
            if (!word_count.containsKey(ngram)) {
                word_count.put(ngram, 1);
            } else {
                int temp = word_count.get(ngram) + 1;
                word_count.put(ngram, temp);
            }
        }
        } while(scanner.hasNextLine());
        
        scanner.close(); // Put this call in a finally block 

        Iterator it = word_count.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //System.out.println(pair.getKey() + "\t" + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }

        System.out.println( ( (double) (System.currentTimeMillis() - start)/ (double) 1000) + "seconds");

    }

}
