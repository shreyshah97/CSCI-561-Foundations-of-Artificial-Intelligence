import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.util.HashSet;

public class GameManager {
    static char[][] board = new char[19][19];
    static int blackCaptures = 0, whiteCaptures = 0;

    public static boolean isValid(int i, int j){
        if(i<0 || i>18 || j<0 || j>18)
            return false;
        return true;
    }

    public static String Mapping(int row, int col){
        String res = Integer.toString(19-row);
        if(col>7)
            col++;
        res += (char)(col+'A');
        return res;
    }

    public static int[] ReverseMapping(String s){
        int[] res = new int[2];
        res[0] = 19-Integer.parseInt(s.substring(0,s.length()-1));
        res[1] = s.charAt(s.length()-1)-'A';
        if(res[1]>8)
            res[1]--;
        return res;
    }

    public static void isCaptured(HashSet<String> whiteMoves, HashSet<String> blackMoves, int i, int j, char[][] board) {
        String player = whiteMoves.contains(i+","+j) ? "White" : "Black";
        int[][] directions = new int[][]{{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1}};
        if(player.equalsIgnoreCase("White")){
            for(int k=0;k<8;k++){
                if(isValid(i+3*directions[k][0],j+3*directions[k][1]) && whiteMoves.contains((i+3*directions[k][0])+","+(j+3*directions[k][1]))
                && blackMoves.contains((i+2*directions[k][0])+","+(j+2*directions[k][1])) && blackMoves.contains((i+directions[k][0])+","+(j+directions[k][1]))){
                    blackMoves.remove((i+2*directions[k][0])+","+(j+2*directions[k][1]));
                    blackMoves.remove((i+directions[k][0])+","+(j+directions[k][1]));
                    whiteCaptures++;
                    board[i+2*directions[k][0]][j+2*directions[k][1]] = '.';
                    board[i+directions[k][0]][j+directions[k][1]] = '.';
                }
            }
        } else {
            for(int k=0;k<8;k++){
                if(isValid(i+3*directions[k][0],j+3*directions[k][1]) && blackMoves.contains((i+3*directions[k][0])+","+(j+3*directions[k][1]))
                && whiteMoves.contains((i+2*directions[k][0])+","+(j+2*directions[k][1])) && whiteMoves.contains((i+directions[k][0])+","+(j+directions[k][1]))){
                    whiteMoves.remove((i+2*directions[k][0])+","+(j+2*directions[k][1]));
                    whiteMoves.remove((i+directions[k][0])+","+(j+directions[k][1]));
                    blackCaptures++;
                    board[i+2*directions[k][0]][j+2*directions[k][1]] = '.';
                    board[i+directions[k][0]][j+directions[k][1]] = '.';
                }
            }
        }
    }
    
    public static void main(String[] args) throws Exception{

        HashSet<String> whiteMoves = new HashSet<String>();
        HashSet<String> blackMoves = new HashSet<String>();
        Float remainingTime = 300.0f;
        //initialize board with '.'
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                board[i][j] = '.';
            }
        }
        String player = "WHITE";

        while(true){

            Pente pente = new Pente();
            FileWriter myWriter = new FileWriter("input.txt");
            myWriter.write(player+"\n"+Float.toString(remainingTime)+"\n");
            myWriter.write((whiteCaptures*2)+","+(blackCaptures*2)+"\n");
            if (player.equalsIgnoreCase("BLACK")) {
                board[9][9] = 'w';
                whiteMoves.add(9+","+9);
            }
            for(int i=0;i<19;i++){
                for(int j=0;j<19;j++){
                    myWriter.write(board[i][j]);
                }
                myWriter.write("\n");
            }
            myWriter.close();
            
            long startTime = System.currentTimeMillis();
            pente.run();
            float totalTime = (System.currentTimeMillis() - startTime)/1000.0f;
            System.out.println("Time taken: "+totalTime+" seconds");
            remainingTime -= totalTime;
            
            BufferedReader br = new BufferedReader(new FileReader("output.txt"));
            String position = br.readLine();
            int rc[] = ReverseMapping(position);
            int row = rc[0];
            int col = rc[1];
            br.close();
            
            if (player.equalsIgnoreCase("WHITE")) {
                board[row][col] = 'w';
                whiteMoves.add(row+","+col);
                System.out.println("White Move: "+position);
                System.out.println(row+" "+col);
            } else {
                board[row][col] = 'b';
                blackMoves.add(row+","+col);
                System.out.println("Black Move: "+position);
                System.out.println(row+" "+col);
            }

            isCaptured(whiteMoves, blackMoves, row, col, board);
            if(pente.isGameOver(whiteMoves, blackMoves, row, col, new int[]{whiteCaptures, blackCaptures})){
                System.out.println("Game Over, You Win!");
                break;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            // Reading data using readLine
            position = reader.readLine();
            rc = ReverseMapping(position);
            row = rc[0];
            col = rc[1];
            if (player.equalsIgnoreCase("WHITE")) {
                board[row][col] = 'b';
                blackMoves.add(row+","+col);
                System.out.println("Black Move: "+position);
                System.out.println(row+" "+col);
            } else {
                board[row][col] = 'w';
                whiteMoves.add(row+","+col);
                System.out.println("White Move: "+position);
                System.out.println(row+" "+col);
            }
            isCaptured(whiteMoves, blackMoves, row, col, board);
            if(pente.isGameOver(whiteMoves, blackMoves, row, col, new int[]{whiteCaptures, blackCaptures})){
                System.out.println("Game Over, You Lost");
                break;
            }
        }
    }
}
