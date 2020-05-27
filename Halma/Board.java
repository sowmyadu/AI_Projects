import java.util.*;

class Board implements Cloneable{

    char config[][];
    String color;
    char player;
    float playTime;
    long startTime;
    char goal[][];
    char camp[][];

    @Override
    public Board clone() throws CloneNotSupportedException{
        return (Board) super.clone();
    }


    // ToDo Persist Utility values across moves

    public void getPiecesList(){
        Map<Node,Character> piecesList = new HashMap<Node,Character>();
        List maxList = new ArrayList<Node>();
        Set<Node> minList = new HashSet<Node>();
        Set<Node> emptyList = new HashSet<Node>();
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                if(config[i][j] == 'B')
                    maxList.add(new Node(i,j));
                else if(config[i][j] == 'W')
                    minList.add(new Node(i,j));
                else
                    emptyList.add(new Node(i,j));
            }
        }
    }


    public void buildGoalNCamp(){
        this.goal = new char[16][16];
        this.camp =  new char[16][16];
        for(int i=0;i<=4;i++) {
            for (int j = 0; j <= 4; j++) {
                    if (i + j < 3) {
                        this.goal[4-i][4-j] = '.';
                        continue;
                    } else {
                        if(color.equals("WHITE"))
                            this.goal[4 - i][4 - j] = 'W';
                        else
                            this.camp[4-i][4-j] = 'B';
                    }
            }
        }
        for(int i=4;i>=0;i--) {
            for (int j = 4; j >= 0; j--) {
                    if(i+j<3)
                        continue;
                    else{
                        if(color.equals("BLACK"))
                            this.goal[11+i][11+j] = 'B';
                        else
                            this.camp[11+i][11+j] = 'W';
                    }

            }
        }
    }

}

class Node{

    // Todo 2: check x, y to be interchanged

    // ToDo: Override Equals
    int x;
    int y;
    Node parent = null;
    double value = 0;
    double boardScore = 0;

    Node(int i,int j){
        this.x =i;
        this.y =j;
    }

    Node(int i,int j, Node parent){
        this.x = i;
        this.y = j;
        this.parent = parent;
    }

    Node(int i,int j, double distance){
        this.x = i;
        this.y = j;
        this.value = distance;
    }

}

class GameTree{
    int depth = 3;
    Board board;

    GameTree(Board board){
        this.board = board;
    }

    // ToDo : Print path along with 'E' or 'J'
    public List<Node> playGame(Board board, char player) throws CloneNotSupportedException {
        //Node n = null;
        //printBoardTestFunc(board);
        Node n = minimax(board,player);

        List<Node> moveList = new ArrayList<Node>();

        while(n.parent!=null){
            moveList.add(n);
            n = n.parent;
        }
        moveList.add(n);
        return moveList;
    }


    // get all valid moves & check for valid moves
    // get all moves for all pieces
    // run minimax with AB of depth 3
    public boolean isValidMove(int x, int y, char player, Board board){
        if(x>15 || y >15 || x < 0 || y < 0)
            return false;
        return true;
    }

    public List<Node> getPlayerPiecesList(Board board,char player){
        List playerList = new ArrayList<Node>();
       // List piecesInCamp = new ArrayList<Node>();
        double distance = 0;
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++) {
                if (board.config[j][i] == player) {
                    distance = utility(i,j,player,board);
                    playerList.add(new Node(i, j,distance));
                }
            }
        }
        return playerList;
    }

    public List<Node> getPiecesInCamp(Board board,char player){
        List<Node> inCamp = new ArrayList<Node>();
        double distance = 0;
        //int k=0;
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                if(board.config[j][i] == player && board.camp[j][i] == player) {
                    distance = utility(i, j, player,board);
                    inCamp.add(new Node(i, j, distance));
                }
            }
        }
        return inCamp;
    }

    public List<Node> getPiecesOutsideGoal(Board board,char player){
        List<Node> outGoal = new ArrayList<Node>();
        double distance = 0;
        int k=0;
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                if(board.config[j][i] == player && board.goal[j][i] != player && board.camp[j][i]!=player){
                    distance = utility(i, j, player,board);
                    outGoal.add(new Node(i, j, distance));
                }
            }
        }
        return outGoal;
    }

    //each row has piece original config as first element
    public List<List<Node>> getAllMovesForPlayer(Board board, char player){
        List<List<Node>> result = new ArrayList<List<Node>>();
        List<Node> eachList;
        boolean inGoal = false;
        List<Node> piecesInCamp = getPiecesInCamp(board,player);
        List<Node> outGoalList = getPiecesOutsideGoal(board,player);
        if(piecesInCamp.size()!= 0) {
            // there are still pieces in camp;
            for (int i = 0; i < piecesInCamp.size(); i++) {
                eachList = findMoves(board, piecesInCamp.get(i), player,inGoal);
                //System.out.println(eachList.size());
                eachList.add(0, piecesInCamp.get(i));
                if(eachList.size()>1)
                    result.add(eachList);
            }

        }
        else if(result.size() == 0 && outGoalList.size() !=0) {
            Node temp;
            for (int i = 0; i < outGoalList.size(); i++) {
                temp = outGoalList.get(i);
                eachList = findMoves(board, temp, player, false);
                if (eachList.size() != 0) {
                    eachList.add(0, temp);
                    result.add(eachList);
                }
            }
        }
        else if(result.size() == 0) {
            List<Node> playerList;
            Node temp;
            playerList = getPlayerPiecesList(board, player);
            for (int i = 0; i < playerList.size(); i++) {
                temp = playerList.get(i);
                if (board.goal[temp.x][temp.y] == player)
                    inGoal = true;
                else
                    inGoal = false;
                eachList = findMoves(board, playerList.get(i), player, inGoal);
                if (eachList.size() != 0) {
                    eachList.add(0, playerList.get(i));
                    result.add(eachList);
                }
            }
        }

        return result;
    }

    // ToDo: Prioritize Jumps,i.e, prioritize distance function
    public List<Node> findMoves(Board board,Node n,char player,boolean inGoal){
        int x = n.x;
        int y = n.y;
        n.parent = null;
        //Set<Node> movesSet = new HashSet<Node>();
        List<Node> movesList =  new ArrayList<Node>();
        List<Node> jumpList = new ArrayList<Node>();
        boolean visited[][] = new boolean[16][16];
        visited[y][x] = true;
        Node move;
        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                if(i==0 && j==0)
                    continue;
                if(!isValidMove(x+i,y+j,player,board))
                    continue;
                if(player == 'B') {
                    if (board.camp[y][x] == player && (i + j < 1)) {
                        continue;
                    }
                }
                else{
                    if (board.camp[y][x] == player && ( i-j < 1)) {
                        continue;
                    }
                }
                if(board.config[y+j][x+i] == '.'){
                    //Add it to set or List
                    move = new Node(x+i,y+j,n);
                    if(inGoal) {
                        if(board.goal[x+i][y+j]==player)
                            movesList.add(move);
                    }
                    else
                        movesList.add(move);
                    visited[y+j][x+i]= true;
                }
                else if(board.config[y+j][x+i] == 'B' || board.config[y+j][x+i] == 'W'){
                    if(isValidSpaceForRecurJump(x+i*2,y+j*2,board,player)) { //check for empty space and not in goal state or another piece

                        move = new Node(x+i*2,y+j*2, n);
                        if(inGoal) {
                            if(board.goal[y+j*2][x+i*2]==player)
                                jumpList.add(move);
                        }
                        else
                            jumpList.add(move);
                        //movesList.add(move);
                        visited[y+j*2][x+i*2] = true;
                        getJumpMoves(board,x + i*2, y + j*2, x, y,jumpList,visited, move,player,inGoal);
                        movesList.addAll(0,jumpList);
                    }
                }
            }
        }
        //System.out.println(movesList.size());
        return movesList;

    }

    // ToDo: check for terminating condition
    public void getJumpMoves(Board board,int x, int y, int ox,int oy, List<Node> jumpList, boolean[][] visited, Node parent, char player, boolean inGoal) {
        Node move;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if(i==0 && j==0)   //piece location
                    continue;
                if(!isValidMove(x+i,y+j,player,board))
                    continue;
                if(board.config[y+j][x+i] == '.')
                    continue;
                if(board.config[y+j][x+i] == 'B' || board.config[y+j][x+i] == 'W'){
                    if(isValidSpaceForRecurJump(x+i*2,y+j*2,board,player) && (x+i*2!=ox) && (y+j*2!=oy) && !visited[y+j*2][x+i*2]) {
                        move = new Node(x+i*2,y+j*2,parent);
                        if(inGoal) {
                            if(board.goal[x+i*2][y+j*2] == player)
                                jumpList.add(move);
                        }
                        else
                            jumpList.add(move);
                        //movesList.add(move);
                        visited[y+j*2][x+i*2] = true;
                        getJumpMoves(board,x + i*2, y + j*2, ox, oy,jumpList,visited,move, player, inGoal);
                    }
                }
            }
        }

    }

    //ToDo : complete rules for jumps
    public boolean isValidSpaceForRecurJump(int x, int y,Board board, char player){

        if(x < 0 || x>15 || y<0 || y> 15 )
            return false;
        else if(board.config[y][x]=='.' )
            return true;
        else
            return false;
    }

    public double utility(int x,int y, char player,Board board){
        int i, j;
        double util=0;
        if(player == 'B'){
            i = 15;
            j = 15;
        }
        else{
            i = 0;
            j = 0;
        }
        util = Math.sqrt((x - i)*(x - i)+(y - j)*(y - j));
        if(board.goal[y][x] == player){
            util = util+1000;
        }
        else
            util = -util;
        return util;
    }

    // ToDo: if one piece is in goal camp: assign a value to it
    public double evaluateBoard(Board board, char player){

        List<Node> playerList = getPlayerPiecesList(board,board.player);
        player = board.player;
        double score = 0,oppScore=0;
        for(int i=0;i<playerList.size();i++) {

            score += utility(playerList.get(i).x, playerList.get(i).y, player, board);

        }

        playerList = getPlayerPiecesList(board,opponent(board.player));
        for(int i=0;i<playerList.size();i++){
            oppScore -= utility(playerList.get(i).x, playerList.get(i).y, player, board);
        }
        return score +oppScore;
    }

    public Node minimax(Board board,char player) throws CloneNotSupportedException {
        //call minimaxABprining with a copy of Board
        // get the corresponding max value and move
        // return the move back

        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        Node bestMove;
        //List<Node> outsideGoal = getPiecesOutsideGoal(board,player);
        //if(outsideGoal.size()<3 && ((System.currentTimeMillis() - board.startTime)/1000.0)<= board.playTime-50))
        //    this.depth = 5;
       
        Node move = null;
        bestMove = minimaxABpruning(board,0, move,player,this.depth,alpha,beta, true);
        return bestMove;
    }

    public Node minimaxABpruning(Board board, int ctr,Node move, char player, int depth, double alpha,double beta, boolean maxi) throws CloneNotSupportedException {

        if(((System.currentTimeMillis() - board.startTime)/1000)>= board.playTime-1){
            double score = evaluateBoard(board, player);
            move.boardScore = score;
            return move;
        }

        List<List<Node>> allPossibleMoves = getAllMovesForPlayer(board, player);

        if (depth == 0 || allPossibleMoves.size() ==0 ) {

            double score = evaluateBoard(board, player);
            move.boardScore = score;
            return move;
        }

        //ToDo : Add Time condition
        if (check_win(board,opponent(player))&& (!maxi)) {
            move.boardScore = Double.POSITIVE_INFINITY;
            return move;
        }
        else if(check_win(board,opponent(player)) && (maxi)){
            move.boardScore = Double.NEGATIVE_INFINITY;
            return move;
        }


        double bestVal = 0;
        Node n,on,bestMove = null;

        Board tempBoard = board;
        if (maxi) {
            bestVal = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < allPossibleMoves.size(); i++) {
                on = allPossibleMoves.get(i).get(0);
                for (int j = 1; j < allPossibleMoves.get(i).size(); j++) {
                    //tempBoard = make a move
                    //tempBoard = board.clone();
                    n = allPossibleMoves.get(i).get(j);

                    tempBoard.config[n.y][n.x] = player;
                    tempBoard.config[on.y][on.x] = '.';

                    Node lastMove = minimaxABpruning(tempBoard, ctr++, n, opponent(player), depth - 1, alpha, beta, !maxi);
                        // Reset board - Y

                    tempBoard.config[n.y][n.x] = '.';
                    tempBoard.config[on.y][on.x] = player;

                    if(lastMove.boardScore == Double.NEGATIVE_INFINITY)
                    {
                        if(move == null){
                            move =lastMove;
                        }
                        return move;
                    }

                    if (bestVal < lastMove.boardScore) {
                            bestVal = lastMove.boardScore;
                            if (move == null) {
                                move = lastMove;
                            }
                            else if(depth == this.depth)
                            {
                                move = lastMove;
                            }
                            //move = lastMove;
                            move.boardScore = bestVal;
                            bestMove = move;

                    }
                        //bestVal = Math.max(bestVal,lastMove.boardScore);
                    alpha = Math.max(alpha, bestVal);

                    if (alpha >= beta) {
                            break;
                    }

                }
            }
        }
        else{
            bestVal = Double.POSITIVE_INFINITY;
            Node lastMove;
            for (int i = 0; i < allPossibleMoves.size(); i++) {
                on = allPossibleMoves.get(i).get(0);
                for (int j = 1; j < allPossibleMoves.get(i).size(); j++) {
                    //tempBoard = board.clone();
                    //tempBoard = make a move
                    n = allPossibleMoves.get(i).get(j);
                    tempBoard.config[n.y][n.x] = player;
                    tempBoard.config[on.y][on.x] = '.';

                    lastMove = minimaxABpruning(tempBoard,ctr++, n,opponent(player), depth -1, alpha, beta, !maxi);

                    tempBoard.config[n.y][n.x] = '.';
                    tempBoard.config[on.y][on.x] = player;

                    if(lastMove.boardScore == Double.POSITIVE_INFINITY)
                    {
                        if(move == null){
                            move = lastMove;
                        }
                        return move;
                    }

                    if(bestVal > lastMove.boardScore){
                        bestVal = lastMove.boardScore;
                        if(move == null){
                            move =lastMove;
                        }
                        else if(depth == this.depth)
                        {
                            move = lastMove;
                        }
                        //move = lastMove;
                        move.boardScore = bestVal;
                        bestMove = move;
                    }
                    beta = Math.min(beta,bestVal);

                    if(alpha >= beta) {
                        break;
                    }

                }
            }
        }

        return bestMove;
    }




    private char opponent(char player){
        if(player == 'B')
            return 'W';
        else
            return 'B';
    }

    // ToDo: goal can be of any player
    private boolean check_win(Board board, char player){
         List<Node> piecesList = getPlayerPiecesList(board,player);
         int counter = 0;
         for(int i=0;i<piecesList.size();i++){

             if(board.goal[piecesList.get(i).y][piecesList.get(i).x] == player){
                 counter++;
             }
         }

         if(counter == 19)
             return true;
         else
             return false;
    }

    public void printBoardTestFunc(Board board){
        for(int i=0;i<16;i++) {
            for (int j = 0; j < 16; j++) {
                System.out.print(board.config[i][j]);
            }
            System.out.println();
        }
    }

}