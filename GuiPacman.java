import javafx.application.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import java.io.*;


public class GuiPacman extends Application
{
  private String outputBoard; // The filename for where to save the Board
  private Board board; // The Game Board

  // Fill colors to choose
  private static final Color COLOR_GAME_OVER = Color.rgb(238, 228, 218, 0.73);
  private static final Color COLOR_VALUE_LIGHT = Color.rgb(249, 246, 242);
  private static final Color COLOR_VALUE_DARK = Color.rgb(119, 110, 101);

  /** Add your own Instance Variables here */
  private static final int DIMENSIONPIX = 500; //dimension on the stage
  private Tile[][] tiles;
  private int dimension;

  //pane to put text and grid
  private GridPane pane;

  //pane for the game board
  private GridPane boardPane;

  //height and width of the stage
  private int stageHeight;
  private int stageWidth;
  //text to show score
  private Text scoreText;
  //the scene for the stage
  private Scene scene;
  //the stack pane use to stack game board and gameover pane
  private StackPane stack;
  //handler for key events
  private myKeyHandler handler;
  //pane for gameover
  private GridPane gameOverPane;
  //text to show that game is over
  private GridPane goTxtPane;


  /*
   * Name:      start
   * Purpose:   Start and keep the game running.
   * Parameter: Stage primaryStage
   * Return:    the stage that shows pacman game
   */
  @Override
  public void start(Stage primaryStage)
  {
    // Process Arguments and Initialize the Game Board
    processArgs(getParameters().getRaw().toArray(new String[0]));

    /** Add your Code for the GUI Here */
    pane = new GridPane();

    //set padding and gaps
    pane.setPadding(new Insets(11.5,12.5,13.5,14.5)); //set the padding of pane
    pane.setHgap(5.5);
    pane.setVgap(5.5);

    //set background color to black
    pane.setStyle("-fx-background-color: rgb(0, 0, 0)");

    GridPane textPane = new GridPane();

    //modify the text for Pac-Man and score
    Text txt = new Text(); //create text object
    scoreText = new Text();
    txt.setText("Pac-Man");
    scoreText.setText("   Score: " + board.getScore());
    txt.setFont(Font.font("Times New Roman", FontWeight.BOLD, 35));
    scoreText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
    txt.setFill(Color.WHITE);
    scoreText.setFill(Color.WHITE);

    dimension = this.board.getGrid().length;

    //add the element onto one of the rows and columns of the GridPane
    textPane.add(txt, 0, 0); //read about colspan and rowspan
    textPane.add(scoreText, dimension/2, 0);
    textPane.setHgap(400/dimension);
    textPane.setVgap(5.5);
    pane.setPadding(new Insets(11.5,12.5,13.5,14.5));

    //add textpane to pane
    pane.add(textPane, 0, 0);

    //set gaps for boardpane and initialize
    boardPane = new GridPane();
    boardPane.setHgap(5.5);
    boardPane.setVgap(5.5);

    //loading the grid
    tiles = loadGrid();

    //loop through all the tiles
    for(int i = 0; i < dimension; i++) {
      for(int j = 0; j < dimension; j++) {
        boardPane.add(tiles[i][j].getNode(), j, i);
      }
    }

    //add boardpane to pane
    pane.add(boardPane, 0, 1);

    //pane to stack it with game over texts
    stack = new StackPane();
    stack.getChildren().add(pane);

    stageHeight = (int)primaryStage.getHeight();
    stageWidth = (int)primaryStage.getWidth();

    scene = new Scene(stack, primaryStage.getWidth(),
      primaryStage.getHeight()); //scene that needs to be displayed

    primaryStage.setTitle("GuiPacman"); //title of the window(primary stage)
    primaryStage.setScene(scene); //set what scene to show in window
    primaryStage.show(); //call show

    //instantiation key handler to the scene
    handler = new myKeyHandler();
    scene.setOnKeyPressed(handler);

  }

  /*
   * Name:      loadGrid
   * Purpose:   method to load the tiles onto the stage
   * Parameter: none
   * Return:    return tiles that represent the game board
   */
  public Tile[][] loadGrid() {
    char[][] gameBoard = this.board.getGrid();
    int dimension = gameBoard.length;
    Tile[][] tiles = new Tile[dimension][dimension];
    for(int i = 0; i < dimension; i++) {
      for(int j = 0; j < dimension; j++) {
        tiles[i][j] = new Tile(gameBoard[i][j]);
      }
    }
    return tiles;
  }



  /** Add your own Instance Methods Here */

  /*
   * Name:       myKeyHandler
   *
   * Purpose:
   *
   *
   */
  private class myKeyHandler implements EventHandler<KeyEvent> {

   /*
    * Name:      handle
    * Purpose:   handle the KeyEvent of user's input.
    * Parameter:  KeyEvent e
    * Return:    handles key input and move pacman
    */
    @Override
    public void handle (KeyEvent e) {
      //when game is not over accept key inputs
      if(!board.isGameOver()) {
        if(e.getCode().equals(KeyCode.UP)) {
          board.move(Direction.UP);
          System.out.println("Moving<UP>");
        }
        else if(e.getCode().equals(KeyCode.DOWN)) {
          board.move(Direction.DOWN);
          System.out.println("Moving<DOWN>");
        }
        else if(e.getCode().equals(KeyCode.LEFT)) {
          board.move(Direction.LEFT);
          System.out.println("Moving<LEFT>");
        }
        else if(e.getCode().equals(KeyCode.RIGHT)) {
          board.move(Direction.RIGHT);
          System.out.println("Moving<RIGHT>");
        }
      }

      //else gameover text will be shown
      else {
        stack.removeEventHandler(KeyEvent.KEY_PRESSED,this);
        gameIsOver();
        return;
      }

      //save board when press s
      if(e.getCode() == KeyCode.S) {
        try {
          board.saveBoard(outputBoard);
          System.out.println("Saving board to <Pac-Man.board>");
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
      }

      //update the score
      scoreText.setText("   Score: " + board.getScore());

      //refresh grid after key input and score update
      board.refreshGrid();

      //clear the grid so no glitchy images
      boardPane.getChildren().clear();

      //loop to get new positions for pacman and ghosts and eaten dots
      for(int i = 0; i < dimension; i++) {
        for(int j = 0; j < dimension; j++) {
          tiles[i][j].setAppearance(board.getGrid()[i][j]);
          boardPane.add(tiles[i][j].getNode(), j, i);
        }
      }
    }


    /*
     * Name:      gameIsOver
     * Purpose:   Check if the game is over and show the gameover board.
     * Parameter: none
     * Return:    game over text when game is Over
     */
    private void gameIsOver() {
      gameOverPane = new GridPane();
      goTxtPane = new GridPane();

      //modify the game over text
      Text gameOverTxt = new Text();
      gameOverTxt.setText("Game Over!");
      gameOverTxt.setFill(Color.BLACK);
      gameOverTxt.setFont(Font.font("Times New Roman", FontWeight.BOLD, 45));
      goTxtPane.add(gameOverTxt, 0, 0);

      //set the color for gameover background
      gameOverPane.setStyle("-fx-background-color: rgb(119, 110, 101)");

      //align the text to the center
      goTxtPane.setAlignment(Pos.CENTER);

      //set the opacity for both panes
      goTxtPane.setOpacity(0.8);
      gameOverPane.setOpacity(0.7);

      //stack the panes on each other
      stack.getChildren().addAll(gameOverPane, goTxtPane);
    }
  } // End of Inner Class myKeyHandler.



  /*
   * Name:        Tile
   *
   * Purpose:     This class tile helps to make the tiles in the board
   *              presented using JavaFX. Whenever a tile is needed,
   *              the constructor taking one char parameter is called
   *              and create certain ImageView fit to the char representation
   *              of the tile.
   *
   *
   */
  private class Tile {

    private ImageView repr;   // This field is for the Rectangle of tile.

    /*
     * Constructor
     *
     * Purpose: to load the image to the stage
     * Parameter: char tileAppearance
     *
     */
    public Tile(char tileAppearance) {
      Image image;

      //upload image corresponding to the grid char
      if(tileAppearance == 'G') {
        image = new Image("image/blinky_left.png");
      }
      else if(tileAppearance == '*') {
        image = new Image("image/dot_uneaten.png");
      }
      else if(tileAppearance == 'P') {
        image = new Image("image/pacman_right.png");
      }
      else if(tileAppearance == ' '){
        image = new Image("image/dot_eaten.png");
      }
      else {
        image = new Image("image/pacman_dead");
      }

      //initialize image and set width and height
      this.repr = new ImageView(image);
      repr.setFitWidth(50);
      repr.setFitHeight(50);
    }

    /*
     * Name:      setAppearance
     * Purpose:   Setter method for tiles
     * Parameter: char tileAppearance
     * Return:    nothing
     */
    public void setAppearance(char tileAppearance) {
      Image image;

      if(tileAppearance == 'G') {
        image = new Image("image/blinky_left.png");
      }
      else if(tileAppearance == '*') {
        image = new Image("image/dot_uneaten.png");
      }
      else if(tileAppearance == 'P') {
        image = new Image("image/pacman_right.png");
      }
      else if(tileAppearance == ' '){
        image = new Image("image/dot_eaten.png");
      }
      else {
        image = new Image("image/pacman_dead.png");
      }

      //initialize image and set width and height
      this.repr = new ImageView(image);
      repr.setFitWidth(50);
      repr.setFitHeight(50);
    }

    /* Name: getNode()
     *
     * Purpose: getter method for ImageView
     * return: ImageView repr
     */
    public ImageView getNode() {
      return repr;
    }

  }  // End of Inner class Tile




  /** DO NOT EDIT BELOW */

  // The method used to process the command line arguments
  private void processArgs(String[] args)
  {
    String inputBoard = null;   // The filename for where to load the Board
    int boardSize = 0;          // The Size of the Board

    // Arguments must come in pairs
    if((args.length % 2) != 0)
    {
      printUsage();
      System.exit(-1);
    }

    // Process all the arguments
    for(int i = 0; i < args.length; i += 2)
    {
      if(args[i].equals("-i"))
      {   // We are processing the argument that specifies
        // the input file to be used to set the board
        inputBoard = args[i + 1];
      }
      else if(args[i].equals("-o"))
      {   // We are processing the argument that specifies
        // the output file to be used to save the board
        outputBoard = args[i + 1];
      }
      else if(args[i].equals("-s"))
      {   // We are processing the argument that specifies
        // the size of the Board
        boardSize = Integer.parseInt(args[i + 1]);
      }
      else
      {   // Incorrect Argument
        printUsage();
        System.exit(-1);
      }
    }

    // Set the default output file if none specified
    if(outputBoard == null)
      outputBoard = "Pac-Man.board";
    // Set the default Board size if none specified or less than 2
    if(boardSize < 3)
      boardSize = 10;

    // Initialize the Game Board
    try{
      if(inputBoard != null)
        board = new Board(inputBoard);
      else
        board = new Board(boardSize);
    }
    catch (Exception e)
    {
      System.out.println(e.getClass().getName() + " was thrown while creating a " +
          "Board from file " + inputBoard);
      System.out.println("Either your Board(String, Random) " +
          "Constructor is broken or the file isn't " +
          "formated correctly");
      System.exit(-1);
    }
  }

  // Print the Usage Message
  private static void printUsage()
  {
    System.out.println("GuiPacman");
    System.out.println("Usage:  GuiPacman [-i|o file ...]");
    System.out.println();
    System.out.println("  Command line arguments come in pairs of the form: <command> <argument>");
    System.out.println();
    System.out.println("  -i [file]  -> Specifies a Pacman board that should be loaded");
    System.out.println();
    System.out.println("  -o [file]  -> Specifies a file that should be used to save the Pac-Man board");
    System.out.println("                If none specified then the default \"Pac-Man.board\" file will be used");
    System.out.println("  -s [size]  -> Specifies the size of the Pac-Man board if an input file hasn't been");
    System.out.println("                specified.  If both -s and -i are used, then the size of the board");
    System.out.println("                will be determined by the input file. The default size is 10.");
  }
}
