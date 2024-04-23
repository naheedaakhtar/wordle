import java.awt.Color;
import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;

//Handles the backend logic for the JWordle game, evaluating player guesses and maintaining
//the state of the game
public class JWordleLogic{
   //public static void main(String[] args ){

   
  
   //Number of words in the provided words.txt file
   private static final int WORDS_IN_FILE = 5758;
   
   //Use for generating random numbers!
   private static final Random rand = new Random();
   
   //Dimensions of the game grid in the game window
   public static final int MAX_ROWS = 6;
   public static final int MAX_COLS = 5;
   
   //Character codes for the enter and backspace key press
   public static final char ENTER_KEY = KeyEvent.VK_ENTER;
   public static final char BACKSPACE_KEY = KeyEvent.VK_BACK_SPACE;
   
   //The null character value (used to represent an "empty" value for a spot on the game grid)
   public static final char NULL_CHAR = 0;
   
   //Various Color Values
   private static final Color CORRECT_COLOR = new Color(53, 209, 42); //(Green)
   private static final Color WRONG_PLACE_COLOR = new Color(235, 216, 52); //(Yellow)
   private static final Color WRONG_COLOR = Color.DARK_GRAY; //(Dark Gray [obviously])
   private static final Color DEFAULT_KEYBOARD_COLOR = new Color(160, 163, 168); //(Light Gray)
   
   //Name of file containing all the five letter words
   private static final String WORDS_FILENAME = "words.txt";
   
   //Secret word used when the game is running in debug mode
   private static final char[] DEBUG_SECRET_WORD = {'B', 'A', 'N', 'A', 'L'};       //change back to Shire
   
   
   //...Feel free to add more final variables of your own!
          
   
   
   
   
   //******************   NON-FINAL GLOBAL VARIABLES   ******************
   //********  YOU CANNOT ADD ANY ADDITIONAL NON-FINAL GLOBALS!  ******** 
   
   
   //Array storing all words read out of the file
   private static String[] words;
   
   //The current row/col where the user left off typing
   private static int currentRow, currentCol;
      
   
   //*******************************************************************
   
   
   
   
   
  

/// METHOD TO CHECK IF WORD IN FILE
   public static boolean validWord(char[] guess){
      for (int i=0; i<words.length;i++){
         char[] testWord= words[i].toUpperCase().toCharArray();
         if (Arrays.equals(guess,testWord)){  
            return true;
         }
         
      }
      return false;
   }

// METHOD RETURNS THE SECRET RANDOM WORD
   public static char[] initGame() { 
      // Warm Up Code
      /* 
      JWordleGUI.setGridLetter(0,0,'C');
      JWordleGUI.setGridColor(0,0,CORRECT_COLOR);
      JWordleGUI.setGridLetter(1,3,'O');
      JWordleGUI.setGridColor(1,3,WRONG_COLOR);
      JWordleGUI.setGridLetter(3,4,'S');
      JWordleGUI.setGridColor(3,4,DEFAULT_KEYBOARD_COLOR);
      JWordleGUI.setGridLetter(5,4,'C');
      JWordleGUI.setGridColor(5,4,WRONG_PLACE_COLOR);
      JWordleGUI.wiggleGrid(3);
      */
      try{
         Scanner sc = new Scanner(new File(WORDS_FILENAME));
         words= new String[WORDS_IN_FILE]; 
         int i=0;
         while(sc.hasNextLine()){
            words[i]=sc.nextLine();
            i+=1;
         }
         sc.close();
      } catch (IOException e){
         e.printStackTrace();
      }
      
      String random= (words[rand.nextInt(0,words.length)]);
      return random.toCharArray();  
   }

   
    // METHOD TO HANDLE ALL KEYS PRESSED
   public static void keyPressed(char key) { 
      if ((key==ENTER_KEY) && (currentCol<MAX_COLS)){
         JWordleGUI.wiggleGrid(currentRow);
      }
      if ((key==ENTER_KEY) && (validWord(input())==false)){  
         JWordleGUI.wiggleGrid(currentRow);
      }
      else if(key==ENTER_KEY){ 
         makeitColor();
         gameState();
         if (currentRow!=5){
            currentRow+=1;
            currentCol-=4;
            JWordleGUI.setGridLetter(currentRow,currentCol,NULL_CHAR);
            currentCol-=1;
         }
      }
      else if(key==BACKSPACE_KEY){
         if (currentCol==0){
            return;
         }
         currentCol-=1;
         JWordleGUI.setGridLetter(currentRow,currentCol,NULL_CHAR);
         }
      else {    //IF STATEMENT CONTROLS FOR INVALID KEYBOARD INPUTS & MORE THAN 5 INPUTS
         if ((currentCol>MAX_COLS-1) || !(((int)key>=65) && (int)key<=90)) {
            return;
         }
         JWordleGUI.setGridLetter(currentRow,currentCol,key);
         currentCol+=1;
      }
   System.out.println("keyPressed called! key (int value) = '" + ((int)key) + "'");
}

//METHOD STORES THE GUESS
public static char[] input(){
   char[] guess= new char[5];
   for (int i=0;i<currentCol;i++){
      char currentChar=JWordleGUI.getGridLetter(currentRow,i);
      guess[i]=currentChar;
   }
   System.out.println(guess);
   return guess;
}

//COLORS THE CELLS AND KEYBOARD CORRECTLY
public static int[] correctColor(){
   char[] secret= JWordleGUI.getSecretWord();
   char[] guess= input();
   int[] colorsArray= new int[5];
   for (int i=0;i<secret.length;i++){
      if (guess[i]==secret[i]){
         colorsArray[i]=1;
         guess[i]=NULL_CHAR;
      }
   }
   for (int i=0; i<secret.length;i++){
      if (guess[i]!=NULL_CHAR){
         for (int j=0;j<secret.length;j++){
            if ((guess[i]==secret[j] && colorsArray[j]==0)){
               colorsArray[i]=2;
               colorsArray[j]=3;
            }
         }
      }
   }
   return colorsArray;
   
}
//METHOD SETS THE COLOR FOR KEY/CELLS
public static void makeitColor(){
   int[] color= correctColor();
   for (int i=0;i<currentCol;i++){ 
      char currentChar=JWordleGUI.getGridLetter(currentRow, i);
      if (color[i]==1){  
         JWordleGUI.setGridColor(currentRow,i,CORRECT_COLOR); 
         JWordleGUI.setKeyColor(currentChar, CORRECT_COLOR);
      }
         else if (color[i]==2){
            JWordleGUI.setGridColor(currentRow,i,WRONG_PLACE_COLOR);
            if (JWordleGUI.getKeyColor(currentChar)!=CORRECT_COLOR){
               JWordleGUI.setKeyColor(currentChar, WRONG_PLACE_COLOR);
         }
      }
         else{
            JWordleGUI.setGridColor(currentRow,i,WRONG_COLOR);
            if ((JWordleGUI.getKeyColor(currentChar)!=CORRECT_COLOR) && (JWordleGUI.getKeyColor(currentChar)!=WRONG_PLACE_COLOR)){
               JWordleGUI.setKeyColor(currentChar,WRONG_COLOR);
            }
   }
   }
}
// METHOD ENDS THE GAME WITH WIN/LOSS
public static void gameState(){
   char[] guess= input();
   System.out.println(JWordleGUI.getSecretWord());
   if (Arrays.equals(guess,JWordleGUI.getSecretWord())){
      JWordleGUI.endGame(true);
      //System.out.print("hi");
   }
   else if ((currentRow==MAX_ROWS-1) && (!Arrays.equals(guess,JWordleGUI.getSecretWord()))){
      JWordleGUI.endGame(false);
   }
}

} //LEAVE FOR CLASS 
