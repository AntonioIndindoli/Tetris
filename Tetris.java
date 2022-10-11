import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tetris extends JPanel {

    //Declaring the Tetris pieces in 2d boolean arrays
    //True = block location
    //false = empty
    private static final boolean[][] i_piece = {
            {true,  true,  true,  true}
    };

    private static final boolean[][] l1_piece = {
            { true, false, false },
            { true, true,  true}
    };

    private static final boolean[][] l2_piece = {
            { false, false, true},
            {true,  true,  true}
    };

    private static final boolean[][] b_piece = {
            { true, true},
            { true, true}
    };

    private static final boolean[][] s_piece = {
            {false, true, true},
            { true,  true, false }
    };

    private static final boolean[][] t_piece = {
            {false, true, false },
            { true,  true, true}
    };

    private static final boolean[][] z_piece = {
            { true,  true, false },
            { false, true, true}
    };

    private Timer timer;
    private Timer timer2;
    private int currentBlock;
    private int currentBlock1;
    private int currentBlock2;
    private int currentBlock3;
    private boolean [][] fallingPiece;
    private Color fallingPieceColor;
    private boolean [][] nextPiece;
    private Color nextPieceColor;
    private boolean [][] holdPiece;
    private Color holdPieceColor;
    private Color backColor;
    private boolean holdFirst;
    private int holdCount;
    private int fallingPieceRow;
    private int fallingPieceCol;
    private int fallingPieceRot;
    private Color[][] base;
    private boolean isOver;
    private int fullCount = 0;
    private Color[][] oldRow;
    private Color[][] newRow;
    private int tempRowNum;
    private int score;
    private int diff;
    private boolean isPause;
    private String endText;
    private String endText2;
    private Color gameOverColor1;
    private Color gameOverColor2;
    private Color gameOverColorText1;
    private Color gameOverColorText2;
    private Color pauseColor;
    private String pauseText1;
    private String pauseText2;


    //Putting the tetris pieces into another array, making a 3 dimensional array
    private static boolean[][][] block_pieces = {
            i_piece, l1_piece, l2_piece, b_piece, s_piece, t_piece, z_piece
    };

    //Colors for the pieces
    private static Color[] block_colors = {
            Color.red, Color.yellow, Color.blue, Color.magenta,
            Color.cyan, Color.green, Color.orange
    };


    public static void main(String[] args) {

        //Making the frame
        JFrame f = new JFrame("Tetris");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(550, 750);
        f.setVisible(true);
        f.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation(dim.width/2-f.getSize().width/2, dim.height/2-f.getSize().height/2);

        Tetris game = new Tetris();
        game.makeBoard();
        f.getContentPane().add(game);
        game.game();

        //Adding key inputs
        f.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        game.rotatePiece();
                        break;
                    case KeyEvent.VK_DOWN:
                        game.movePiece(+1,0);
                        break;
                    case KeyEvent.VK_LEFT:
                        game.movePiece(0,-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        game.movePiece(0,+1);
                        break;
                    case KeyEvent.VK_SPACE:
                        game.resetGame();
                        break;
                    case KeyEvent.VK_CONTROL:
                        game.holdPiece();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        game.pauseGame();
                        break;
                }

            }

            public void keyReleased(KeyEvent e) {
            }
        });


    }

    //Create color array and assign black to each position
    public void makeBoard() {
        base = new Color[10][20];

        for(int row1=0; row1<base.length;row1++) {
            for(int column1=0;column1<base[row1].length;column1++) {

                base[row1][column1] = Color.BLACK;

            }

        }

        //initializing lots of variables
        gameOverColor1 = new Color(1.0f, 1.0f, 1.0f, 0.0f);
        gameOverColor2 = Color.WHITE;
        pauseColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);
        gameOverColorText1 = Color.RED;
        gameOverColorText2 = Color.WHITE;
        pauseText1 = ("");
        pauseText2 = ("");
        currentBlock1 = (int) (Math.random() * 7);
        currentBlock2 = (int) (Math.random() * 7);
        currentBlock3 = (int) (Math.random() * 7);
        backColor = Color.GRAY;
        diff = 600;
        endText = ("");
        endText2 = ("");
        holdFirst = true;
        holdCount = 0;
        newPiece();


    }

    //Paints board black, creates new piece, sets isOver to true then restarts timer
    private void resetGame() {
        if(isPause == false) {
            for(int row1=0; row1<base.length;row1++) {
                for(int column1=0;column1<base[row1].length;column1++) {
                    base[row1][column1] = Color.BLACK;
                }
            }
            backColor = Color.GRAY;
            gameOverColor1 = new Color(0.0f, 0.0f, 0.0f, 0.0f);
            timer.cancel();
            newPiece();
            isOver = false;
            score = 0;
            endText = ("");
            endText2 = ("");
            holdFirst = true;
            holdCount = 0;
            diff = 600;
            game();
        }
    }

    private void pauseGame() {
        if(isOver == false) {
            if (isPause == true) {
                game();
                pauseColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);
                isPause = false;
                isOver = false;
                timer2.cancel();
                pauseText1 = ("");
                pauseText2 = ("");
                System.out.print("NotPause");
                gameOverColor1 = new Color(0.0f, 0.0f, 0.0f, 0.0f);
            }
            else {
                gamePauseLoop();
                isPause = true;
                timer.cancel();
                pauseText1 = ("PAUSED");
                pauseText2 = ("Press escape to continue");
                pauseColor = Color.WHITE;
                gameOverColor1 = new Color(0.0f, 0.0f, 0.0f, 1.0f);
            }
            repaint();
        }
    }

    //selects random piece that isn't one of the last 3 pieces
    public void newPiece() {
        fallingPiece = block_pieces[currentBlock];
        fallingPieceColor = block_colors[currentBlock];
        fallingPieceRow = 0; //starting row
        fallingPieceCol = 4; //and column
        fallingPieceRot = 0;
        currentBlock3 = currentBlock2;
        currentBlock2 = currentBlock1;
        currentBlock1 = currentBlock;
        currentBlock = (int) (Math.random() * 7);
        Boolean blockCycle = true;
        while (blockCycle) {
            if (currentBlock == currentBlock1 || currentBlock == currentBlock2 || currentBlock == currentBlock3) {
                currentBlock = (int) (Math.random() * 7);
            }
            else {blockCycle = false;}
        }
        nextPiece = block_pieces[currentBlock];
        nextPieceColor = block_colors[currentBlock];

    }

    //Changes position of the falling piece
    //Also returns if the move was successful or not
    public boolean movePiece(int Crow, int Ccol) {
        if(isPause == false) {
            fallingPieceRow += Crow;
            fallingPieceCol += Ccol;

            //checks if move isn't legal, if so, undo the move
            if (ifAble() == false) {
                fallingPieceRow -= Crow;
                fallingPieceCol -= Ccol;
                return false;

            }
            repaint();
            return true;
        }
        else {return false;}
    }

    //Adds moving piece to the board
    public void placePiece() {

        for (int i = 0; i < fallingPiece.length; i++) {
            for (int h = 0; h < fallingPiece[i].length; h++) {
                if(fallingPiece[i][h] == true) {
                    base[i + fallingPieceCol][ h + fallingPieceRow] = fallingPieceColor;
                }
            }
        }
        rowClear(); //rowClear is called everytime a block is placed
        score += 2;
        holdCount = 0;
        repaint();
    }

    //Hold piece functionality
    public void holdPiece() {
        if(isPause == false) {
            if (holdCount == 0) {
                if (holdFirst == false) {
                    boolean[][] fallingTempPiece = fallingPiece;
                    Color fallingTempPieceColor= fallingPieceColor;
                    fallingPiece = holdPiece;
                    fallingPieceColor = holdPieceColor;
                    fallingPieceRow = 0; //starting row
                    fallingPieceCol = 4; //and column
                    fallingPieceRot = 0;
                    holdPiece = fallingTempPiece;
                    holdPieceColor = fallingTempPieceColor;
                }
                if (holdFirst == true) {
                    holdPiece = fallingPiece;
                    holdPieceColor = fallingPieceColor;
                    newPiece();
                }
                holdFirst = false;
                repaint();
                holdCount++;
            }
        }
    }

    //This method checks if rows are full then deletes them and
    //pushes uncleared rows down
    public void rowClear() {
        oldRow = new Color[10][20];
        newRow = new Color[10][20];
        fullCount = 0; //used to count how many blocks occupy a row
        tempRowNum = 19; //this keeps track of how many rows are being cleared
        int tempval = 0;
        //Making newRow all black
        for (int i = 0; i < base[0].length; i++) {
            for (int h = 0; h < base.length; h++) {
                newRow[h][i] = Color.BLACK;
            }
        }
        //this for loop cycles from the bottom of the board to the top
        for (int i = base[0].length-1; i > 0; i--) {
            for (int h = 0; h < base.length; h++) {
                //This checks if there is a block in each position in a row,
                //and if there is it adds one to fullcount
                if(base[h][i] != Color.BLACK) {
                    fullCount++;
                }
                //oldRow is filled with values for row i
                oldRow[h][i] = base[h][i];
            }
            //if a row is full then add one to tempRowNum
            //this allows the blocks to fall
            if (fullCount == 10) {
                tempRowNum++;
                tempval++;

            }
            //if a row isn't full then it is copied into newRow
            if (fullCount != 10) {
                for (int t = 0; t < base.length; t++) {
                    newRow[t][tempRowNum] = oldRow[t][i];
                }
            }
            fullCount = 0; //after a row is cycled through this is reset so it can check another row
            tempRowNum--;
        }
        //finally update the board with newRow
        for (int i = 0; i < base[0].length; i++) {
            for (int h = 0; h < base.length; h++) {
                base[h][i] = newRow[h][i];
            }
        }
        score += 10*(tempval * tempval);
        tempval = 0;
        System.out.println(score);
    }



    //Rotate piece
    public void rotatePiece() {
        if(isPause == false) {
            //Every other attempted rotation will undergo a different rotation method
            //The first method under this if condition rotates the piece 90 degrees
            if (fallingPieceRot % 2 == 0) {
                fallingPieceRot += 1;
                boolean [][] oldPiece = fallingPiece;
                //This creates a new temp array that has the inversed length and height of the original
                boolean [][] tempPiece = new boolean[fallingPiece[0].length][fallingPiece.length];
                //System.out.println(fallingPieceRot);
                for (int i = 0; i < fallingPiece[0].length; i++) {
                    for (int h = 0; h < fallingPiece.length; h++) {
                        tempPiece[i][h] = fallingPiece[fallingPiece.length - h -1][i];
                        //This populates the temp array
                    }
                }
                fallingPiece = tempPiece;
                if (ifAble() == false) {
                    fallingPiece = oldPiece;
                    fallingPieceRot -= 1;
                }

            }

            //This rotate method first rotates the piece back to its original rotation,
            //then flips the rows and columns, which for some reason works
            else {
                boolean [][] oldPiece2 = fallingPiece;
                fallingPieceRot += 1;
                boolean [][] tempPiece = new boolean[fallingPiece[0].length][fallingPiece.length];
                //System.out.println(fallingPieceRot);
                for (int i = 0; i < fallingPiece[0].length; i++) {
                    for (int h = 0; h < fallingPiece.length; h++) {
                        tempPiece[i][h] = fallingPiece[fallingPiece.length - h -1][i];
                    }
                }
                fallingPiece = tempPiece;
                tempPiece = new boolean[fallingPiece.length][fallingPiece[0].length];
                for (int i = 0; i < fallingPiece.length; i++) {
                    for (int h = 0; h < fallingPiece[0].length; h++) {
                        tempPiece[i][h] = fallingPiece[i][h];
                    }
                }
                fallingPiece = tempPiece;
                if (ifAble() == false) {
                    fallingPiece = oldPiece2;
                    fallingPieceRot -= 1;
                }
            }
            repaint();
        }
    }

    //Checks if move is legal
    private boolean ifAble() {
        for (int i = 0; i < fallingPiece.length; i++) {
            for (int h = 0; h < fallingPiece[i].length; h++) {
                //checks each position in the boolean array if its
                //position is on board
                if(fallingPiece[i][h] == true) {
                    if (fallingPieceCol+i < 0 || fallingPieceCol+i > 9 || fallingPieceRow+h < 0 || fallingPieceRow+h > 19) {
                        return false;
                    }
                    if (base[fallingPieceCol+i][fallingPieceRow+h] != Color.BLACK) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //Paint grid of blocks and fill with assigned color from base array
    @Override
    public void paintComponent(Graphics g)
    {
        g.setColor(backColor); //Background grid
        g.fillRect(0, 0, 360, 800);

        g.setColor(Color.BLACK); //Right side color
        g.fillRect(360, 0, 190, 800);

        //Painting board
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                g.setColor(base[i][j]);
                g.fillRect(36*i, 36*j, 35, 35);
            }
        }
        g.setColor(Color.BLACK);  //Score backing color
        g.fillRect(370, 55, 100, 30);


        //Score and next block
        g.setFont(new Font("Impact", Font.PLAIN, 25));
        g.setColor(Color.WHITE);
        g.drawString("" + score, 380, 77);
        g.drawString("SCORE", 380, 45);

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Impact", Font.PLAIN, 20));
        g.drawString("UP NEXT", 380, 150);
        g.drawString("HOLD", 380, 350);

        //Calling methods to draw pieces
        drawFallingPiece(g);
        drawNextPiece(g);
        drawHoldPiece(g);

        //End Screen
        g.setColor(gameOverColor1);
        g.fillRect(0, 252, 360, 107);

        g.setColor(gameOverColorText1);
        g.setFont(new Font("Impact", Font.PLAIN, 40));
        g.drawString(endText, 95, 305);

        g.setColor(gameOverColorText2);
        g.setFont(new Font("Impact", Font.PLAIN, 20));
        g.drawString(endText2, 86, 335);

        //Pause screen
        g.setColor(pauseColor);
        g.setFont(new Font("Impact", Font.PLAIN, 40));
        g.drawString(pauseText1, 123, 305);

        g.setColor(pauseColor);
        g.setFont(new Font("Impact", Font.PLAIN, 20));
        g.drawString(pauseText2, 88, 335);
    }

    //paints the falling piece
    private void drawFallingPiece(Graphics g) {
        for (int i = 0; i < fallingPiece.length; i++) {
            for (int h = 0; h < fallingPiece[i].length; h++) {

                //checks each position in the boolean array if its true
                //if so, then paint a new block with designated color
                if(fallingPiece[i][h] == true) {
                    g.setColor(fallingPieceColor);
                    g.fillRect(36*i +fallingPieceCol*36, 36*h + fallingPieceRow*36, 35, 35);
                }
            }
        }
    }
    //draws upcoming piece
    private void drawNextPiece(Graphics g) {
        for (int i = 0; i < nextPiece.length; i++) {
            for (int h = 0; h < nextPiece[i].length; h++) {

                //checks each position in the boolean array if its true
                //if so, then paint a new block with designated color
                if(nextPiece[i][h] == true) {
                    g.setColor(nextPieceColor);
                    g.fillRect(36*i + 390, 36*h + 175, 35, 35);
                }
            }
        }
    }
    //draws hold piece
    private void drawHoldPiece(Graphics g) {
        if (holdFirst == false) {
            for (int i = 0; i < holdPiece.length; i++) {
                for (int h = 0; h < holdPiece[i].length; h++) {

                    //checks each position in the boolean array if its true
                    //if so, then paint a new block with designated color
                    if(holdPiece[i][h] == true) {
                        g.setColor(holdPieceColor);
                        g.fillRect(36*i + 380, 36*h + 380, 35, 35);
                    }
                }
            }
        }
    }

    //Timer function
    public void game() {

        timer = new Timer();
        timer.schedule(new RemindTask(),
                0,        //initial delay
                1*diff);  //subsequent rate
    }

    class RemindTask extends TimerTask {
        int count = 0;
        int end = 0;
        public void run() {
            count ++;

            //Moves the piece down one row with each tick
            if(movePiece(+1,0) == false) { //Checks if the move was legal
                placePiece(); //Fixes piece to board if not
                end++;
                if(base[5][1] != Color.BLACK) {
                    System.out.print("END");
                    isOver = true;
                    timer.cancel();
                    for(int row1=0; row1<base.length;row1++) {
                        for(int column1=7;column1<10;column1++) {
                            base[row1][column1] = Color.BLACK;
                        }
                    }
                    for(int row1=0; row1<base.length;row1++) {
                        for(int column1=0;column1<base[row1].length;column1++) {
                            if(base[row1][column1] != Color.BLACK) {
                                base[row1][column1] = Color.LIGHT_GRAY;
                            }
                        }
                    }
                    fallingPieceColor = Color.LIGHT_GRAY;
                    gameOverColor1 = new Color(0.0f, 0.0f, 0.0f, 1.0f);
                    backColor = Color.DARK_GRAY;
                    count = 0;
                    endText = ("GAME OVER");
                    endText2 = ("Press space to continue");
                    repaint();
                    gameEnd();
                }
                end = count;
                if(isOver == false) {
                    newPiece(); //Then creates new piece as long as game isnt over
                }
            }

            //Difficulty functionality
            if (count == 120) {
                if (diff > 200) {
                    diff -= 100;
                    timer.cancel();
                    game();
                    movePiece(-1,0);
                    count = -50;
                    System.out.print("Speed Increase");
                }
            }

            repaint();
        }
    }

    //Loops flashing red background
    public void gameEnd() {

        timer = new Timer();
        timer.schedule(new RemindTask2(),
                0,        //initial delay
                1*400);  //subsequent rate
    }

    class RemindTask2 extends TimerTask {
        int count = 0;
        public void run() {
            count++;

            Color noColor = new Color(255, 0, 0, 150);

            if(count % 2 == 0) {
                gameOverColorText1 = Color.RED;
            }
            else {
                gameOverColorText1 = noColor;
            }
            repaint();
        }
    }

    public void gamePauseLoop() {

        timer2 = new Timer();
        timer2.schedule(new RemindTask3(),
                0,        //initial delay
                1*200);  //subsequent rate
    }

    class RemindTask3 extends TimerTask {
        int count = 0;
        public void run() {
            count++;
            if(count % 2 == 0) {
                pauseColor = Color.WHITE;
            }
            else {
                pauseColor = Color.LIGHT_GRAY;
            }
            repaint();
        }
    }

}
