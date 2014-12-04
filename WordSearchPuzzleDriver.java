import java.util.Arrays;
import java.util.ArrayList;

public class WordSearchPuzzleDriver {
    public static void main(String[] args) {
        String[] words = {"galt", "steel", "rand", "train", "industry", "government"};
        ArrayList<String> wordsForPuzzle = new ArrayList<String>(Arrays.asList(words));

        WordSearchPuzzle p = new WordSearchPuzzle(wordsForPuzzle);
        p.generateWordSearchPuzzle();
        p.showWordSearchPuzzle();

        String file = "wlist.txt";
        WordSearchPuzzle x = new WordSearchPuzzle(file, 2, 3, 10);
        x.generateWordSearchPuzzle();
        x.showWordSearchPuzzle();
    }
}
