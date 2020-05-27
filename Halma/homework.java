import java.io.*;
import java.util.List;
import java.util.Scanner;

public class homework {

    public static void main(String args[]) throws CloneNotSupportedException {

        long time_start = System.currentTimeMillis();
        File inp_file =  new File("input.txt");
        try {
            Scanner s = new Scanner(inp_file);
            String inp = s.nextLine();
            Board board = new Board();
            String color = s.nextLine();
            board.color = color;
            char player;
            if(color.equals("BLACK"))
                player = 'B';
            else
                player = 'W';
            board.player = player;
            String boardConfig[]= new String[16];
            board.playTime = s.nextFloat();
            board.startTime =  time_start;
            s.nextLine();
            board.config = new char[16][16];
            for(int i=0;i<16;i++) {
                boardConfig[i] = s.nextLine();
                board.config[i] = boardConfig[i].toCharArray();
            }
            List<Node> bestMove;
            if(inp.equals("GAME")){
                board.buildGoalNCamp();
                GameTree gameTree =  new GameTree(board);
                bestMove = gameTree.playGame(board, player);
                //printOutput(bestMove);
            }
            else {
                board.buildGoalNCamp();
                SingleMove sm = new SingleMove();
                bestMove = sm.singleMove(board,player);
                //printOutput(bestMove);
            }
            
            int n =bestMove.size();
            Node n1,n2;
            String result;
            BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
            for(int i=n-1;i>0;i--){
                n1 = bestMove.get(i);
                n2 = bestMove.get(i-1);
                //System.out.println(n1.x+","+n1.y+";;"+n2.x+","+n2.y);
                if(Math.abs(n1.x - n2.x) > 1 || Math.abs(n1.y - n2.y) > 1){
                    result = "J ";
                }
                else{
                    result = "E ";
                }
                result+= n1.x+","+n1.y+" ";
                result+= n2.x+","+n2.y;
                bw.write(result);
                bw.newLine();
            }
            bw.close();

        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /*public static void printOutput(List<Node> bestMove){
        int n =bestMove.size();
        Node n1,n2;
        String result;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
            for(int i=n-1;i>0;i--){
                n1 = bestMove.get(i);
                n2 = bestMove.get(i-1);
                //System.out.println(n1.x+","+n1.y+";;"+n2.x+","+n2.y);
                if(Math.abs(n1.x - n2.x) > 1 || Math.abs(n1.y - n2.y) > 1){
                    result = "J ";
                }
                else{
                    result = "E ";
                }
                result+= n1.x+","+n1.y+" ";
                result+= n2.x+","+n2.y;
                bw.write(result);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
