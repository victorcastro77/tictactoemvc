package mvc.tictactoe;

import com.mrjaffesclass.apcs.messenger.*;

/**
 * The model represents the data that the app uses.
 * @author Roger Jaffe
 * @version 1.0
 */
public class Model implements MessageHandler {

  // Messaging system for the MVC
  private final Messenger mvcMessaging;

  // Model's data variables
private boolean whoseMove;
private boolean gameOver; 
private String[][] board;

  /**
   * Model constructor: Create the data representation of the program
   * @param messages Messaging class instantiated by the Controller for 
   *   local messages between Model, View, and controller
   */
  public Model(Messenger messages) {
    mvcMessaging = messages;
    

    

  }
  
  /**
   * Initialize the model here and subscribe to any required messages
   */
  public void init() {
    this.newGame();
this.mvcMessaging.subscribe("playerMove", this);
this.mvcMessaging.subscribe("newGame", this);

  }
  private void newGame() {
      
      this.board = new String[3][3];
    for(int row=0; row<this.board.length; row++) {
      for (int col=0; col<this.board[0].length; col++) {
        this.board[row][col] = "";
      }
    }
    this.whoseMove = false;
    this.gameOver = false;
     this.mvcMessaging.notify("gameOver", "");
  }

  
  public void messageHandler(String messageName, Object messagePayload) {
    // Display the message to the console for debugging
    if (messagePayload != null) {
      System.out.println("MSG: received by model: "+messageName+" | "+messagePayload.toString());
    } else {
      System.out.println("MSG: received by model: "+messageName+" | No data sent");
    }
    
    // playerMove message handler
    if (messageName.equals("playerMove")) {
      if(!gameOver){
        // Get the position string and convert to row and col
        String position = (String)messagePayload;
        Integer row = new Integer(position.substring(0,1));
        Integer col = new Integer(position.substring(1,2));
        // If square is blank...
        if (this.board[row][col].equals("")) {
          // ... then set X or O depending on whose move it is
          if (this.whoseMove) {
            this.board[row][col] = "X";
            this.whoseMove = !this.whoseMove;
          } else {
            this.board[row][col] = "O";
            this.whoseMove = !this.whoseMove;
          }
          // Send the boardChange message along with the new board
          isWinner();
          this.mvcMessaging.notify("boardChange", this.board);
        }
      }
    // newGame message handler
    } else if (messageName.equals("newGame")) {
      // Reset the app state
      this.newGame();
      // Send the boardChange message along with the new board 
      this.mvcMessaging.notify("boardChange", this.board);
    }
  }
  
   private void isWinner(){
       String player = (this.whoseMove) ? "X" : "O";
    for (int i=0; i<3; i++) {
      if (!board[i][0].isEmpty() && board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2])){
        this.mvcMessaging.notify("gameOver", "Winner: " + board[i][0]);
        this.gameOver = true;
        this.mvcMessaging.notify("isWinner", board[i][0]);
      }
      if (!board[0][i].isEmpty() && board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i]))
      {
        this.mvcMessaging.notify("gameOver", "Winner: " +board[0][i]);
        this.gameOver = true;
        this.mvcMessaging.notify("isWinner", board[0][i]);
      }
    }

    // Check the diagonals
    if (!board[0][0].isEmpty() && board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2])){
      this.mvcMessaging.notify("gameOver", "Winner: " +board[0][0]);
      this.gameOver = true;
      this.mvcMessaging.notify("isWinner", board[0][0]);
    }
    if (!board[0][2].isEmpty() && board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0])){
      this.mvcMessaging.notify("gameOver", "Winner: " +board[0][2]);
      this.gameOver = true;
      this.mvcMessaging.notify("isWinner", board[0][2]);
    }

    //Checks for any legal moves, if none then returns tie
    boolean tie = true;
    for(int y = 0; y < 3; y++){
        for(int x = 0; x < 3; x++){
            if(board[y][x].isEmpty()){
                tie = false;
            }
        }
    }

    if(tie){
        this.gameOver = true;
        this.mvcMessaging.notify("gameOver", "tie");
    }
  }
}
