import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class homework {

    public static void main(String args[]) {
        File inp_file = new File("src/input.txt");
        try {
            Scanner s = new Scanner(inp_file);
            int numQueries = s.nextInt();
            String query = "";
            ArrayList<String> queries = new ArrayList<String>();
            s.nextLine();
            for(int i = 0;i<numQueries;i++){
                //query = s.nextLine().trim();
                queries.add(s.nextLine().trim());
            }
            int numSentences = s.nextInt();
            String sentence = "";
            ArrayList<String> knowledgeBase = new ArrayList<String>();
            s.nextLine();
            for(int i = 0;i<numSentences;i++){
                sentence = s.nextLine().trim();
                // convert to sentences
                knowledgeBase.add(sentence);
            }
//            System.out.println(numQueries);
//            for(int i = 0;i<numQueries;i++){
//                //query = s.nextLine().trim();
//                System.out.println(queries.get(i));
//            }
//            System.out.println(numSentences);
//            for(int i = 0;i<numSentences;i++){
//                System.out.println(knowledgeBase.get(i));
//            }
            Resolution r = new Resolution(queries,knowledgeBase);
            r.convertToCNF();
            //check each query
            boolean[] result = new boolean[numQueries];

            for(int i=0;i<numQueries;i++){
                result[i] = r.evaluateQuery(queries.get(i));
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter("src/output.txt"));
            for(int i=0;i<numQueries;i++) {
                if(result[i])
                    bw.write("TRUE");
                else
                    bw.write("FALSE");
                bw.newLine();
            }
            bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

}