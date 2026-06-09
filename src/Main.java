import java.nio.file.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;


public class Main {
    // GLOBAL STOP WORDS
    static Set<String> stopWords = new HashSet<>(Arrays.asList(
            "the", "is", "am", "are", "was", "were", "a", "an", "and", "or",
            "in", "on", "at", "to", "for", "of", "with", "as"
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

    // PHASE 6 - PRIORITY QUEUE

    static class Score {

        String name;
        double value;

        Score(String n, double v) {

            name = n;
            value = v;
        }
    }

    // GUI

    public static void gui() {

        JFrame frame = new JFrame("TEXTI-FOX");
        frame.setSize(1100, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.getContentPane().setBackground(
                new Color(240, 245, 255));

        frame.setLayout(new BorderLayout(15, 15));

        // TITLE

        JLabel title =
                new JLabel(
                        "TEXTI-FOX PLAGIARISM DETECTOR",
                        JLabel.CENTER
                );

        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        title.setForeground(
                new Color(25, 118, 210)
        );

        title.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 0, 10, 0
                )
        );

        frame.add(title, BorderLayout.NORTH);

        // CENTER PANEL

        JPanel centerPanel =
                new JPanel(
                        new GridLayout(1, 2, 15, 15)
                );

        centerPanel.setBackground(
                new Color(240, 245, 255)
        );

        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 20, 10, 20
                )
        );

        JTextPane t1 = new JTextPane();
        JTextPane t2 = new JTextPane();

        t1.setFont(new Font("Consolas", Font.PLAIN, 15));
        t2.setFont(new Font("Consolas", Font.PLAIN, 15));

        t1.setBackground(Color.WHITE);
        t2.setBackground(Color.WHITE);

        t1.setForeground(Color.BLACK);
        t2.setForeground(Color.BLACK);

        JScrollPane sp1 = new JScrollPane(t1);
        JScrollPane sp2 = new JScrollPane(t2);

        sp1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(
                        new Color(25, 118, 210)
                ),
                "Document 1",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(25, 118, 210)
        ));

        sp2.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(
                        new Color(25, 118, 210)
                ),
                "Document 2",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(25, 118, 210)
        ));

        centerPanel.add(sp1);
        centerPanel.add(sp2);

        frame.add(centerPanel, BorderLayout.CENTER);

        // SOUTH PANEL

        JPanel southPanel = new JPanel();

        southPanel.setLayout(
                new BoxLayout(
                        southPanel,
                        BoxLayout.Y_AXIS
                )
        );

        southPanel.setBackground(
                new Color(240, 245, 255)
        );

        southPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 20, 20, 20
                )
        );


        // BUTTON
        JButton btn =
                new JButton("CHECK PLAGIARISM");

        btn.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        btn.setBackground(
                new Color(25, 118, 210)
        );

        btn.setForeground(Color.WHITE);

        btn.setFocusPainted(false);

        btn.setMaximumSize(
                new Dimension(260, 45)
        );

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // RESULT LABEL

        JLabel result =
                new JLabel("Plagiarism Score: ");

        result.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        result.setForeground(
                new Color(25, 118, 210)
        );

        result.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );


        // OUTPUT REPORT
        JTextArea output = new JTextArea();

        output.setEditable(false);

        output.setFont(
                new Font(
                        "Consolas",
                        Font.PLAIN,
                        14
                )
        );

        output.setBackground(Color.WHITE);

        output.setForeground(
                new Color(40, 40, 40)
        );

        output.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        JScrollPane reportScroll =
                new JScrollPane(output);

        reportScroll.setPreferredSize(
                new Dimension(900, 220)
        );

        reportScroll.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(
                                new Color(25, 118, 210)
                        ),
                        "Analysis Report",
                        0,
                        0,
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                14
                        ),
                        new Color(25, 118, 210)
                )
        );


        // ADD COMPONENTS

        southPanel.add(btn);

        southPanel.add(
                Box.createRigidArea(
                        new Dimension(0, 15)
                )
        );

        southPanel.add(result);

        southPanel.add(
                Box.createRigidArea(
                        new Dimension(0, 15)
                )
        );

        southPanel.add(reportScroll);

        frame.add(southPanel, BorderLayout.SOUTH);


        // BUTTON ACTION
        btn.addActionListener(e -> {

            ArrayList<String> a =
                    preprocess(t1.getText());

            ArrayList<String> b =
                    preprocess(t2.getText());


            // PHASES
            double p1 = phase1(a, b);

            double p2 =
                    phase2(
                            nGram(a, 3),
                            nGram(b, 3)
                    );

            double p3 = phase3(a, b);

            double finalScore =
                    (p1 + p2 + p3) / 3;

            // TRIE
            Trie trie = new Trie();

            for (String w : a)
                trie.insert(w);

            int trieMatch =
                    trie.matchCount(b);

            // RESULT LABEL
            result.setText(
                    "Plagiarism Score: "
                            + String.format(
                            "%.2f",
                            finalScore
                    ) + "%"
            );

            // PRIORITY QUEUE
            PriorityQueue<Score> pq =
                    new PriorityQueue<>(
                            (x, y) ->
                                    Double.compare(
                                            y.value,
                                            x.value
                                    )
                    );

            pq.add(new Score("Phase 1 - HashSet", p1));
            pq.add(new Score("Phase 2 - NGram", p2));
            pq.add(new Score("Phase 3 - LCS", p3));

            // REPORT
            StringBuilder sb =
                    new StringBuilder();

            sb.append(
                    "============== TEXTI-FOX REPORT ==============\n\n"
            );

            sb.append(
                    "DOCUMENT STATISTICS\n"
            );

            sb.append(
                    "--------------------------------------------------\n"
            );

            sb.append("Document 1 Words : ")
                    .append(a.size())
                    .append("\n");

            sb.append("Document 2 Words : ")
                    .append(b.size())
                    .append("\n\n");
            sb.append(
                    "TRIE ANALYSIS\n"
            );
            sb.append(
                    "--------------------------------------------------\n"
            );

            sb.append("Trie Matched Words : ")
                    .append(trieMatch)
                    .append("\n\n");
            sb.append(
                    "PHASE RANKING (PriorityQueue)\n"
            );
            sb.append(
                    "--------------------------------------------------\n"
            );

            while (!pq.isEmpty()) {
                Score s = pq.poll();
                sb.append(s.name)
                        .append(" : ")
                        .append(
                                String.format(
                                        "%.2f",
                                        s.value
                                )
                        )
                        .append("%\n");
            }

            sb.append("\n");

            sb.append(
                    "GRAPH REPRESENTATION\n"
            );

            sb.append(
                    "--------------------------------------------------\n"
            );

            sb.append("[Document 1] ===== ")
                    .append(
                            String.format(
                                    "%.2f",
                                    finalScore
                            )
                    )
                    .append("% ===== [Document 2]\n");

            output.setText(sb.toString());
        });

        frame.setVisible(true);
    }

 // MAIN
    public static void main(String[] args) {
    gui();
    }
}