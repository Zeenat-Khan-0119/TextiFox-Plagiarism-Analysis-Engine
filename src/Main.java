import java.nio.file.*;
import java.util.*;

public class Main {
    // GLOBAL STOP WORDS
    static Set<String> stopWords = new HashSet<>(Arrays.asList(
            "the","is","am","are","was","were","a","an","and","or",
            "in","on","at","to","for","of","with","as"
    ));

    // FILE READER
    public static String readFile(String path) {

        String data = "";

        try {
            data = new String(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            System.out.println("Error reading file: " + path);
        }

        return data;
    }

    // PREPROCESSING
    public static ArrayList<String> preprocess(String text) {

        text = text.toLowerCase();
        text = text.replaceAll("[^a-zA-Z ]", "");

        String[] words = text.split("\\s+");

        ArrayList<String> list = new ArrayList<>();

        for (String w : words) {

            if (w.length() > 0 && !stopWords.contains(w)) {
                list.add(w);
            }
        }
        return list;
    }
    // PHASE 1 - HASHSET COMPARISON
    public static double phase1(ArrayList<String> a, ArrayList<String> b) {

        HashSet<String> set1 = new HashSet<>(a);
        HashSet<String> set2 = new HashSet<>(b);
        int count = 0;
        for (String s : set1) {
            if (set2.contains(s)) {
                count++;
            }
        }
        return (2.0 * count / (set1.size() + set2.size())) * 100;
    }

    // MAIN
    public static void main(String[] args) {
    }
}