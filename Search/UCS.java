import java.util.*;

public class UCS {
    int w,h,land_x,land_y,threshold,num_target;
    int target[][];
    int terrain[][];
    NodeUCS source;
    private static final int rows[] = {-1,-1,0,1,1,1,0,-1};
    private static final int columns[] = {0,1,1,1,0,-1,-1,-1};
    private static final int cost[] = {2,3,2,3,2,3,2,3};

    public UCS(int w, int h, int land_x, int land_y, int threshold, int num_target, int[][] target, int[][] terrain) {
        this.w = w;
        this.h = h;
        this.land_x = land_x;
        this.land_y = land_y;
        this.threshold = threshold;
        this.num_target = num_target;
        this.target = target;
        this.terrain = terrain;
        source = new NodeUCS(land_x,land_y, terrain[land_y][land_x],null,0);
    }
    private boolean isValidRowsCols(int x, int y){
        return (x>=0 && x<this.w && y>=0 && y<this.h);
    }

    public List<String> ucsearch_targets(){
        List<String> resultList = new ArrayList<>();
        NodeUCS targetNode;
        String targetResult = "";
        for(int i=0;i<this.num_target;i++){
            targetNode = new NodeUCS(this.target[i][0],this.target[i][1], terrain[this.target[i][1]][this.target[i][0]],null,0);
            targetResult = ucsearch(targetNode);
            resultList.add(targetResult);
        }
        return resultList;
    }

    public String ucsearch(NodeUCS targetNode){
        boolean[][] visited = new boolean[this.h][this.w];
        boolean[][] polled = new boolean[this.h][this.w];
        PriorityQueue<NodeUCS> queue = new PriorityQueue<NodeUCS>(8, new NodeComparator());
        String result = "";
        boolean flag = false;
        visited[this.source.y][this.source.x] = true;
        queue.add(this.source);
        NodeUCS resultNode = null;
        NodeUCS child,node, leave = null;
        while(!queue.isEmpty())
        {
            NodeUCS temp = queue.poll();
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
                            child = new NodeUCS(cx,cy,terrain[cy][cx],temp, cost[k]+temp.pathCost);
                            queue.add(child);
                            
                        }

                    }
                    else if(isValidRowsCols(cx,cy) && visited[cy][cx] && !polled[cy][cx]) {
                        if((Math.abs(terrain[cy][cx] - temp.value)) <= this.threshold){
                            child = new NodeUCS(cx,cy,terrain[cy][cx],temp, cost[k]+temp.pathCost);
                            
                            for(NodeUCS node1: queue){
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
            NodeUCS temp = null;
            Stack<NodeUCS> resultStack = new Stack<NodeUCS>();
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

class NodeUCS{
    int x, y, value, pathCost;
    NodeUCS parent;
    NodeUCS(int x, int y, int value, NodeUCS parent, int pathCost){
        this.x = x;
        this.y = y;
        this.value = value;
        this.parent = parent;
        this.pathCost = pathCost;
    }

    @Override
    public boolean equals(Object o) {
        NodeUCS nucs = (NodeUCS)o;
        if(this.x == nucs.x && this.y == nucs.y){
            return true;
        }
        else
            return false;
    }
}

class NodeComparator implements Comparator<NodeUCS>{

    public int compare(NodeUCS n1, NodeUCS n2){
        if(n1.pathCost < n2.pathCost)
            return -1;
        else if(n1.pathCost > n2.pathCost)
            return 1;
        else return 0;
    }
}

