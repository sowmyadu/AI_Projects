
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class homework {
    public static void main(String args[]){
        File inp_file =  new File("input.txt");
        try {
            Scanner s = new Scanner(inp_file);
            String str, alg = "";
            List<String> result = new ArrayList<>();
            int w = 0, h =0, land_x =0, land_y = 0, threshold= 0;
            int num_target = 0;
            int target[][] = new int[0][];
            int terrain[][] = new int[0][];
            while(s.hasNextLine())
            {
                alg = s.nextLine();
                w = s.nextInt();
                h = s.nextInt();
                terrain = new int[h][w];
                land_x = s.nextInt();
                land_y = s.nextInt();
                threshold = s.nextInt();
                num_target = s.nextInt();
                target = new int[num_target][2];
                for(int i=0;i<num_target;i++)
                {
                    target[i][0] = s.nextInt();
                    target[i][1] = s.nextInt();
                }
                for(int i=0;i<h;i++) {
                    for (int j = 0; j < w; j++) {
                        terrain[i][j] = s.nextInt();
                    }
                }
            }

            if(alg.equals("BFS")){
                BFS bfs =new BFS(w,h,land_x,land_y,threshold,num_target,target,terrain);
                result = bfs.bfsearch_targets();
            }
            else if(alg.equals("UCS")){
                UCS usc = new UCS(w,h,land_x,land_y,threshold,num_target,target,terrain);
                result = usc.ucsearch_targets();
            }
            else if(alg.equals("A*")){
                AStar astar = new AStar(w,h,land_x,land_y,threshold,num_target,target,terrain);
                result = astar.asearch_targets();
            }

            // writing to output file
            BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
            for(int i=0;i<result.size();i++){
                bw.write(result.get(i));
                bw.newLine();
            }
            bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
