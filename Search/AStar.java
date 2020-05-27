import java.util.*;

public class AStar {
    int w,h,land_x,land_y,threshold,num_target;
    int target[][];
    int terrain[][];
    NodeAS source;
    private static final int rows[] = {-1,-1,0,1,1,1,0,-1};
    private static final int columns[] = {0,1,1,1,0,-1,-1,-1};
    private static final int cost[] = {2,3,2,3,2,3,2,3};

    public AStar(int w, int h, int land_x, int land_y, int threshold, int num_target, int[][] target, int[][] terrain) {
        this.w = w;
        this.h = h;
        this.land_x = land_x;
        this.land_y = land_y;
        this.threshold = threshold;
        this.num_target = num_target;
        this.target = target;
        this.terrain = terrain;
        source = new NodeAS(land_x,land_y, terrain[land_y][land_x],null,0,0);
    }

    private boolean isValidRowsCols(int x, int y){
        return (x>=0 && x<this.w && y>=0 && y<this.h);
    }

    private int distance(int x, int y, int z, NodeAS n2){
        return (int)(Math.sqrt(Math.pow(x - n2.x,2) + Math.pow(y-n2.y,2) + Math.pow(z - n2.value,2)));
    }

    public List<String> asearch_targets(){
        List<String> resultList = new ArrayList<>();
        NodeAS targetNode;
        String targetResult = "";
        for(int i=0;i<this.num_target;i++){
            targetNode = new NodeAS(this.target[i][0],this.target[i][1], terrain[this.target[i][1]][this.target[i][0]],null,0,0);
            targetResult = asearch(targetNode);
            resultList.add(targetResult);
        }
        return resultList;
    }

    public String asearch(NodeAS targetNode){
        boolean[][] visited = new boolean[this.h][this.w];
        boolean[][] polled = new boolean[this.h][this.w];
        PriorityQueue<NodeAS> queue = new PriorityQueue<NodeAS>(8, new NodeComparatorAS());
        String result = "";
        boolean flag = false;
        visited[this.source.y][this.source.x] = true;
        this.source.heuristic = distance(this.source.x, this.source.y, this.source.value, targetNode);
        queue.add(this.source);
        NodeAS resultNode = null;
        NodeAS child,node, leave = null;
        int heu =0;
        while(!queue.isEmpty())
        {
            NodeAS temp = queue.poll();
            polled[temp.y][temp.x] = true;
            if(temp.x == targetNode.x && temp.y == targetNode.y){

                resultNode = temp;
                flag = true;
                break;
            }
            else{
                for(int k =0;k<8;k++){
                    leave = null;
                    int cx = temp.x+columns[k]; int cy = temp.y+rows[k];

                    if (isValidRowsCols(cx,cy) && !visited[cy][cx]){
                        if((Math.abs(terrain[cy][cx] - temp.value)) <= this.threshold){
                            visited[cy][cx] = true;
                            heu = distance(cx,cy,terrain[cy][cx], targetNode);
                            child = new NodeAS(cx,cy,terrain[cy][cx],temp, cost[k]+temp.pathCost + Math.abs(temp.value - terrain[cy][cx]), heu );
                            queue.add(child);
                      
                        }

                    }
                    else if(isValidRowsCols(cx,cy) && visited[cy][cx] && !polled[cy][cx]) {
                        if((Math.abs(terrain[cy][cx] - temp.value)) <= this.threshold){
                            heu = distance(cx,cy,terrain[cy][cx], targetNode);
                            child = new NodeAS(cx,cy,terrain[cy][cx],temp, cost[k]+temp.pathCost + Math.abs(temp.value - terrain[cy][cx]), heu);
                            for(NodeAS node1: queue){
                                if (node1.equals(child)){
                                    if(node1.pathCost> child.pathCost){
                                        leave = node1;
                                    }
                                }
                            }
                            if(leave!= null) {
                                queue.remove(leave);
                                queue.add(child);
                            }
                        }
                    }

                }

            }

        }
        if(flag == true){
            NodeAS temp = null;
            Stack<NodeAS> resultStack = new Stack<NodeAS>();
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

class NodeAS{
    int x, y, value, pathCost, heuristic;
    NodeAS parent;
    NodeAS(int x, int y, int value, NodeAS parent, int pathCost, int heuristic){
        this.x = x;
        this.y = y;
        this.value = value;
        this.parent = parent;
        this.pathCost = pathCost;
        this.heuristic = heuristic;
    }

    @Override
    public boolean equals(Object o) {
        NodeAS nucs = (NodeAS)o;
        if(this.x == nucs.x && this.y == nucs.y ){
            return true;
        }
        else
            return false;
    }
}

class NodeComparatorAS implements Comparator<NodeAS> {

    public int compare(NodeAS n1, NodeAS n2){
        if(n1.pathCost+n1.heuristic < n2.pathCost+n2.heuristic)
            return -1;
        else if(n1.pathCost+n1.heuristic > n2.pathCost+n2.heuristic)
            return 1;
        else return 0;
    }
}