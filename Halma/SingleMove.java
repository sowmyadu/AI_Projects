
import java.util.*;

class SingleMove{
    int depth = 3;
    Board board;

    public List<Node> singleMove(Board board, char player){

        Node singleMove = getMoveForPlayerPieces(board,player);
        List<Node> nodeList = new ArrayList<Node>();

        while(singleMove.parent!=null){
            nodeList.add(singleMove);
            singleMove = singleMove.parent;

        }
        nodeList.add(singleMove);
        return nodeList;
    }

    public boolean isValidMove(int x, int y, char player, Board board){
        if(x>15 || y >15 || x < 0 || y < 0 )
            return false;
        return true;
    }

    public List<Node> getPiecesInCamp(Board board,char player){
        List<Node> inCamp = new ArrayList<Node>();
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                if(board.config[j][i] == player && board.camp[j][i] == player)
                    inCamp.add(new Node(i,j));
            }
        }
        return inCamp;
    }

    public List<Node> getPlayerPiecesList(Board board,char player){
        List<Node> playerList = new ArrayList<Node>();
        double distance = 0;
        for(int j=0;j<16;j++){
            for(int i=0;i<16;i++) {
                if (board.config[i][j] == player) {
                    distance = utility(j,i,player);
                    playerList.add(new Node(j, i,distance));
                }
            }

        }
        return playerList;
    }

    public List<Node> getPiecesOutsideGoal(Board board,char player){
        List<Node> outGoal = new ArrayList<Node>();
        double distance = 0;
        int k=0;
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                if(board.config[j][i] == player && board.goal[j][i] != player && board.camp[j][i]!=player){
                    //distance = utility(i, j, player);
                    outGoal.add(new Node(i, j, distance));
                }
            }
        }
        return outGoal;
    }

    //each row has piece original config as first element
    public Node getMoveForPlayerPieces(Board board, char player){
        List<Node> piecesInCamp = getPiecesInCamp(board,player);
        List<Node> outGoalList = getPiecesOutsideGoal(board,player);
        Node move = null,temp;
        boolean inGoal = false;
        if(piecesInCamp.size()!= 0) {
            // there are still pieces in camp;
            for (int i = 0; i < piecesInCamp.size(); i++) {
                move = findSingleMoves(board, piecesInCamp.get(i), player,false);
                if (move != null)
                    break;
            }
        }
        if(outGoalList.size() !=0 && move ==null) {

            for (int i = 0; i < outGoalList.size(); i++) {
                move = findSingleMoves(board, outGoalList.get(i), player,false);
                if (move != null)
                    break;
            }
        }
        if(move==null){
            List<Node> playerList = getPlayerPiecesList(board, player);
            for (int i = 0; i < playerList.size(); i++) {
                temp = playerList.get(i);
                if (board.goal[temp.x][temp.y] == player)
                    inGoal = true;
                else
                    inGoal = false;
                move = findSingleMoves(board, playerList.get(i), player,inGoal);
                if (move != null)
                    break;
            }
        }

        return move;
    }

    public Node findSingleMoves(Board board,Node n,char player,boolean inGoal){
        int x = n.x;
        int y = n.y;
        n.parent = null;
        List<Node> movesList =  new ArrayList<Node>();

        boolean found =false;
        Node move = null;
        for(int i=-1;i<=1;i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;
                if (!isValidMove(x + i, y + j, player, board))
                    continue;
                if(player == 'B') {
                    if (board.camp[y][x] == player && (i + j < 1)) {
                        continue;
                    }
                }
                else{
                    if (board.camp[y][x] == player && (j-i < 1)) {
                        continue;
                    }
                }
                if (board.config[y + j][x + i] == '.' && board.camp[y+j][x+i] != player) {

                    if(inGoal) {
                        if(board.goal[x+i][y+j]==player) {
                            move = new Node(x + i, y + j, n);
                            found = true;
                            return move;
                        }
                    }
                    else{
                        move = new Node(x + i, y + j, n);
                        found = true;
                        return move;
                    }
                }
            }
        }
        if(!found) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0)
                        continue;
                    if (!isValidMove(x + i, y + j, player, board))
                        continue;
                    if (board.config[y + j][x + i] == 'B' || board.config[y + j][x + i] == 'W') {
                        if (isValidSpaceForRecurJump(x + i * 2, y + j * 2,board,player)) { //check for empty space and not in goal state or another piece

                            if(inGoal) {
                                if(board.goal[x+i*2][y+j*2]==player) {
                                    found = true;
                                    move = new Node(x + i * 2, y + j * 2, n);
                                    return move;
                                }
                                //ToDo: call else with recursive jump

                            }
                            else{
                                found = true;
                                move = new Node(x + i * 2, y + j * 2, n);
                                return move;
                            }
                            //move = getJumpSingleMoves(board, x + i * 2, y + j * 2, x, y, move);
                        }
                    }
                }
            }
        }
        return move;

    }

    public boolean isValidSpaceForRecurJump(int x, int y,Board board, char player){
        if(x < 0 || x>15 || y<0 || y> 15)
            return false;
        else if(board.config[x][y]=='.' )
            return true;
        else
            return false;
    }

    public double utility(int x,int y, char player){
        int i, j;
        if(player == 'B'){
            i = 15;
            j = 15;
        }
        else{
            i = 0;
            j = 0;
        }
        return Math.sqrt((x - i)*(x - i)+(y - j)*(y - j));
    }

    public double evaluateBoard(Board board, char player){
        List<Node> playerList = getPlayerPiecesList(board,player);
        double score =0;
        for(int i=0;i<playerList.size();i++){
            score += playerList.get(i).value;
        }
        return score;
    }

}