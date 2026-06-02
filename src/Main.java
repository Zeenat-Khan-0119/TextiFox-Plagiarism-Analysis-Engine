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


    // PHASE 2 - NGRAM + QUEUE + HASHING

    public static ArrayList<String> nGram(ArrayList<String> tokens, int n) {

        Queue<String> q = new LinkedList<>();
        ArrayList<String> result = new ArrayList<>();

        for (String t : tokens) {
            q.add(t);
            if (q.size() == n) {
                StringBuilder sb = new StringBuilder();
                for (String x : q) {
                    sb.append(x).append(" ");
                }
                result.add(sb.toString().trim());
                q.poll();
            }
        }
        return result;
    }

    public static double phase2(ArrayList<String> a, ArrayList<String> b) {

        HashSet<Integer> h1 = new HashSet<>();
        HashSet<Integer> h2 = new HashSet<>();

        for (String s : a)
            h1.add(s.hashCode());

        for (String s : b)
            h2.add(s.hashCode());

        int match = 0;

        for (int x : h1) {
            if (h2.contains(x))
                match++;
        }
        return (2.0 * match / (h1.size() + h2.size())) * 100;
    }

    // PHASE 3 - LCS (RECURSION + DP)

    public static int lcs(ArrayList<String> a,
                          ArrayList<String> b,
                          int i,
                          int j,
                          int[][] dp) {

        if (i == 0 || j == 0)
            return 0;

        if (dp[i][j] != -1)
            return dp[i][j];

        if (a.get(i - 1).equals(b.get(j - 1))) {

            return dp[i][j] =
                    1 + lcs(a, b, i - 1, j - 1, dp);
        }

        return dp[i][j] = Math.max(
                lcs(a, b, i - 1, j, dp),
                lcs(a, b, i, j - 1, dp)
        );
    }

    public static double phase3(ArrayList<String> a,
                                ArrayList<String> b) {

        int[][] dp = new int[a.size() + 1][b.size() + 1];

        for (int[] row : dp)
            Arrays.fill(row, -1);

        int res = lcs(a, b, a.size(), b.size(), dp);

        return (2.0 * res / (a.size() + b.size())) * 100;
    }

    // PHASE 4 - TRIE

    static class Node {

        HashMap<Character, Node> child = new HashMap<>();
        boolean end = false;
    }

    static class Trie {

        Node root = new Node();

        void insert(String word) {

            Node cur = root;

            for (char c : word.toCharArray()) {

                cur.child.putIfAbsent(c, new Node());
                cur = cur.child.get(c);
            }

            cur.end = true;
        }

        boolean search(String word) {

            Node cur = root;

            for (char c : word.toCharArray()) {

                if (!cur.child.containsKey(c))
                    return false;

                cur = cur.child.get(c);
            }

            return cur.end;
        }

        int matchCount(ArrayList<String> list) {

            int count = 0;

            for (String w : list) {

                if (search(w))
                    count++;
            }

            return count;
        }
    }


    // MAIN
    public static void main(String[] args) {
    }
}