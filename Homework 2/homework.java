import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

class Game{
    private String player;
    private float time;
    private int whiteCaptures;
    private int whiteTurns;
    private int blackCaptures;
    private int blackTurns;
    private char[][] board;
    
    public Game(String player, float time, int whiteCaptures, int blackCaptures, char[][] board){
        this.player = player;
        this.time = time;
        this.whiteCaptures = whiteCaptures;
        this.blackCaptures = blackCaptures;
        this.board = board;
        this.whiteTurns = 0;
        this.blackTurns = 0;
        
    }
    public String getPlayer(){
        return player;
    }
    public float getTime(){
        return time;
    }
    public int getWhiteCaptures(){
        return whiteCaptures;
    }
    public int getBlackCaptures(){
        return blackCaptures;
    }
    public char[][] getBoard(){
        return board;
    }
    public int getWhiteTurns(){
        return whiteTurns;
    }
    public int getBlackTurns(){
        return blackTurns;
    }
    public void setWhiteTurns(int whiteTurns){
        this.whiteTurns = whiteTurns;
    }
    public void setBlackTurns(int blackTurns){
        this.blackTurns = blackTurns;
    }
    public void setWhiteCaptures(int whiteCaptures){
        this.whiteCaptures = whiteCaptures;
    }
    public void setBlackCaptures(int blackCaptures){
        this.blackCaptures = blackCaptures;
    }
}

class Pente{
    Game gameInfo;
    int[] bestMove = new int[]{-1,-1};
    int count1=0;

    // Print the game information stored in the Game object.
    public void printInfo(){
        System.out.println("Player: "+gameInfo.getPlayer());
        System.out.println("Time Remaining: "+gameInfo.getTime());
        System.out.println("White Captures: "+gameInfo.getWhiteCaptures());
        System.out.println("Black Captures: "+gameInfo.getBlackCaptures());
        System.out.println("White Pieces: "+gameInfo.getWhiteTurns());
        System.out.println("Black Pieces: "+gameInfo.getBlackTurns());
        char[][] board = gameInfo.getBoard();
        for(int i = 0; i < 19; i++){
            for(int j = 0; j < 19; j++){
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    // Read the input.txt file to get the game information.
    public void readFile(String fileName) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String player = br.readLine();
        float time = Float.parseFloat(br.readLine());
        String captures = br.readLine();
        String[] arr = captures.split(",");
        int whiteCaptures = Integer.parseInt(arr[0]);
        int blackCaptures = Integer.parseInt(arr[1]);
        char[][] board = new char[19][19];
        for(int i = 0; i < 19; i++){
            String row = br.readLine();
            for(int j = 0; j < 19; j++){
                board[i][j] = row.charAt(j);
            }
        }
        gameInfo = new Game(player, time, whiteCaptures, blackCaptures, board);
        br.close();
    }

    // Get information like number of turns for black and white.
    // Also get the range of the board to check for pente.
    // Also store the map for black and white moves.
    public int[] getSecondaryInfo(HashSet<String> WhiteMoves, HashSet<String> BlackMoves, int depth){
        int whiteTurns=0, blackTurns = 0;
        int top=19, bot=0, left=19, right=0;
        char[][] board = gameInfo.getBoard();
        for(int i = 0; i < 19; i++){
            for(int j = 0; j < 19; j++){
                if(board[i][j] == 'w'){
                    whiteTurns++;
                    WhiteMoves.add(i+","+j);
                    if((i-depth)<top)
                        top = Math.min(top, Math.max(i-depth,0));
                    if((i+depth)>bot)
                        bot = Math.max(bot, Math.min(i+depth,18));
                    if((j-depth)<left)
                        left = Math.min(left, Math.max(j-depth,0));
                    if((j+depth)>right)
                        right = Math.max(right, Math.min(j+depth,18));
                } else if(board[i][j] == 'b'){
                    blackTurns++;
                    BlackMoves.add(i+","+j);
                    if((i-depth)<top)
                        top = Math.min(top, Math.max(i-depth,0));
                    if((i+depth)>bot)
                        bot = Math.max(bot, Math.min(i+depth,18));
                    if((j-depth)<left)
                        left = Math.min(left, Math.max(j-depth,0));
                    if((j+depth)>right)
                        right = Math.max(right,  Math.min(j+depth,18));
                }
            }
        }
        gameInfo.setWhiteTurns(whiteTurns);
        gameInfo.setBlackTurns(blackTurns);
        return new int[]{top, bot, left, right};
    }
    
    // Calculate the moves in the increasing order of the manhattan distance from all the pieces in the board.
    public List<List<String>> calculateRange(int[] range, HashSet<String> whiteMoves, HashSet<String> blackMoves) {
        List<List<String>> rangeMoves = new ArrayList<>();
        for (int i=range[0]; i<=range[1]; ++i) {
            for (int j=range[2]; j<=range[3]; ++j) {
                int distance = 0;
                for (String move : whiteMoves) {
                    distance += Math.abs(Integer.parseInt(move.split(",")[0])-i) + Math.abs(Integer.parseInt(move.split(",")[1])-j);
                }
                for (String move : blackMoves) {
                    distance += Math.abs(Integer.parseInt(move.split(",")[0])-i) + Math.abs(Integer.parseInt(move.split(",")[1])-j);
                }
                ArrayList<String> temp = new ArrayList<>();
                temp.add(Integer.toString(distance));
                temp.add(i+","+j);
                rangeMoves.add(temp);
            }
        }
        
        Collections.sort(rangeMoves, new Comparator<List<String>>() {
            @Override
            public int compare(List<String> a, List<String> b) {
                return Integer.parseInt(a.get(0))-(Integer.parseInt(b.get(0)));
            }
        });

        // for(int i=0; i<rangeMoves.size(); ++i) {
        //     System.out.println("Distance: " + rangeMoves.get(i).get(0) + " Position: " + rangeMoves.get(i).get(1));
        // }

        // System.exit(0);

        return rangeMoves;
    }

    // Check if the given position is valid or not (within the range of the board).
    public boolean isValid(int i, int j){
        if(i<0 || i>18 || j<0 || j>18)
            return false;
        return true;
    }

    // Calculate open four for the player passed 
    public int[] doubleOpenFour(HashSet<String> whiteMoves, HashSet<String> blackMoves, String player) {
        HashSet<String> moves, opponentMoves;
        if (player.equalsIgnoreCase("WHITE")) {
            moves = whiteMoves;
            opponentMoves = blackMoves;
            // System.out.println("White" + moves.size());
            // System.out.println("Black" + opponentMoves.size());
        } else {
            moves = blackMoves;
            opponentMoves = whiteMoves;
        }

        int doubleOpenTotal = 0, openTotal = 0;
        int[][] dir = new int[][]{{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        for (String move : moves) {
            int x = Integer.parseInt(move.split(",")[0]);
            int y = Integer.parseInt(move.split(",")[1]);
            for (int i = 0; i < 4; i++) {
                if (isValid(x + dir[i][0], y + dir[i][1]) && moves.contains((x + dir[i][0])+","+(y + dir[i][1]))
                    && isValid(x + 2*dir[i][0], y + 2*dir[i][1]) && moves.contains((x + 2*dir[i][0])+","+(y + 2*dir[i][1]))
                    && isValid(x + 3*dir[i][0], y + 3*dir[i][1]) && moves.contains((x + 3*dir[i][0])+","+(y + 3*dir[i][1]))) {

                    if(!opponentMoves.contains((x+4*dir[i][0])+","+(y+4*dir[i][1])) && !opponentMoves.contains((x-dir[i][0])+","+(y-dir[i][1]))
                        && !moves.contains((x-dir[i][0])+","+(y-dir[i][1])) && !moves.contains((x+4*dir[i][0])+","+(y+4*dir[i][1])))
                        doubleOpenTotal++;

                    else if ((!opponentMoves.contains((x+4*dir[i][0])+","+(y+4*dir[i][1])) && !moves.contains((x+4*dir[i][0])+","+(y+4*dir[i][1])))
                            || (!opponentMoves.contains((x-dir[i][0])+","+(y-dir[i][1])) && !moves.contains((x-dir[i][0])+","+(y-dir[i][1]))))
                        openTotal++;
                }
            }
        }
        return new int[]{doubleOpenTotal, openTotal};
    }

    // Calculate stretch four or open three for the player passed
    public int stretchFourOpenThree(HashSet<String> moves, HashSet<String> opponentMoves, int count1, int count2, int x, int y, int dirX, int dirY, int sumSides) {

        int sum = 0;
        // Stretch four left side
        if (isValid(x + (1 + count2)*dirX, y + (1 + count2)*dirY) 
            && !opponentMoves.contains((x + (1 + count2)*dirX)+","+(y + (1 + count2)*dirY))
            && moves.contains((x + (2 + count2)*dirX+","+(y + (2 + count2)*dirY)))) {

            if(isValid(x+(3+count2)*dirX, y+(3+count2)*dirY) && !opponentMoves.contains((x+(3+count2)*dirX)+","+(y+(3+count2)*dirY))
                && isValid(x - (1 + count1)*dirX, y - (1 + count1)*dirY) && !opponentMoves.contains((x - (1 + count1)*dirX)+","+(y - (1 + count1)*dirY)))
                sum += 8000;
            else
                sum += 5000;
        }
        // Stretch four right side 
        else if (isValid(x - (count1 + 1)*dirX, y - (1 + count1)*dirY) 
            && !opponentMoves.contains((x - (1 + count1)*dirX)+","+(y - (1 + count1)*dirY))
            && moves.contains((x - (2 + count1)*dirX)+","+(y - (2 + count1)*dirY))) {
            
            if ((isValid(x - (3 + count1)*dirX, y - (3 + count1)*dirY) 
                && !opponentMoves.contains((x - (3 + count1)*dirX)+","+(y - (3 + count1)*dirY))) ||  
                (isValid(x + (1 + count2)*dirX, y + (1 + count2)*dirY)  
                && !opponentMoves.contains((x + (count2 + 1)*dirX)+","+(y + (count2 + 1)*dirY))))
                sum += 8000;
            else {
                sum += 5000;
            }
        } 
        // Open Three
        else {
            sum += 3500 + 1000*sumSides*sumSides;
        }
        return sum;
    }

    // Calculate stretch three or open two for the player
    public int stretchThreeOpenTwo(HashSet<String> moves, HashSet<String> opponentMoves, int count1, int count2, int x, int y, int dirX, int dirY, int sumSides) {
            
        int sum = 0;
        // Stretch three right side
        if (isValid(x + (1 + count2)*dirX, y + (1 + count2)*dirY) 
            && !opponentMoves.contains((x + (1 + count2)*dirX)+","+(y + (1 + count2)*dirY))
            && moves.contains((x + (2 + count2)*dirX)+","+(y + (2 + count2)*dirY))) {
            
            // left and right should open for stretch three
            if ((isValid(x + (3 + count2)*dirX, y + (3 + count2)*dirY) 
                && !opponentMoves.contains((x + (3 + count2)*dirX)+","+(y + (3 + count2)*dirY))) ||  
                (isValid(x - (1 + count1)*dirX, y - (1 + count1)*dirY)  
                && !opponentMoves.contains((x - (count1 + 1)*dirX)+","+(y - (count1 + 1)*dirY))))
                sum += 3000 + 1000*sumSides*sumSides;
            else {
                sum += 5000;
            }
        } 

        // Stretch three left side
        else if (isValid(x - (count1 + 1)*dirX, y - (1 + count1)*dirY) 
            && !opponentMoves.contains((x - (1 + count1)*dirX)+","+(y - (1 + count1)*dirY))
            && moves.contains((x - (2 + count1)*dirX)+","+(y - (2 + count1)*dirY))) {
            
            // left and right should be open for stretch three
            if ((isValid(x - (3 + count1)*dirX, y - (3 + count1)*dirY) 
                && !opponentMoves.contains((x - (3 + count1)*dirX)+","+(y - (3 + count1)*dirY))) ||  
                (isValid(x + (1 + count2)*dirX, y + (1 + count2)*dirY)  
                && !opponentMoves.contains((x + (count2 + 1)*dirX)+","+(y + (count2 + 1)*dirY))))
                sum += 3000 + 1000*sumSides*sumSides;
            else {
                sum += 5000;
            }
        }
        // Open two 
        else {
            sum +=2000;
        }
        return sum;
    }

    // Function calculates the cost of the player to win aggressively.
    // In other words, this cost rewards open four, stretch four, stretch three etc.*/
    public int opportunityCost(HashSet<String> whiteMoves, HashSet<String> blackMoves, String player, int[] captures, String callFrom) {
        
        HashSet<String> moves, opponentMoves;
        if (player.equalsIgnoreCase("BLACK")) {
            moves = blackMoves;
            opponentMoves = whiteMoves;
        } else {
            moves = whiteMoves;
            opponentMoves = blackMoves;
        }
        
        int dir[][] = new int[][]{{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        int a, b, x, y, sum = 0;
        for (String move : moves) {
            String[] split = move.split(",");
            a = Integer.parseInt(split[0]);
            b = Integer.parseInt(split[1]);
            for (int k=0; k<4; ++k) {
                boolean right = false, left = false;
                int count1 = 0, count2 = 0;
                x = a + dir[k][0];
                y = b + dir[k][1];
                while (isValid(x, y) && moves.contains(x+","+y)) {
                    count2++;
                    x += dir[k][0];
                    y += dir[k][1];
                }
                
                if (isValid(x, y) && !opponentMoves.contains(x+","+y)) {
                    right = true;
                }

                x = a - dir[k][0];
                y = b - dir[k][1];
                while (isValid(x, y) && moves.contains(x+","+y)) {
                    count1++;
                    x -= dir[k][0];
                    y -= dir[k][1];
                }
                if (isValid(x, y) && !opponentMoves.contains(x+","+y)) {
                    left = true;
                }

                if (left || right) {

                    int sumSides = (left && right) ? 2 : 1;
                    // Single open four
                    if (count1 + count2 == 3) {
                        sum += 5000;
                    } else if (count1 + count2 == 2) {
                        sum += stretchFourOpenThree(moves, opponentMoves, count1, count2, a, b, dir[k][0], dir[k][1], sumSides);
                    } else if (count1 + count2 == 1) {
                        sum += stretchThreeOpenTwo(moves, opponentMoves, count1, count2, a, b, dir[k][0], dir[k][1], sumSides);
                    }
                } 
            }
        }
        return sum;
    } 

    // Function calculates the cost of the player to defend itself.
    // In other words, this cost rewards blocking opponent's open four, stretch four, stretch three etc.*/
    public int hedgingCost(HashSet<String> whiteMoves, HashSet<String> blackMoves, String player, int[] captures, String callFrom) {

        // check whose move is it and assign player moves and opponent moves
        HashSet<String> moves, opponentMoves;
        if (player.equalsIgnoreCase("BLACK")) {
            moves = blackMoves;
            opponentMoves = whiteMoves;
        } else {
            moves = whiteMoves;
            opponentMoves = blackMoves;
        }
        
        int dir[][] = new int[][]{{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        int sum = 0, x, y;
        
        // Capturing cost to promote our captures and demotes opponent's captures
        sum = 15000*captures[0] - 20000*captures[1];
        
        // Main blocking logic for heuristic functions
        for (String move : moves) {
            String[] split = move.split(",");
            int a = Integer.parseInt(split[0]);
            int b = Integer.parseInt(split[1]);
            for (int k=0; k<4; k++) {
                int count1 = 0, count2 = 0;
                x = a + dir[k][0];
                y = b + dir[k][1];
                while (isValid(x, y) && opponentMoves.contains(x+","+y)) {
                    count1++;
                    x += dir[k][0];
                    y += dir[k][1];
                }
                x = a - dir[k][0];
                y = b - dir[k][1];
                while (isValid(x, y) && opponentMoves.contains(x+","+y)) {
                    count2++;
                    x -= dir[k][0];
                    y -= dir[k][1];
                }
                if (count1 >= 4 || count2 >= 4) {
                    sum += 80000;
                } else if (count1 + count2 >= 4) {
                    sum += 20000;
                } else if (count1 + count2 == 3) {
                    sum += 8500;
                } else if (count1 + count2 == 2) {
                    // sum += 40
                } else if (count1 + count2 == 1) {
                    sum += 175;
                }
            }
        }
        // System.out.println("Hedging cost: "+sum);
        return sum;
    }

    // This is the main evaluation/heuristic function for the game. 
    // It will internally calculate hedging and opportunity cost */
    public int calculateUtility(String player, HashSet<String> whiteMoves, HashSet<String> blackMoves, int[] captures, String callFrom) {
        String opponent;
        if(player.equalsIgnoreCase("WHITE")){
            opponent = "BLACK";
        }
        else{
            opponent = "WHITE";
        }

        // cost functions for absolute winning moves
        int[] maxPlayer  = doubleOpenFour(whiteMoves, blackMoves, player);
        int[] minPlayer = doubleOpenFour(whiteMoves, blackMoves, opponent);

        // if it is our move and opponent has double open four
        if (callFrom == "max" && minPlayer[0] > 0) {
            // We win if we have either open four or double open four in this move, otherwise we lose in next move
            if (maxPlayer[0] == 0 && maxPlayer[1] == 0) 
                return Integer.MIN_VALUE;
            else {
                return Integer.MAX_VALUE-1;
            }
        }

        // if it is our turn, and we have either open four or double open four, we win in this move
        if (callFrom == "max" && (maxPlayer[1] > 0 || maxPlayer[0] > 0)) {
            return Integer.MAX_VALUE-1;
        }

        // if it is opponent's turn, and have either open four or double open four, opponent wins
        if (callFrom == "min" && (minPlayer[1] > 0 || minPlayer[0] > 0)) {
            return Integer.MIN_VALUE;
        }

        // cost calculations
        int cost = 0;
        if (player.equalsIgnoreCase("BLACK")) {
            cost += 7*hedgingCost(whiteMoves, blackMoves, player, captures, callFrom);
            cost += 6*opportunityCost(whiteMoves, blackMoves, player, captures, callFrom);
        } else {
            cost += 6*hedgingCost(whiteMoves, blackMoves, player, captures, callFrom);
        }
        return cost;
    }
    
    public int calculateDepth(HashSet<String> whiteMoves, HashSet<String> blackMoves, String player){
        String opponent = "WHITE";
            if (player.equalsIgnoreCase("WHITE")) {
                opponent = "BLACK";
            } 
            int[] maxPlayer  = doubleOpenFour(whiteMoves, blackMoves, player);
            int[] minPlayer = doubleOpenFour(whiteMoves, blackMoves, opponent);
            if (maxPlayer[0] > 0 || maxPlayer[1] > 0) {
                return 1;
            }
            if (minPlayer[0] > 0 || minPlayer[1] > 0) {
                return 2;
            }
            return 3;
    }

    // Create and return a copy of the set.
    public HashSet<String> copySet(HashSet<String> set){
        HashSet<String> temp = new HashSet<>();
        for (String s : set)
            temp.add(s);
        return temp;
    }

    // Check if the game is over
    public boolean isGameOver(HashSet<String> whiteMoves, HashSet<String> blackMoves, int i, int j, int[] captures){
        if(captures[0] == 5 || captures[1] == 5)
            return true;
        HashSet<String> moves = new HashSet<>();
        if(whiteMoves.contains(i+","+j))
            moves = whiteMoves;
        else
            moves = blackMoves;
        int[][] directions = new int[][]{{-1,0},{-1,1},{0,1},{1,1}};
        for(int k=0;k<4;k++){
            int count = 1;
            int x = i;
            int y = j;
            while(isValid(x+directions[k][0], y+directions[k][1]) && moves.contains((x+directions[k][0])+","+(y+directions[k][1]))){
                count++;
                x = x+directions[k][0];
                y = y+directions[k][1];
            }
            x = i;
            y = j;
            while(isValid(x-directions[k][0], y-directions[k][1]) && moves.contains((x-directions[k][0])+","+(y-directions[k][1]))){
                count++;
                x = x-directions[k][0];
                y = y-directions[k][1];
            }
            if(count>=5)
                return true;
        }
        return false;
    }
    
    // Calculate the captures for the current move.
    public int[] calculteNextStage(HashSet<String> whiteMoves, HashSet<String> blackMoves, int i, int j, int[] captures){
        String player = whiteMoves.contains(i+","+j) ? "White" : "Black";
        int[][] directions = new int[][]{{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1}};
        int whiteCaptures = captures[0];
        int blackCaptures = captures[1];
        if(player.equalsIgnoreCase("White")){
            for(int k=0;k<8;k++){
                if(isValid(i+3*directions[k][0],j+3*directions[k][1]) && whiteMoves.contains((i+3*directions[k][0])+","+(j+3*directions[k][1]))
                && blackMoves.contains((i+2*directions[k][0])+","+(j+2*directions[k][1])) && blackMoves.contains((i+directions[k][0])+","+(j+directions[k][1]))){
                    blackMoves.remove((i+2*directions[k][0])+","+(j+2*directions[k][1]));
                    blackMoves.remove((i+directions[k][0])+","+(j+directions[k][1]));
                    whiteCaptures++;
                }
            }
        } else {
            for(int k=0;k<8;k++){
                if(isValid(i+3*directions[k][0],j+3*directions[k][1]) && blackMoves.contains((i+3*directions[k][0])+","+(j+3*directions[k][1]))
                && whiteMoves.contains((i+2*directions[k][0])+","+(j+2*directions[k][1])) && whiteMoves.contains((i+directions[k][0])+","+(j+directions[k][1]))){
                    whiteMoves.remove((i+2*directions[k][0])+","+(j+2*directions[k][1]));
                    whiteMoves.remove((i+directions[k][0])+","+(j+directions[k][1]));
                    blackCaptures++;
                }
            }
        }
        return new int[]{whiteCaptures, blackCaptures};
    }
    
    // Alpha beta pruning max function
    public int alphaBetaMax(int maxDepth, int currentDepth, int alpha, int beta, List<List<String>> range, String player, HashSet<String> WhiteMovesPrev, HashSet<String> BlackMovesPrev, int[] captures){
        if (currentDepth == maxDepth){
            return calculateUtility(player, WhiteMovesPrev, BlackMovesPrev, captures, "max");
        }
            
        int maxUtility = Integer.MIN_VALUE;
        int best = Integer.MIN_VALUE;
        boolean isAssigned = false;
        for (int k = 0; k < range.size(); k++) {
            List<String> rangeList = range.get(k);
            String[] ij = rangeList.get(1).split(",");
            int i = Integer.parseInt(ij[0]);
            int j = Integer.parseInt(ij[1]);
            if (isValid(i, j) && !(WhiteMovesPrev.contains(i+","+j) || BlackMovesPrev.contains(i+","+j))){
                HashSet<String> whiteCopySet = copySet(WhiteMovesPrev);
                HashSet<String> blackCopySet = copySet(BlackMovesPrev);
                if(player.equalsIgnoreCase("White"))
                    whiteCopySet.add(i+","+j);
                else
                    blackCopySet.add(i+","+j);
                
                int[] new_captures = calculteNextStage(whiteCopySet, blackCopySet, i, j, captures);

                if(isGameOver(whiteCopySet, blackCopySet, i, j, new_captures)){
                    if(currentDepth == 0){
                        bestMove[0] = i;
                        bestMove[1] = j;
                        System.out.println(bestMove[0]+","+bestMove[1]);
                        System.out.println("White Moves: ");
                        for(String s: whiteCopySet){
                            System.out.print(s+" ");
                        }
                        System.out.println();
                        System.out.println("Black Moves: ");
                        for(String s: blackCopySet){
                            System.out.print(s+" ");
                        }
                        System.out.println();
                    }
                    return Integer.MAX_VALUE;
                }
                if(i==8 && j==9 && currentDepth==0){
                    System.out.println("count1 in max: "+count1);
                }
                best = Math.max(best, alphaBetaMin(maxDepth, currentDepth+1, alpha, beta, range, player, whiteCopySet, blackCopySet, new_captures));

                if(currentDepth==0){
                    if(!isAssigned || best>maxUtility){
                        isAssigned = true;
                        maxUtility = best;
                        bestMove[0] = i;
                        bestMove[1] = j;
                        System.out.println(bestMove[0]+","+bestMove[1]+" "+maxUtility);
                        System.out.println("White Moves: ");
                        for(String s: whiteCopySet){
                            System.out.print(s+" ");
                        }
                        System.out.println();
                        System.out.println("Black Moves: ");
                        for(String s: blackCopySet){
                            System.out.print(s+" ");
                        }
                        System.out.println();
                        // System.out.println(Mapping(bestMove[0], bestMove[1]));
                    }
                }
                if(player.equalsIgnoreCase("White"))
                    whiteCopySet.remove(i+","+j);
                else
                    blackCopySet.remove(i+","+j);
                alpha = Math.max(alpha, best);
                if (beta <= alpha){
                    return best;
                }
            }
        }
        return best;
    }

    // Alpha beta pruning min function
    public int alphaBetaMin(int maxDepth, int currentDepth, int alpha, int beta, List<List<String>> range, String player, HashSet<String> WhiteMovesPrev, HashSet<String> BlackMovesPrev, int[] captures){
        if (currentDepth == maxDepth) {
            return calculateUtility(player, WhiteMovesPrev, BlackMovesPrev, captures, "min");
        }
            
        int best = Integer.MAX_VALUE;
        
        for (int k = 0; k < range.size(); k++) {
            List<String> rangeList = range.get(k);
            String[] ij = rangeList.get(1).split(",");
            int i = Integer.parseInt(ij[0]);
            int j = Integer.parseInt(ij[1]);
            if (isValid(i, j) && !(WhiteMovesPrev.contains(i+","+j) || BlackMovesPrev.contains(i+","+j))){
                HashSet<String> whiteCopySet = copySet(WhiteMovesPrev);
                HashSet<String> blackCopySet = copySet(BlackMovesPrev);
                if(player.equalsIgnoreCase("White"))
                    blackCopySet.add(i+","+j);
                else
                    whiteCopySet.add(i+","+j);
                
                int[] new_captures = calculteNextStage(whiteCopySet, blackCopySet, i, j, captures);
                
                if(isGameOver(whiteCopySet, blackCopySet, i, j, new_captures)){
                    return Integer.MIN_VALUE;
                }
                int tt = alphaBetaMax(maxDepth, currentDepth+1, alpha, beta, range, player, whiteCopySet, blackCopySet, new_captures);
                best = Math.min(best, tt);
                if(player.equalsIgnoreCase("White"))
                    blackCopySet.remove(i+","+j);
                else
                    whiteCopySet.remove(i+","+j);
                
                beta = Math.min(beta, best);
                if (beta <= alpha){
                    return best;
                }
            }
        }
        return best;
    }

    // Alpha beta function
    public int alphaBeta(String player){
        HashSet<String> BlackMoves = new HashSet<String>();
        HashSet<String> WhiteMoves = new HashSet<String>();
        int depth = calculateDepth(WhiteMoves, BlackMoves, player);
        int[] range = getSecondaryInfo(WhiteMoves, BlackMoves, depth);
        List<List<String>> RangeMoves = calculateRange(range, WhiteMoves, BlackMoves);
        
        if(gameInfo.getWhiteTurns()==0){
            bestMove[0] = 9;
            bestMove[1] = 9;
            return 0;
        }
        if (gameInfo.getWhiteTurns()==1 && gameInfo.getBlackTurns()==1) {
            selectThirdMove(BlackMoves);
            return 0;
        }
        depth = calculateDepth(WhiteMoves, BlackMoves, player);
        System.out.println("Depth: "+depth);
        return alphaBetaMax(depth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, RangeMoves, player, WhiteMoves, BlackMoves, new int[]{gameInfo.getWhiteCaptures(), gameInfo.getBlackCaptures()});
    }

    // Select second move for white, should be outside the inner box
    public void selectThirdMove(HashSet<String> blackMoves){
        String xy = blackMoves.iterator().next();
        int x = Integer.parseInt(xy.split(",")[0]);
        int y = Integer.parseInt(xy.split(",")[1]);
        int man1 = Math.abs(x - 6) + Math.abs(y - 6);
        int man2 = Math.abs(x - 12) + Math.abs(y - 6);
        int man3 = Math.abs(x - 6) + Math.abs(y - 12);
        int man4 = Math.abs(x - 12) + Math.abs(y - 12);
        int maximum = Math.max(man1, Math.max(man2, Math.max(man3, man4)));
        if (maximum == man1) {
            bestMove[0] = 6;
            bestMove[1] = 6;
        } else if (maximum == man2) {
            bestMove[0] = 12;
            bestMove[1] = 6;
        } else if (maximum == man3) {
            bestMove[0] = 6;
            bestMove[1] = 12;
        } else {
            bestMove[0] = 12;
            bestMove[1] = 12;
        }
    }

    // Covnert the board indices to an alphanumeric string
    public static String Mapping(int row, int col){
        String res = Integer.toString(19-row);
        if(col>7)
            col++;
        res += (char)(col+'A');
        return res;
    }

    // Convert the alphanumeric string to board indices
    public static int[] ReverseMapping(String s){
        int[] res = new int[2];
        res[0] = 19-Integer.parseInt(s.substring(0,s.length()-1));
        res[1] = s.charAt(s.length()-1)-'A';
        if(res[1]>8)
            res[1]--;
        return res;
    }

    public void run() throws Exception{
        readFile("input.txt");
        printInfo();
        alphaBeta(gameInfo.getPlayer());
        writeFile("output.txt");
    }

    // Write to file the best move found
    public void writeFile(String filename) throws Exception{
        FileWriter fw = new FileWriter(filename);
        fw.write(Mapping(bestMove[0], bestMove[1]));
        fw.close();
    }

    // Main function to read input and write output after calling our algorithm
    public static void main(String[] args) throws Exception{
        Pente p = new Pente();
        p.readFile("input.txt");
        p.alphaBeta(p.gameInfo.getPlayer());
        p.writeFile("output.txt");
    }
}