import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class AI_Main_v1 {
    private final int dim = 8;
    private final int maxDepth = 5;
    private final int posInf = 7654321;
    private final int negInf = -this.posInf;
    private final long timeConstant = 1000000000;
    private final long timeLimit;
    private final int opponentID;
    private final char myColor;
    private final char opColor;

    private final boolean [][] vis = new boolean[this.dim][this.dim];

    private int myPieceCount = 2 * (this.dim - 2);
    private int opPieceCount = 2 * (this.dim - 2);

    private long startTime;

    private int bfx, bfy, btx, bty, comparisonCount;

    private final char[][] board;
    private final  int[][] directions;
    private final  int[][] pieceSquareTable;


    public AI_Main_v1(boolean is1st) {

        if (this.dim == 6) {
            this.timeLimit = this.timeConstant;
            this.board = new char[][]{  {'-', 'b', 'b', 'b', 'b', '-'},       // 6
                                        {'w', '-', '-', '-', '-', 'w'},       // 5
                                        {'w', '-', '-', '-', '-', 'w'},       // 4
                                        {'w', '-', '-', '-', '-', 'w'},       // 3
                                        {'w', '-', '-', '-', '-', 'w'},       // 2
                                        {'-', 'b', 'b', 'b', 'b', '-'}  };    // 1
            //                            A    B    C    D    E    F

            this.pieceSquareTable = new int[][]{{-80, -25, -10, -10, -25, -80},      // 8
                                                {-25, +10, +20, +20, +10, -25},      // 7
                                                {-10, +20, +50, +50, +20, -10},      // 6
                                                {-10, +20, +50, +50, +20, -10},      // 3
                                                {-25, +10, +20, +20, +10, -25},      // 2
                                                {-80, -25, -10, -10, -25, -80}};     // 1
            //                                    A    B    C    D    E    F

        } else {
            this.timeLimit = 2 * this.timeConstant;
            this.board = new char[][]{  {'-', 'b', 'b', 'b', 'b', 'b', 'b', '-'},       // 8
                                        {'w', '-', '-', '-', '-', '-', '-', 'w'},       // 7
                                        {'w', '-', '-', '-', '-', '-', '-', 'w'},       // 6
                                        {'w', '-', '-', '-', '-', '-', '-', 'w'},       // 5
                                        {'w', '-', '-', '-', '-', '-', '-', 'w'},       // 4
                                        {'w', '-', '-', '-', '-', '-', '-', 'w'},       // 3
                                        {'w', '-', '-', '-', '-', '-', '-', 'w'},       // 2
                                        {'-', 'b', 'b', 'b', 'b', 'b', 'b', '-'}  };    // 1
            //                            A    B    C    D    E    F    G    H

            this.pieceSquareTable = new int[][]{{-80, -25, -20, -20, -20, -20, -25, -80},      // 8
                                                {-25, +10, +10, +10, +10, +10, +10, -25},      // 7
                                                {-20, +10, +25, +25, +25, +25, +10, -20},      // 6
                                                {-20, +10, +25, +50, +50, +25, +10, -20},      // 5
                                                {-20, +10, +25, +50, +50, +25, +10, -20},      // 4
                                                {-20, +10, +25, +25, +25, +25, +10, -20},      // 3
                                                {-25, +10, +10, +10, +10, +10, +10, -25},      // 2
                                                {-80, -25, -20, -20, -20, -20, -25, -80}};     // 1
            //                                    A    B    C    D    E    F    G    H
        }

        this.printBoard();
        System.out.println();

        if (is1st) {
            this.directions = new int[][]{{0, 0}, {0, 1}, {0, -1}, {-1, 1}, {1, 1}, {-1, -1}, {1, -1}, {1, 0}, {-1, 0}};
            this.myColor = 'b';
            this.opColor = 'w';
            this.opponentID = 1;
            this.play1st();
        } else {
            this.directions = new int[][]{{0, 0}, {1, -1}, {-1, -1}, {1, 1}, {-1, 1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            this.myColor = 'w';
            this.opColor = 'b';
            this.opponentID = 0;
            this.play2nd();
        }
    }

    private void play1st() {
        while (true) {
            makeMove();
            if (isFinished(myColor) || isFinished(opColor)) {
                break;
            }
            readMove();
            if (isFinished(myColor) || isFinished(opColor)) {
                break;
            }
        }
    }

    private void play2nd() {
        while (true) {
            readMove();
            if (isFinished(myColor) || isFinished(opColor)) {
                break;
            }
            makeMove();
            if (isFinished(myColor) || isFinished(opColor)) {
                break;
            }
        }
    }

    private boolean isFinished(char color) {
        return countComponents(color) == 1;
    }

    private int countComponents(char color) {
        int count = 0;

//        System.out.println(color);
//        for (int r = 0; r < this.dim; r++) {
//            for (int c = 0; c < this.dim; c++) {
//                System.out.print(vis[r][c] + " ");
//            }
//            System.out.println();
//        }

        for (int r = 0; r < this.dim; r++) {
            for (int c = 0; c < this.dim; c++) {
                this.vis[r][c] = false;
            }
        }

        for (int r = 0; r < this.dim; r++) {
            for (int c = 0; c < this.dim; c++) {
                if ((this.board[r][c] == color) && (!this.vis[r][c])) {
                    count++;
                    bfs(r, c);
                }
            }
        }

        return count;
    }

    private void bfs(int srcRow, int srcCol) {
        ArrayList<Integer> rQ = new ArrayList<>();
        ArrayList<Integer> cQ = new ArrayList<>();

        rQ.add(srcRow);
        cQ.add(srcCol);

        char color = this.board[srcRow][srcCol];
        this.vis[srcRow][srcCol] = true;

        int currRow, currCol, nRow, nCol;

        while (!rQ.isEmpty()) {
            currRow = rQ.remove(0);
            currCol = cQ.remove(0);

            for (int d = 1; d < 9; d++) {
                nRow = currRow + this.directions[d][0];
                nCol = currCol + this.directions[d][1];


                if (nRow >= 0 && nRow < this.dim && nCol >= 0 && nCol < this.dim) { // on board
                    if ((this.board[nRow][nCol] == color) && (!this.vis[nRow][nCol])) {
                        this.vis[nRow][nCol] = true;
                        rQ.add(nRow);
                        cQ.add(nCol);
                    }
                }
            }
        }
    }


    private void readMove () {
        int fx, fy, tx, ty;
        while (true) {
            try {
                Thread.sleep(100);
                Scanner in = new Scanner(new File("shared.txt"));
                if (in.hasNext()) {
                    int who = in.nextInt();
                    if (who == this.opponentID) {
                        fx = in.nextInt();
                        fy = in.nextInt();
                        tx = in.nextInt();
                        ty = in.nextInt();

                        if (this.board[tx][ty] == this.myColor) {
                            this.myPieceCount--;
                        }

                        this.board[fx][fy] = '-';
                        this.board[tx][ty] = this.opColor;

                        this.printBoard();
                        System.out.println();

                        return;
                    }
                    in.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void makeMove() {
        this.comparisonCount = 0;
        this.startTime = System.nanoTime();
        int score = minimax(this.maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

//        if (this.bfx == -1) {
//            System.out.println("in");
//            System.out.println(this.comparisonCount + ": " + score);
//            score = minimax(3, this.negInf, this.posInf, true);
//            System.out.println(this.comparisonCount + ": " + score);
//        }

        if (this.board[this.btx][this.bty] == this.opColor) {
            this.opPieceCount--;
        }
        this.board[this.bfx][this.bfy] = '-';
        this.board[this.btx][this.bty] = this.myColor;

        this.writeMove("" + this.bfx + " " + this.bfy + " " + this.btx + " " + this.bty);
        System.out.println("" + this.bfx + " " + this.bfy + " " + this.btx + " " + this.bty);
        System.out.println(this.comparisonCount + ": " + score + " " + this.countComponents(this.myColor) + " " + this.countComponents(this.opColor));
        this.printBoard();
        System.out.println();

        this.bfx = this.bfy = this.btx = this.bty = -1;
    }

    private int minimax(int depth, int alpha, int beta, boolean isMaximizing) {
//        if (this.isFinished(myColor)) {
////            System.out.println("depth is " + depth);
//            return this.posInf - 1;
//        }
//        if (this.isFinished(opColor)) {
////            System.out.println("depth is " + depth);
//            return this.negInf + 1;
//        }
//        if (depth == 0) {
//            return evaluate();
//        }

        if (System.nanoTime() - this.startTime > this.timeLimit) {
            return evaluate();
        }

        if (depth == 0 || this.isFinished(myColor) || this.isFinished(opColor)) {
            return evaluate();
        }

        if (isMaximizing) {
            int bestVal = Integer.MIN_VALUE;
            for (int fx = 0; fx < this.dim; fx++) {
                for (int fy = 0; fy < this.dim; fy++) {
                    if (this.board[fx][fy] == this.myColor) {
                        for (int d = 1; d < 9; d++) {
                            // determine change in row & col for direction[d]
                            int cx = this.directions[d][0], cy = this.directions[d][1];

                            // count steps in direction d
                            int count = countInDir(fx, fy, cx, cy);

                            // check if move is reachable
                            int tx = fx + cx * count, ty = fy + cy * count;
                            if (tx < 0 || tx >= this.dim || ty < 0 || ty >= this.dim) { // not reachable
                                continue;                                               // so, continue
                            }

                            // move is reachable

                            // check if move is valid
                            if (isIllegalMove(fx, fy, cx, cy, count, this.myColor, this.opColor)) { // move is not valid
                                continue;                                                           // so, continue
                            }

                            // move is valid

                            // store the destination color for revert back
                            char tColor = this.board[tx][ty];

                            // makeMove
                            this.board[fx][fy] = '-';
                            this.board[tx][ty] = this.myColor;
                            if (tColor == this.opColor) {
                                this.opPieceCount--;
                            }

                            // call minimax
                            int currVal = minimax(depth-1, alpha, beta, false);

                            if (depth == this.maxDepth) {
                                this.comparisonCount++;
                            }

                            if (currVal > bestVal) {
                                bestVal = currVal;
//                                if (depth == this.maxDepth) {
//                                    this.bfx = fx;
//                                    this.bfy = fy;
//                                    this.btx = tx;
//                                    this.bty = ty;
//                                }
                            }
                            if (bestVal > alpha) {
                                alpha = bestVal;
                                if (depth == this.maxDepth) {
                                    this.bfx = fx;
                                    this.bfy = fy;
                                    this.btx = tx;
                                    this.bty = ty;
                                }
                            }

                            // revertMove
                            this.board[fx][fy] = this.myColor;
                            this.board[tx][ty] = tColor;
                            if (tColor == this.opColor) {
                                this.opPieceCount++;
                            }

                            // pruning
                            if (beta <= alpha) {
                                return bestVal;
                            }
                        }
                    }
                }
            }
            return bestVal;
        }
        else {
            if (depth == this.maxDepth) {
                System.out.println("this is wrongg");
            }

            int bestVal = Integer.MAX_VALUE;
            for (int fx = 0; fx < this.dim; fx++) {
                for (int fy = 0; fy < this.dim; fy++) {
                    if (this.board[fx][fy] == this.opColor) {
                        for (int d = 1; d < 9; d++) {
                            // determine change in row & col for direction[d]
                            int cx = this.directions[d][0], cy = this.directions[d][1];

                            // count steps in direction d
                            int count = countInDir(fx, fy, cx, cy);

                            // check if move is reachable
                            int tx = fx + cx * count, ty = fy + cy * count;
                            if (tx < 0 || tx >= this.dim || ty < 0 || ty >= this.dim) { // not reachable
                                continue;                                               // so, continue
                            }

                            // move is reachable

                            // check if move is valid
                            if (isIllegalMove(fx, fy, cx, cy, count, this.opColor, this.myColor)) { // move is not valid
                                continue;                                                           // so, continue
                            }

                            // move is valid

                            // store the destination color for revert back
                            char tColor = this.board[tx][ty];

                            // makeMove
                            this.board[fx][fy] = '-';
                            this.board[tx][ty] = this.opColor;
                            if (tColor == this.myColor) {
                                this.myPieceCount--;
                            }

                            // call minimax
                            int currVal = minimax(depth-1, alpha, beta, true);

                            if (depth == this.maxDepth) {
//                                this.comparisonCount++;
                                System.out.println("but it should not be!!!!!!!!!!!");
                            }

                            if (currVal < bestVal) {
                                bestVal = currVal;
                            }
                            if (bestVal < beta) {
                                beta = bestVal;
                                if (depth == this.maxDepth) {
                                    System.out.println("!!!!!!!!!!!!!!!!!!!");
                                }
                            }

                            // revertMove
                            this.board[fx][fy] = this.opColor;
                            this.board[tx][ty] = tColor;
                            if (tColor == this.myColor) {
                                this.myPieceCount++;
                            }

                            // pruning
                            if (beta <= alpha) {
                                return bestVal;
                            }
                        }
                    }
                }
            }
            return bestVal;
        }
    }


    // FIXME
    private int evaluate() {
        int score = 0;

        if (isFinished(myColor)) {
            score = (int)(this.posInf + 1000 * Math.random());
            return score;
        }
        if (isFinished(opColor)) {
            score = (int)(this.negInf - 1000 * Math.random());
            return score;
        }

        score += this.evaluateByDensity() * 1000;
        score += this.evaluateByPST() * 1000;
        score += this.evaluateByArea() * 100;
        score += this.evaluateByQuads() * 10;
        score += this.evaluateByConnectedness() * 1000;
        score += (int)(10000 * Math.random());

        return score;
    }


    private int evaluateByPST() {
        int score, myScore = 0, opScore = 0;

        for (int i = 0; i < this.dim; i++) {
            for (int j = 0; j < this.dim; j++) {
                if (this.board[i][j] == myColor) {
                    myScore += this.pieceSquareTable[i][j];
                } else if (this.board[i][j] == opColor) {
                    opScore += this.pieceSquareTable[i][j];
                }
            }
        }

        score = myScore / this.myPieceCount - opScore / this.myPieceCount;
        return score;
    }

    private int evaluateByArea() {
        int myArea, opArea, score;
        int myLeft = this.dim, myRight = 0, myUp = this.dim, myDown = 0;
        int opLeft = this.dim, opRight = 0, opUp = this.dim, opDown = 0;

        for (int i = 0; i < this.dim; i++) {
            for (int j = 0; j < this.dim; j++) {
                if (this.board[i][j] == myColor) {
                    if (i < myUp) {
                        myUp = i;
                    }
                    if (i > myDown) {
                        myDown = i;
                    }
                    if (j < myLeft) {
                        myLeft = j;
                    }
                    if (j > myRight) {
                        myRight = j;
                    }
                }
                else if (this.board[i][j] == opColor) {
                    if (i < opUp) {
                        opUp = i;
                    }
                    if (i > opDown) {
                        opDown = i;
                    }
                    if (j < opLeft) {
                        opLeft = j;
                    }
                    if (j > opRight) {
                        opRight = j;
                    }
                }
            }
        }

        myArea = (myDown - myUp + 1) * (myRight - myLeft + 1);
        opArea = (opDown - opUp + 1) * (opRight - opLeft + 1);
        score = opArea - myArea;

        return score;
    }

    private int evaluateByDensity() {
        int score, myGX = 0, myGY = 0, opGX = 0, opGY = 0, myDist = 0, opDist = 0;

        for (int i = 0; i < this.dim; i++) {
            for (int j = 0; j < this.dim; j++) {
                if (this.board[i][j] == this.myColor) {
                    myGX += i;
                    myGY += j;
                } else if (this.board[i][j] == this.opColor) {
                    opGX += i;
                    opGY += j;
                }
            }
        }

        myGX /= this.myPieceCount;
        opGX /= this.opPieceCount;

        for (int i = 0; i < this.dim; i++) {
            for (int j = 0; j < this.dim; j++) {
                if (this.board[i][j] == this.myColor) {
                    myDist += Math.max(Math.abs(myGX - i), Math.abs(myGY - j));
                } else if (this.board[i][j] == this.opColor) {
                    opDist += Math.max(Math.abs(opGX - i), Math.abs(opGY - j));
                }
            }
        }

        myDist /= myPieceCount;
        opDist /= opPieceCount;

        score = opDist - myDist;

        return score;
    }

    private int evaluateByQuads() {
        int score, myScore = 0, opScore = 0, myCount, opcount;

        for (int r = 0; r < this.dim - 1; r++) {
            for (int c = 0; c < this.dim - 1; c++) {
                myCount = 0;
                opcount = 0;
                if (this.board[r][c] == this.myColor) {
                    myCount++;
                } else if (this.board[r][c] == this.opColor) {
                    opcount++;
                }
                if (this.board[r][c+1] == this.myColor) {
                    myCount++;
                } else if (this.board[r][c+1] == this.opColor) {
                    opcount++;
                }
                if (this.board[r+1][c] == this.myColor) {
                    myCount++;
                } else if (this.board[r+1][c] == this.opColor) {
                    opcount++;
                }
                if (this.board[r+1][c+1] == this.myColor) {
                    myCount++;
                } else if (this.board[r+1][c+1] == this.opColor) {
                    opcount++;
                }
                if (myCount > 2) {
                    myScore++;
                }
                if (opcount > 2) {
                    opScore++;
                }
            }
        }

        score = myScore - opScore;

        return score;
    }

    private int evaluateByConnectedness() {
        int score, myConnectionCount = 0, opConnectionCount = 0;

        for (int fx = 0; fx < this.dim; fx++) {
            for (int fy = 0; fy < this.dim; fy++) {
                if (this.board[fx][fy] == this.myColor) {
                    for (int d = 1; d < 9; d++) {
                        // determine change in row & col for direction[d]
                        int cx = this.directions[d][0], cy = this.directions[d][1];

                        // check if move is reachable
                        int tx = fx + cx, ty = fy + cy;
                        if (tx < 0 || tx >= this.dim || ty < 0 || ty >= this.dim) { // not reachable
                            continue;                                               // so, continue
                        }

                        if (this.board[tx][ty] == this.myColor) {
                            myConnectionCount++;
                        }
                    }
                }
                else if (this.board[fx][fy] == this.opColor) {
                    for (int d = 1; d < 9; d++) {
                        // determine change in row & col for direction[d]
                        int cx = this.directions[d][0], cy = this.directions[d][1];

                        // check if move is reachable
                        int tx = fx + cx, ty = fy + cy;
                        if (tx < 0 || tx >= this.dim || ty < 0 || ty >= this.dim) { // not reachable
                            continue;                                               // so, continue
                        }

                        if (this.board[tx][ty] == this.opColor) {
                            opConnectionCount++;
                        }
                    }
                }
            }
        }

        score = myConnectionCount / this.myPieceCount - opConnectionCount / this.opPieceCount;

        return score;
    }


    // FIXME
    private int evaluateByMobility() {
        int score, myScore = 0, opScore = 0;

        for (int fx = 0; fx < this.dim; fx++) {
            for (int fy = 0; fy < this.dim; fy++) {
                if (this.board[fx][fy] == this.myColor) {
                    for (int d = 1; d < 9; d++) {
                        // determine change in row & col for direction[d]
                        int cx = this.directions[d][0], cy = this.directions[d][1];

                        // count steps in direction d
                        int count = countInDir(fx, fy, cx, cy);

                        // check if move is reachable
                        int tx = fx + cx * count, ty = fy + cy * count;
                        if (tx < 0 || tx >= this.dim || ty < 0 || ty >= this.dim) { // not reachable
                            continue;                                               // so, continue
                        }

                        // move is reachable

                        // check if move is valid
                        if (isIllegalMove(fx, fy, cx, cy, count, this.myColor, this.opColor)) { // move is not valid
                            continue;                                                           // so, continue
                        }

                        // move is valid
                        myScore++;


                        // store the destination color
                        char tColor = this.board[tx][ty];
                        if (tColor == this.opColor) { // move is capture

                        }
                    }
                }
                else if (this.board[fx][fy] == this.opColor) {
                    for (int d = 1; d < 9; d++) {
                        // determine change in row & col for direction[d]
                        int cx = this.directions[d][0], cy = this.directions[d][1];

                        // count steps in direction d
                        int count = countInDir(fx, fy, cx, cy);

                        // check if move is reachable
                        int tx = fx + cx * count, ty = fy + cy * count;
                        if (tx < 0 || tx >= this.dim || ty < 0 || ty >= this.dim) { // not reachable
                            continue;                                               // so, continue
                        }

                        // move is reachable

                        // check if move is valid
                        if (isIllegalMove(fx, fy, cx, cy, count, this.opColor, this.myColor)) { // move is not valid
                            continue;                                                           // so, continue
                        }

                        // move is valid
                        opScore++;

                        // store the destination color
                        char tColor = this.board[tx][ty];

                        if (tColor == this.opColor) { // move is capture

                        }
                    }
                }
            }
        }

        score = myScore - opScore;

        return score;
    }


    private boolean isIllegalMove(int fx, int fy, int cx, int cy, int count, char mC, char oC) {
        int x = fx, y = fy;
        for (int i = 0, imax = count - 1; i  < imax; i++) {
            x += cx;
            y += cy;
            if (this.board[x][y] == oC) {
                return true;
            }
        }

        return this.board[x + cx][y + cy] == mC;
    }

    private int countInDir(int fx, int fy, int cx, int cy) {
        int x = fx, y = fy, count = 1;
        while (true) {
            x += cx;
            y += cy;
            if (x < 0 || x >= dim || y < 0 || y >= dim) {
                break;
            }
            if (this.board[x][y] != '-') {
                count++;
            }
        }
        x = fx;
        y = fy;
        while (true) {
            x -= cx;
            y -= cy;
            if (x < 0 || x >= dim || y < 0 || y >= dim) {
                break;
            }
            if (this.board[x][y] != '-') {
                count++;
            }
        }

        //System.out.println(fx + " " + fy + " " + cx + " " + cy + " " + count);

        return count;
    }

    private void writeMove (String move) {
        try {
            FileWriter out = new FileWriter("shared.txt");
            out.write("2\n" + move + "\n");
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printBoard() {
        for (char [] row : board) {
            for (char piece : row) {
                System.out.print(piece + " ");
            }
            System.out.println();
        }
    }
}
