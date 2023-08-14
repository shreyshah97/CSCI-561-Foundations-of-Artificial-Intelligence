import java.io.FileWriter;
import java.util.*;

class GameInfo{
    private String player;
    private float time;
    private int whiteCaptures;
    private int whiteTurns;
    private int blackCaptures;
    private int blackTurns;
    private char[][] board;
    public GameInfo(String player, float time, int whiteCaptures, int blackCaptures, char[][] board){
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

class calibrate{
    GameInfo gameInfo;
    int[] bestMove = new int[]{-1,-1};
    
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
        return rangeMoves;
    }

    // Check if the given position is valid or not (within the range of the board).
    public boolean isValid(int i, int j){
        if(i<0 || i>18 || j<0 || j>18)
            return false;
        return true;
    }

    // Calculate open fours for the player passed 
    public int[] countFours(HashSet<String> whiteMoves, HashSet<String> blackMoves, String player) {
        HashSet<String> playerMoves, opponentMoves;
        if (player.equalsIgnoreCase("WHITE")) {
            playerMoves = whiteMoves;
            opponentMoves = blackMoves;
        } else {
            playerMoves = blackMoves;
            opponentMoves = whiteMoves;
        }

        int doubleOpenFour = 0, singleOpenFour = 0, threes = 0;
        int[][] dir = new int[][]{{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        for (String move : playerMoves) {
            int x = Integer.parseInt(move.split(",")[0]);
            int y = Integer.parseInt(move.split(",")[1]);
            for (int i = 0; i < 4; i++) {
                if (isValid(x + dir[i][0], y + dir[i][1]) && playerMoves.contains((x + dir[i][0])+","+(y + dir[i][1]))){
                    if(isValid(x + 2*dir[i][0], y + 2*dir[i][1]) && playerMoves.contains((x + 2*dir[i][0])+","+(y + 2*dir[i][1]))){
                        if(isValid(x + 3*dir[i][0], y + 3*dir[i][1]) && playerMoves.contains((x + 3*dir[i][0])+","+(y + 3*dir[i][1]))) {
                            if(isValid(x+4*dir[i][0],y+4*dir[i][0]) && !opponentMoves.contains((x+4*dir[i][0])+","+(y+4*dir[i][1])) && !opponentMoves.contains((x-dir[i][0])+","+(y-dir[i][1]))
                                && isValid(x - dir[i][0],y - dir[i][0]) && !playerMoves.contains((x-dir[i][0])+","+(y-dir[i][1])) && !playerMoves.contains((x+4*dir[i][0])+","+(y+4*dir[i][1])))
                                doubleOpenFour++;

                            else if ((isValid(x + 4*dir[i][0],y + 4*dir[i][0]) && !opponentMoves.contains((x + 4*dir[i][0])+","+(y + 4*dir[i][1])) && !playerMoves.contains((x + 4*dir[i][0])+","+(y+4*dir[i][1])))
                                    || (isValid(x - dir[i][0],y - dir[i][0]) && !opponentMoves.contains((x - dir[i][0])+","+(y - dir[i][1])) && !playerMoves.contains((x - dir[i][0])+","+(y-dir[i][1]))))
                                singleOpenFour++;
                        } else {
                            if(isValid(x + 3*dir[i][0], y + 3*dir[i][1]) && !playerMoves.contains((x + 3*dir[i][0])+","+(y + 3*dir[i][1])) && !opponentMoves.contains((x + 3*dir[i][0])+","+(y + 3*dir[i][1]))
                            && isValid(x - dir[i][0], y - dir[i][1]) && !playerMoves.contains((x - dir[i][0])+","+(y - dir[i][1])) && !opponentMoves.contains((x - dir[i][0])+","+(y - dir[i][1])))
                                if(isValid(x + 4*dir[i][0], y + 4*dir[i][1]) && !playerMoves.contains((x + 4*dir[i][0])+","+(y + 4*dir[i][1])) && !opponentMoves.contains((x + 4*dir[i][0])+","+(y + 4*dir[i][1]))
                                || isValid(x - dir[i][0], y - dir[i][1]) && !playerMoves.contains((x - dir[i][0])+","+(y - dir[i][1])) && !opponentMoves.contains((x - dir[i][0])+","+(y - dir[i][1])))
                                    threes++;
                        }
                    } else {
                        if(isValid(x + 2*dir[i][0], y + 2*dir[i][1]) && !playerMoves.contains((x + 2*dir[i][0])+","+(y + 2*dir[i][1])) && !opponentMoves.contains((x + 2*dir[i][0])+","+(y + 2*dir[i][1]))
                            && isValid(x - dir[i][0], y - dir[i][1]) && !playerMoves.contains((x - dir[i][0])+","+(y - dir[i][1])) && !opponentMoves.contains((x - dir[i][0])+","+(y - dir[i][1])))
                            
                            if(isValid(x + 3*dir[i][0], y + 3*dir[i][1]) && playerMoves.contains((x + 3*dir[i][0])+","+(y + 3*dir[i][1])) && isValid(x + 4*dir[i][0], y + 4*dir[i][1]) && !playerMoves.contains((x + 4*dir[i][0])+","+(y + 4*dir[i][1])) && !opponentMoves.contains((x + 4*dir[i][0])+","+(y + 4*dir[i][1]))
                                || isValid(x - 2*dir[i][0], y - 2*dir[i][1]) && playerMoves.contains((x - 2*dir[i][0])+","+(y - 2*dir[i][1])) && isValid(x - 3*dir[i][0], y - 3*dir[i][1]) && !playerMoves.contains((x - 3*dir[i][0])+","+(y - 3*dir[i][1])) && !opponentMoves.contains((x - 3*dir[i][0])+","+(y - 3*dir[i][1])))
                                threes++;
                    }
                }
            }
        }
        return new int[]{doubleOpenFour, singleOpenFour, threes};
    }

    public boolean fourInFive(HashSet<String> whiteMoves, HashSet<String> blackMoves, String player) {
        HashSet<String> playerMoves, opponentMoves;
        if (player.equalsIgnoreCase("WHITE")) {
            playerMoves = whiteMoves;
            opponentMoves = blackMoves;
        } else {
            playerMoves = blackMoves;
            opponentMoves = whiteMoves;
        }

        int[][] dir = new int[][]{{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        for (String move: playerMoves) {
            int a = Integer.parseInt(move.split(",")[0]);
            int b = Integer.parseInt(move.split(",")[1]);
            for (int i = 0; i < 4; i++) {
                int left = 0, right = 0;
                int x = a + dir[i][0];
                int y = b + dir[i][1];
                while (isValid(x, y) && playerMoves.contains(x+","+y)) {
                    right++;
                    x += dir[i][0];
                    y += dir[i][1];
                }

                x = a - dir[i][0];
                y = b - dir[i][1];

                while (isValid(x, y) && playerMoves.contains(x+","+y)) {
                    left++;
                    x -= dir[i][0];
                    y -= dir[i][1];
                }

                int remainingMoves = 5 - (left+right+1);
                if(remainingMoves > 4) {
                    return true;
                }
                
                int space = 0, j = 0;
                // Check left
                while( j < remainingMoves && isValid(a - (left+1+j)*dir[i][0], b - (left+1+j)*dir[i][1]) && !opponentMoves.contains((a - (left+j+1)*dir[i][0])+","+(b - (left+1+j)*dir[i][1])))
                {
                    if(!playerMoves.contains((a - (left+1+j)*dir[i][0])+","+(b - (left+1+j)*dir[i][1]))) {
                        space++;
                        if(space == 2) {
                            break;
                        }
                    }
                    j++;
                }
                if(space < 2 && j == remainingMoves) {
                    return true;
                }

                space = 0; j = 0;
                // Check right
                while( j < remainingMoves && isValid( a + (right+1+j)*dir[i][0], b + (right+1+j)*dir[i][1]) && !opponentMoves.contains((a + (right+j+1)*dir[i][0])+","+(b + (right+1+j)*dir[i][1])))
                {
                    if(!playerMoves.contains((a + (right+1+j)*dir[i][0])+","+(b + (right+1+j)*dir[i][1]))) {
                        space++;
                        if(space == 2) {
                            break;
                        }
                    }
                    j++;
                }
                if(space < 2 && j == remainingMoves) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Calculate stretch four or open three for the player passed
    public int stretchFourOpenThree(HashSet<String> playerMoves, HashSet<String> opponentMoves, int countLeft, int countRight, int x, int y, int dirX, int dirY, int sumSides) {

        int sum = 0;
        // Stretch four left side
        if (isValid(x + (1 + countRight)*dirX, y + (1 + countRight)*dirY) 
            && !opponentMoves.contains((x + (1 + countRight)*dirX)+","+(y + (1 + countRight)*dirY))
            && playerMoves.contains((x + (2 + countRight)*dirX+","+(y + (2 + countRight)*dirY)))) {

            if(isValid(x + (3 + countRight)*dirX, y + (3 + countRight) * dirY) && !opponentMoves.contains((x + (3 + countRight) * dirX)+","+(y + (3 + countRight) * dirY))
                && isValid(x - (1 + countLeft)*dirX, y - (1 + countLeft) * dirY) && !opponentMoves.contains((x - (1 + countLeft) * dirX)+","+(y - (1 + countLeft) * dirY)))
                sum += 120;
            else if(isValid(x + (3 + countRight) * dirX, y + (3 + countRight) * dirY) && !opponentMoves.contains((x + (3 + countRight) * dirX)+","+(y + (3 + countRight)*dirY))
                || isValid(x - (1 + countLeft)*dirX, y - (1 + countLeft)*dirY) && !opponentMoves.contains((x - (1 + countLeft)*dirX)+","+(y - (1 + countLeft)*dirY)))
                sum += 75;
            else
                sum += 30;
        }
        // Stretch four right side 
        else if (isValid(x - (countLeft + 1)*dirX, y - (1 + countLeft)*dirY) 
            && !opponentMoves.contains((x - (1 + countLeft)*dirX)+","+(y - (1 + countLeft)*dirY))
            && playerMoves.contains((x - (2 + countLeft)*dirX)+","+(y - (2 + countLeft)*dirY))) {
            
            if ((isValid(x - (3 + countLeft)*dirX, y - (3 + countLeft)*dirY) && !opponentMoves.contains((x - (3 + countLeft)*dirX)+","+(y - (3 + countLeft)*dirY)))
                && (isValid(x + (1 + countRight)*dirX, y + (1 + countRight)*dirY) && !opponentMoves.contains((x + (countRight + 1)*dirX)+","+(y + (countRight + 1)*dirY))))
                sum += 120;
            else if((isValid(x - (3 + countLeft)*dirX, y - (3 + countLeft)*dirY) && !opponentMoves.contains((x - (3 + countLeft)*dirX)+","+(y - (3 + countLeft)*dirY)))
                || (isValid(x + (1 + countRight)*dirX, y + (1 + countRight)*dirY) && !opponentMoves.contains((x + (countRight + 1)*dirX)+","+(y + (countRight + 1)*dirY))))
                sum += 75;
            else
                sum += 30;
        } 
        // Open Three
        else {
            sum += 15 + 15*sumSides*sumSides;
        }
        return sum;
    }

    // Calculate stretch three or open two for the player
    public int stretchThreeOpenTwo(HashSet<String> playerMoves, HashSet<String> opponentMoves, int countLeft, int countRight, int x, int y, int dirX, int dirY, int sumSides) {
            
        int sum = 0;
        // Stretch three right side
        if (isValid(x + (1 + countRight)*dirX, y + (1 + countRight)*dirY) 
            && !opponentMoves.contains((x + (1 + countRight)*dirX)+","+(y + (1 + countRight)*dirY))
            && playerMoves.contains((x + (2 + countRight)*dirX)+","+(y + (2 + countRight)*dirY))) {
            
            // left and right should open for stretch three
            if ((isValid(x + (3 + countRight)*dirX, y + (3 + countRight)*dirY) && !opponentMoves.contains((x + (3 + countRight)*dirX)+","+(y + (3 + countRight)*dirY)))
                && (isValid(x - (1 + countLeft)*dirX, y - (1 + countLeft)*dirY) && !opponentMoves.contains((x - (countLeft + 1)*dirX)+","+(y - (countLeft + 1)*dirY))))
                sum +=  75;
            else if ((isValid(x + (3 + countRight)*dirX, y + (3 + countRight)*dirY) && !opponentMoves.contains((x + (3 + countRight)*dirX)+","+(y + (3 + countRight)*dirY)))
                || (isValid(x - (1 + countLeft)*dirX, y - (1 + countLeft)*dirY) && !opponentMoves.contains((x - (countLeft + 1)*dirX)+","+(y - (countLeft + 1)*dirY))))
                sum += 0;
            else {
                sum += 0;
            }
        } 

        // Stretch three left side
        else if (isValid(x - (countLeft + 1)*dirX, y - (1 + countLeft)*dirY) 
            && !opponentMoves.contains((x - (1 + countLeft)*dirX)+","+(y - (1 + countLeft)*dirY))
            && playerMoves.contains((x - (2 + countLeft)*dirX)+","+(y - (2 + countLeft)*dirY))) {
            
            // left and right should be open for stretch three
            if ((isValid(x - (3 + countLeft)*dirX, y - (3 + countLeft)*dirY) && !opponentMoves.contains((x - (3 + countLeft)*dirX)+","+(y - (3 + countLeft)*dirY))) 
                && (isValid(x + (1 + countRight)*dirX, y + (1 + countRight)*dirY) && !opponentMoves.contains((x + (countRight + 1)*dirX)+","+(y + (countRight + 1)*dirY))))
                sum += 75;
            else if ((isValid(x - (3 + countLeft)*dirX, y - (3 + countLeft)*dirY) && !opponentMoves.contains((x - (3 + countLeft)*dirX)+","+(y - (3 + countLeft)*dirY))) 
                || (isValid(x + (1 + countRight)*dirX, y + (1 + countRight)*dirY) && !opponentMoves.contains((x + (countRight + 1)*dirX)+","+(y + (countRight + 1)*dirY))))
                sum += 0;
            else 
                sum += 0;
        }
        // Open two 
        else {
            // should be technically 3.75
            sum += 3*sumSides*sumSides;
        }
        return sum;
    }

    // Function calculates the cost of the player to win aggressively.
    // In other words, this cost rewards open four, stretch four, stretch three etc.*/
    public int offensiveCost(HashSet<String> whiteMoves, HashSet<String> blackMoves, String player, int[] captures, String callFrom) {
        
        HashSet<String> playerMoves, opponentMoves;
        if (player.equalsIgnoreCase("BLACK")) {
            playerMoves = blackMoves;
            opponentMoves = whiteMoves;
        } else {
            playerMoves = whiteMoves;
            opponentMoves = blackMoves;
        }
        
        int dir[][] = new int[][]{{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        int a, b, x, y, sum = 0;
        for (String move : playerMoves) {
            String[] split = move.split(",");
            a = Integer.parseInt(split[0]);
            b = Integer.parseInt(split[1]);
            for (int k=0; k<4; ++k) {
                boolean right = false, left = false;
                int countLeft = 0, countRight = 0;
                x = a + dir[k][0];
                y = b + dir[k][1];
                while (isValid(x, y) && playerMoves.contains(x+","+y)) {
                    countRight++;
                    x += dir[k][0];
                    y += dir[k][1];
                }
                if (isValid(x, y) && !opponentMoves.contains(x+","+y)) {
                    right = true;
                }

                x = a - dir[k][0];
                y = b - dir[k][1];
                while (isValid(x, y) && playerMoves.contains(x+","+y)) {
                    countLeft++;
                    x -= dir[k][0];
                    y -= dir[k][1];
                }
                if (isValid(x, y) && !opponentMoves.contains(x+","+y)) {
                    left = true;
                }

                if (left || right) {

                    int sumSides = (left && right) ? 2 : 1;
                    // Single open four
                    if (countLeft + countRight == 3) {
                        sum += 75;
                    } else if (countLeft + countRight == 2) {
                        sum += stretchFourOpenThree(playerMoves, opponentMoves, countLeft, countRight, a, b, dir[k][0], dir[k][1], sumSides);
                    } else if (countLeft + countRight == 1) {
                        sum += stretchThreeOpenTwo(playerMoves, opponentMoves, countLeft, countRight, a, b, dir[k][0], dir[k][1], sumSides);
                    }
                } 
            }
        }
        return sum;
    } 

    // Function calculates the cost of the player to defend itself.
    // In other words, this cost rewards blocking opponent's open four, stretch four, stretch three etc.*/
    public int defensiveCost(HashSet<String> whiteMoves, HashSet<String> blackMoves, String player, int[] captures, String callFrom) {
        
        // check whose move is it and assign player moves and opponent moves
        HashSet<String> playerMoves, opponentMoves;
        if (player.equalsIgnoreCase("BLACK")) {
            playerMoves = blackMoves;
            opponentMoves = whiteMoves;
        } else {
            playerMoves = whiteMoves;
            opponentMoves = blackMoves;
        }
        
        int dir[][] = new int[][]{{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        int sum = 0, x, y;
        
        // Capturing cost to promote our captures and demotes opponent's captures
        sum = 150*(int)Math.pow(captures[0], 1.5) - 210*(int)Math.pow(captures[1], 1.5);
        
        // Main blocking logic for heuristic functions
        for (String move : playerMoves) {
            String[] split = move.split(",");
            int a = Integer.parseInt(split[0]);
            int b = Integer.parseInt(split[1]);
            for (int k=0; k<4; k++) {
                boolean leftOpen = false, rightOpen = false;
                int countleft = 0, countRight = 0;
                x = a + dir[k][0];
                y = b + dir[k][1];
                while (isValid(x, y) && opponentMoves.contains(x+","+y)) {
                    countleft++;
                    x += dir[k][0];
                    y += dir[k][1];
                }
                if (countleft > 0 && isValid(x, y) && !playerMoves.contains(x+","+y)) {
                    leftOpen = true;
                }

                x = a - dir[k][0];
                y = b - dir[k][1];
                while (isValid(x, y) && opponentMoves.contains(x+","+y)) {
                    countRight++;
                    x -= dir[k][0];
                    y -= dir[k][1];
                }
                if (countRight > 0 && isValid(x, y) && !playerMoves.contains(x+","+y)) {
                    rightOpen = true;
                }
                if(leftOpen || rightOpen) {
                    if (countleft >= 4 || countRight >= 4)
                        sum += 480;
                    else if (countleft + countRight >= 4)
                        sum += 360;
                    else if (countleft + countRight == 3)
                        sum += 240;
                    else if (countleft + countRight == 2)
                        sum += 60;
                    else if (countleft + countRight == 1)
                        sum += 0;
                }
            }
        }
        return sum;
    }

    // This is the main evaluation/heuristic function for the game. 
    // It will internally calculate hedging and opportunity cost */
    public int calculateUtility(String player, HashSet<String> whiteMoves, HashSet<String> blackMoves, int[] captures, String callFrom) {
        
        String opponent;
        if (player.equalsIgnoreCase("WHITE")) {
            opponent = "BLACK";
        } else {
            opponent = "WHITE";
            int t = captures[0];
            captures[0] = captures[1];
            captures[1] = t;
        }

        // Cost functions for absolute winning moves
        int[] maxPlayer  = countFours(whiteMoves, blackMoves, player);
        int[] minPlayer = countFours(whiteMoves, blackMoves, opponent);
        
        // Opponent's turn and we have double open four
        if (callFrom == "min" && maxPlayer[0] > 0) {
            // We win if opponent has neither open four nor double open four in next move, otherwise we lose in this move
            if (minPlayer[0] == 0 && minPlayer[1] == 0)
                return Integer.MAX_VALUE-1;
            else
                return Integer.MIN_VALUE;
        }

        // Our turn and opponent has double open four
        if (callFrom == "max" && minPlayer[0] > 0) {
            // We win if we have either open four or double open four in this move, otherwise we lose in next move
            if (maxPlayer[0] == 0 && maxPlayer[1] == 0) 
                return Integer.MIN_VALUE;
            else
                return Integer.MAX_VALUE-1;
        }

        boolean check_max = fourInFive(whiteMoves, blackMoves, player);
        boolean check_min = fourInFive(whiteMoves, blackMoves, opponent);
        
        // Our turn, and we have either open four, double open four or four in a row, we win
        if (callFrom == "max" && (maxPlayer[1] > 0 || maxPlayer[0] > 0 || check_max))
            return Integer.MAX_VALUE - 1;

        // Opponent's turn, and have either open four, double open four or four in a row, opponent wins
        if (callFrom == "min" && (minPlayer[1] > 0 || minPlayer[0] > 0 || check_min))
            return Integer.MIN_VALUE;

        // Our turn, and we have three in a row, we win
        if (callFrom == "max" && maxPlayer[2] > 0)
            return Integer.MAX_VALUE - 2;
        
        // Opponent's turn, and have three in a row, opponent wins
        if (callFrom == "min" && minPlayer[2] > 0)
            return Integer.MIN_VALUE;

        // Cost calculations
        if (player.equalsIgnoreCase("BLACK"))
            return 2*defensiveCost(whiteMoves, blackMoves, player, captures, callFrom) + offensiveCost(whiteMoves, blackMoves, player, captures, callFrom);
        else
            return defensiveCost(whiteMoves, blackMoves, player, captures, callFrom) + offensiveCost(whiteMoves, blackMoves, player, captures, callFrom);
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
        if (currentDepth == maxDepth)
            return calculateUtility(player, WhiteMovesPrev, BlackMovesPrev, captures, "max");
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
                    }
                    return Integer.MAX_VALUE;
                }
                best = Math.max(best, alphaBetaMin(maxDepth, currentDepth+1, alpha, beta, range, player, whiteCopySet, blackCopySet, new_captures));

                if(currentDepth==0){
                    if(!isAssigned || best>maxUtility){
                        isAssigned = true;
                        maxUtility = best;
                        bestMove[0] = i;
                        bestMove[1] = j;
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
        if (currentDepth == maxDepth)
            return calculateUtility(player, WhiteMovesPrev, BlackMovesPrev, captures, "min");
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
                
                best = Math.min(best, alphaBetaMax(maxDepth, currentDepth+1, alpha, beta, range, player, whiteCopySet, blackCopySet, new_captures));
                
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
    public int alphaBeta() throws Exception{
        HashSet<String> BlackMoves = new HashSet<String>();
        HashSet<String> WhiteMoves = new HashSet<String>();
        char[][] board = new char[][]{
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', 'b', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', 'b', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', 'w', '.', '.', 'w', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', 'w', '.', '.', 'w', '.', '.', '.', 'b', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', 'w', '.', 'b', 'w', '.', 'w', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', 'b', 'w', 'b', '.', 'b', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', 'b', 'b', '.', 'b', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', 'w', '.', 'b', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', 'b', 'w', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', 'w', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'}
        };
        String player = "WHITE";
        int range[] = new int[]{0,18,0,18};
        int depth = 2; 
        gameInfo = new GameInfo(player, 300, 0, 0, board);
        for(int i=0; i<19; i++){
            for(int j=0; j<19; j++){
                if(gameInfo.getBoard()[i][j] == 'w'){
                    WhiteMoves.add(i+","+j);
                }
                else if(gameInfo.getBoard()[i][j] == 'b'){
                    BlackMoves.add(i+","+j);
                }
            }
        }

        List<List<String>> RangeMoves = calculateRange(range, WhiteMoves, BlackMoves);
        int ans = alphaBetaMax(depth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, RangeMoves, player, WhiteMoves, BlackMoves, new int[]{0,0});
        return ans;
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

    public void writeCalibrateData(String filename, float totalTime) throws Exception{
        FileWriter fw = new FileWriter(filename);
        fw.write(Float.toString(totalTime));
        fw.close();
    }

    // Main function to read input and write output after calling our algorithm
    public static void main(String[] args) throws Exception{
        calibrate c = new calibrate();
        long startTime = System.currentTimeMillis();
        c.alphaBeta();
        float totalTime = (System.currentTimeMillis() - startTime)/1000.0f;
        c.writeCalibrateData("calibrate.txt", totalTime);
    }
}