// Name: Tyrone Darby
// Class: CSCI 4330

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DynamicProgrammingLCS {

    private static class Result {
        int[][] table;
        int length;
        String answer;

        Result(int[][] table, int length, String answer) {
            this.table = table;
            this.length = length;
            this.answer = answer;
        }
    }

    // Longest Common Subsequence
    private static Result lcs(String x, String y) {
        int m = x.length();
        int n = y.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (x.charAt(i - 1) == y.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(
                            dp[i - 1][j], dp[i][j - 1]
                    );
                }
            }
        }

        // Reconstruct LCS
        StringBuilder reversed = new StringBuilder();
        int i = m;
        int j = n;

        while (i > 0 && j > 0) {
            if (x.charAt(i - 1) == y.charAt(j - 1)) {
                reversed.append(x.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return new Result(
                dp, dp[m][n], reversed.reverse().toString()
        );
    }

    // Longest Common Substring
    private static Result lcSubstring(String x, String y) {
        int m = x.length();
        int n = y.length();
        int[][] dp = new int[m + 1][n + 1];

        int bestLength = 0;
        int endInX = 0;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (x.charAt(i - 1) == y.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;

                    if (dp[i][j] > bestLength) {
                        bestLength = dp[i][j];
                        endInX = i;
                    }
                } else {
                    dp[i][j] = 0;
                }
            }
        }

        // Reconstruct longest common substring
        String answer = x.substring(
                endInX - bestLength, endInX
        );

        return new Result(dp, bestLength, answer);
    }

    // Display complete DP table
    private static void printTable(
            int[][] dp, String x, String y) {

        System.out.printf("%5s%5s", "", "-");

        for (int j = 0; j < y.length(); j++) {
            System.out.printf("%5s", y.charAt(j));
        }

        System.out.println();

        for (int i = 0; i <= x.length(); i++) {
            String rowLabel = (i == 0)
                    ? "-"
                    : String.valueOf(x.charAt(i - 1));

            System.out.printf("%5s", rowLabel);

            for (int j = 0; j <= y.length(); j++) {
                System.out.printf("%5d", dp[i][j]);
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        String fileName = args.length > 0
                ? args[0] : "LCS_Test.txt";

        try (Scanner input = new Scanner(
                new File(fileName))) {

            int testCases = Integer.parseInt(
                    input.nextLine().trim()
            );

            for (int test = 1; test <= testCases; test++) {

                if (!input.hasNextLine()) {
                    throw new IllegalArgumentException(
                            "Missing X for test " + test
                    );
                }

                String x = input.nextLine();

                if (!input.hasNextLine()) {
                    throw new IllegalArgumentException(
                            "Missing Y for test " + test
                    );
                }

                String y = input.nextLine();

                Result subsequence = lcs(x, y);
                Result substring = lcSubstring(x, y);

                System.out.println(
                        "\n========== TEST CASE " +
                                test + " =========="
                );

                System.out.println(
                        "X (length " + x.length() + "): " + x
                );

                System.out.println(
                        "Y (length " + y.length() + "): " + y
                );

                // Display tables only if both lengths <= 20
                if (x.length() <= 20 && y.length() <= 20) {

                    System.out.println("\nLCS DP Table:");
                    printTable(subsequence.table, x, y);

                    System.out.println("\nLCSubstr DP Table:");
                    printTable(substring.table, x, y);

                } else {
                    System.out.println(
                            "\nDP tables computed but not displayed."
                    );
                }

                System.out.println(
                        "\nLCS length: " + subsequence.length
                );

                System.out.println(
                        "LCS answer: \"" +
                                subsequence.answer + "\""
                );

                System.out.println(
                        "LCSubstr length: " + substring.length
                );

                System.out.println(
                        "LCSubstr answer: \"" +
                                substring.answer + "\""
                );
            }

        } catch (FileNotFoundException e) {
            System.err.println(
                    "Could not find " + fileName
            );

        } catch (IllegalArgumentException e) {
            System.err.println(
                    "Invalid input: " + e.getMessage()
            );
        }
    }
}
