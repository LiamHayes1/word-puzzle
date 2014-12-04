import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class WordSearchPuzzle {
    private char[][] puzzle ;
    private List<String> puzzleWords ;

	/**
	 * I have made the decision to use the ArrayList implementation of the the 
	 * List interface to store the puzzle words. For the most part, I will be 
	 * performing the get operation on the List, this operation is faster for 
	 * ArrayLists O(1) compared to LinkedLists O(n).
	 */

    public WordSearchPuzzle(List<String> userSpecifiedWords) {
    	/*
    	 * Ensure that the userSpecified words list does not just contain empty strings
    	 * Covert the userSpecifiedWords list into a HashSet to remove duplicates
    	 * Then remove empty strings if present, then check the size
    	 */
    	
    	HashSet<String> words = new HashSet<String>(userSpecifiedWords);
        words.remove("");

        if (words.size() == 0) {
            System.out.println("Please enter a valid list of words...");
        }
        else {
            puzzleWords = new ArrayList<String>(userSpecifiedWords);
            double sizeInDouble = Math.ceil(Math.sqrt((calculateLengthOfWord(puzzleWords) * 5)));
            int size = (int) (sizeInDouble);
            
            size = checkIfTheDimensionIsOk(size); //Ensures that the dimension of array will fit the longest word
            
            puzzle = new char[size][size];
        }
    }

    public WordSearchPuzzle(String wordFile, int wordCount, int shortest, int longest) {
        if (wordCount < 1) {
            System.out.println("Your word puzzle (wordCount) must contain atleast 1 word, please try again...");
        }
        else if (shortest < 1 || longest < 1) {
            System.out.println("You must enter values greater than 0 for shortest and longest, please try again...");
        }
        else {
            puzzleWords = new ArrayList<String>(); //gets assigned words in the getRandomWords method
            File file = new File(wordFile);

            if (file.exists()) {
                importFromFile(wordFile);

                if (shortest > longest) {
                    int temp = shortest;
                    shortest = longest;
                    longest = temp;
                }

                getRandomWords(wordCount, shortest, longest);

                double sizeInDouble = Math.ceil(Math.sqrt((calculateLengthOfWord(puzzleWords) * 5)));
                int size = (int) (sizeInDouble);
                
                size = checkIfTheDimensionIsOk(size); //Ensures that the dimension of array will fit the longest word
                
                puzzle = new char[size][size]; //Set up the 2D array with the correct size
            } else {
                System.out.println("File Not Found!\nPlease start again...");
            }
        }
    }
    
    private int checkIfTheDimensionIsOk(int size) {
    	int longestWordLength = 0;
    	
    	for (String s : puzzleWords) {
    		if (s.length() > longestWordLength) {
    			longestWordLength = s.length();
    		}
    	}
    	
    	if (size <= longestWordLength) {
    		return longestWordLength + 1;
    	} else {
    		return size;
    	}
    }

    private void importFromFile(String wordFile) {
        try {
            FileReader fr = new FileReader(wordFile);
            BufferedReader br = new BufferedReader(fr);

            String lineFromFile = br.readLine();
            while (lineFromFile != null) {
                if (!puzzleWords.contains(lineFromFile)) {
                    puzzleWords.add(lineFromFile);
                }
                lineFromFile = br.readLine();
            }

            br.close();
            fr.close();
        } catch (IOException ex) {
            System.out.println("Error getting data from file... \nPlease start again...");
            System.exit(0);
        }
    }

    private void getRandomWords(int wordCount, int shortest, int longest) {
        ArrayList<String> wordsForGame = new ArrayList<String>();

        while (wordCount > 0) { //greater than zero
            int randomWordIndex = (int) (Math.random() * puzzleWords.size());
            String word = puzzleWords.get(randomWordIndex);
            if (!wordsForGame.contains(word) && word.length() >= shortest && word.length() <= longest) {
                wordsForGame.add(word);
                wordCount--;
            }
        }

        puzzleWords = new ArrayList<String>(wordsForGame); //create a deep copy of wordsForGame and assign the reference to puzzleWords
    }

    private int calculateLengthOfWord(List<String> input) {
        int lengthOfWords = 0;

        for (String s : input) {
            lengthOfWords += s.length();
        }
        return lengthOfWords;
    }

    public List<String> getWordSearchList() {
        return puzzleWords;
    }

    public char[][] getPuzzleAsGrid() {
            //returns the generated grid as a two-dimensional array of characters
            return puzzle;
    }

    private boolean checkOkToProceed() {
        /**
         * This method ensures that the puzzleWords list and puzzle char array have been created
         * in memory and not equal to null. It also ensures that puzzleWords list is not empty
         * before performing any operations on it.
         */

        if (puzzleWords != null && !puzzleWords.isEmpty() && puzzle != null)
            return true;

        return false;
    }

    public String getPuzzleAsString() {
        if(checkOkToProceed()) {
            String output = Arrays.deepToString(puzzle).replace("], [", "\n");
            return output.substring(2, output.length() - 2).replace(",", "");
        }
        return "";
    }

    public void showWordSearchPuzzle() {
        if (checkOkToProceed()) {
            //displays the grid and list of words on the screen
            String display = getPuzzleAsString() + "\n";

            //Sort by natural order
            Collections.sort(puzzleWords);

            for (String s : puzzleWords) {
                display += s + "\n";
            }

            System.out.println(display);
        }
    }

    public void generateWordSearchPuzzle() {
        if (checkOkToProceed()) {
            List<String> words = new ArrayList<String>(puzzleWords);

            /* Order by anonymous comparator --
             * Based on word length with the shortest words at the start of the list
             * I will work from the end of the LinkList, inserting the longest words into
             * the charArray first, then will remove that word from the words ArrayList
             */
            Collections.sort(words, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    if (s1.length() != s2.length()) {
                        return s1.length() - s2.length();
                    }
                    return s1.compareTo(s2); //use natural order if same length
                }
            });
            
            while (!words.isEmpty()) {
                String word = words.get(words.size() - 1);

                int randomRow = (int) ((Math.random() * puzzle.length));
                int randomCol = (int) ((Math.random() * puzzle[0].length));
                int maxRow = randomRow + word.length();
                int minRow = randomRow - word.length();
                int maxCol = randomCol + word.length();
                int minCol = randomCol - word.length();
                String direction = generateRandomDirection();
                boolean hasWordPlaced = false;

                if (direction.equals("right")) {
                    if (maxCol < puzzle.length) { //its a valid word to put on the grid
                        if (isFreeSpace(randomRow, randomCol, direction, word)) { //check no other word in the way
                            for (int i = 0; i < word.length(); i++) {
                                puzzle[randomRow][randomCol + i] = word.charAt(i);
                            }
                            hasWordPlaced = true;
                        }
                    }
                } else if (direction.equals("down")) {
                    if (maxRow < puzzle.length) { //its a valid word to put on the grid
                        if (isFreeSpace(randomRow, randomCol, direction, word)) {
                            for (int i = 0; i < word.length(); i++) {
                                puzzle[randomRow + i][randomCol] = word.charAt(i);
                            }
                            hasWordPlaced = true;
                        }
                    }
                } else if (direction.equals("left")) {
                    if (minCol > 0) { //its a valid word to put on the grid
                        if (isFreeSpace(randomRow, randomCol, direction, word)) {
                            for (int i = word.length() - 1; i >= 0; i--) {
                                puzzle[randomRow][randomCol - i] = word.charAt(i);
                            }
                            hasWordPlaced = true;
                        }
                    }
                } else if (direction.equals("up")) {
                    if (minRow > 0) { //its a valid word to put on the grid
                        if (isFreeSpace(randomRow, randomCol, direction, word)) {
                            for (int i = word.length() - 1; i >= 0; i--) {
                                puzzle[randomRow - i][randomCol] = word.charAt(i);
                            }
                            hasWordPlaced = true;
                        }
                    }
                }
                if (hasWordPlaced) { //The word has been successfully placed, so remove from list
                    words.remove(words.size() - 1);
                }
            }
            placeRandomLettersOnPuzzle();
        }
    }

    private boolean isFreeSpace(int randomRow, int randomCol, String direction, String word) {
        int i;

        //char arrays are assigned the value of \u0000 by default, so we will check this
        if (direction.equals("right")) {
            for (i = 0; i < word.length() && (puzzle[randomRow][randomCol + i] == '\u0000'); i++);
            if (i == word.length())	return true;
        } else if (direction.equals("down")) {
            for (i = 0; i < word.length() && (puzzle[randomRow + i][randomCol] == '\u0000'); i++);
            if (i == word.length())	return true;
        } else if (direction.equals("left")) {
            for (i = 0; i < word.length() && (puzzle[randomRow][randomCol - i] == '\u0000'); i++);
            if (i == word.length())	return true;
        } else if (direction.equals("up")) {
            for (i = 0; i < word.length() && (puzzle[randomRow - i][randomCol] == '\u0000'); i++);
            if (i == word.length())	return true;
        }
        return false;
    }

    private String generateRandomDirection() {
        String[] directions = {"left", "right", "up", "down"};
        int randomPosition = (int) (Math.random() * directions.length);

        return directions[randomPosition];
    }

    private void placeRandomLettersOnPuzzle() {
        String letters = "abcdefghijklmnopqrstuvwxyz";

        for (int row = 0; row < puzzle.length; row++) {
            for (int col = 0; col < puzzle[0].length; col++) {
                if (puzzle[row][col] == '\u0000') {
                    int pos = (int) (Math.random() * letters.length());
                    puzzle[row][col] = letters.charAt(pos);
                }
            }
        }
    }
}
