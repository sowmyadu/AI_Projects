import java.util.*;


public class BFS {
    int w,h,land_x,land_y,threshold,num_target;
    int target[][];
    int terrain[][];
    Node source;
    private static final int rows[] = {-1,-1,0,1,1,1,0,-1};
    private static final int columns[] = {0,1,1,1,0,-1,-1,-1};

    public BFS(int w, int h, int land_x, int land_y, int threshold, int num_target, int[][] target, int[][] terrain) {
        this.w = w;
        this.h = h;
        this.land_x = land_x;
        this.land_y = land_y;
        this.threshold = threshold;
        this.num_target = num_target;
        this.target = target;
        this.terrain = terrain;
        source = new Node(land_x,land_y, terrain[land_y][land_x],null);

    }

    private boolean isValidRowsCols(int x, int y, boolean visited[][]){
        return (x>=0 && x<this.w && y>=0 && y<this.h && !visited[y][x]);
    }

    public List<String> bfsearch_targets(){
        List<String> resultList = new ArrayList<>();
        Node targetNode;
        String targetResult = "";
        for(int i=0;i<this.num_target;i++){
            targetNode = new Node(this.target[i][0],this.target[i][1], terrain[this.target[i][1]][this.target[i][0]],null);
            targetResult = bfsearch(targetNode);
            resultList.add(targetResult);
        }
        return resultList;
    }

    public String bfsearch(Node targetNode){
        boolean[][] visited = new boolean[this.h][this.w];
        Queue<Node> queue = new LinkedList<>();
        String result = "";
        boolean flag = false;
        // queue the source
        visited[this.source.y][this.source.x] = true;
        queue.add(this.source);
        Node resultNode = null;

        while(!queue.isEmpty())
        {
            Node temp = queue.poll();

            if(temp.x == targetNode.x && temp.y == targetNode.y){

                resultNode = temp;
                flag = true;
                break;
            }
            else{
                for(int k =0;k<8;k++){
                    int cx = temp.x+columns[k]; int cy = temp.y+rows[k];
                    if (isValidRowsCols(cx,cy,visited)){
                        if((Math.abs(terrain[cy][cx] - temp.value)) <= this.threshold){
                            visited[cy][cx] = true;
                            queue.add(new Node(cx,cy,terrain[cy][cx],temp));
                        }
                    }

                }

            }

        }
        if(flag == true){
            Node temp = null;
            Stack<Node> resultStack = new Stack<Node>();
            //resultStack.add(resultNode);
            temp = resultNode;
            result = this.source.x+","+this.source.y;
            while(temp.parent!=null){
                resultStack.push(temp);
                temp = temp.parent;

            }
            while(!resultStack.empty()){
                temp = resultStack.pop();
                result += " "+ temp.x + ","+temp.y;
            }
        }
        else if(flag == false){
            result = "FAIL";
        }
        return result;
    }


}

class Node{
    int x, y, value;
    Node parent;
    Node(int x, int y, int value, Node parent){
        this.x = x;
        this.y = y;
        this.value = value;
        this.parent = parent;
    }
}
