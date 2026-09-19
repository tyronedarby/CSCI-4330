// ========================================================================
// CSCI 4330 Algorithm Design and Analysis
// Author: Tyrone Darby
// Homework 2
// ========================================================================

import java.util.ArrayList;
import java.util.Random;

public class Quicksort<E extends Comparable<E>> {

    // Counters for Hoare-Quicksort
    public static long hoareKC = 0;
    public static long hoareSwaps = 0;

    // Counters for Lomuto-Quicksort
    public static long lomutoKC = 0;
    public static long lomutoSwaps = 0;

    // Quicksort using Hoare's partition
    public static <E extends Comparable<E>> void quicksortHoare(ArrayList<E> list, int l, int r) {
        if (l < r) {
            int p = partitionHoare(list, l, r);
            quicksortHoare(list, l, p);
            quicksortHoare(list, p + 1, r);
        }
    }

    // Quicksort using Lomuto's partition
    public static <E extends Comparable<E>> void quicksortLomuto(ArrayList<E> list, int l, int r) {
        if (l < r) {
            int p = partitionLomuto(list, l, r);
            quicksortLomuto(list, l, p - 1);
            quicksortLomuto(list, p + 1, r);
        }
    }

    // Hoare's partition (first element as pivot)
    public static <E extends Comparable<E>> int partitionHoare(ArrayList<E> list, int l, int r) {
        E x = list.get(l);
        int i = l - 1;
        int j = r + 1;

        while (true) {
            do {
                j--;
                hoareKC++;
            } while (list.get(j).compareTo(x) > 0);

            do {
                i++;
                hoareKC++;
            } while (list.get(i).compareTo(x) < 0);

            if (i < j) {
                hoareSwaps++;
                E temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            } else {
                return j;
            }
        }
    }

    // Lomuto's partition (last element as pivot)
    public static <E extends Comparable<E>> int partitionLomuto(ArrayList<E> list, int l, int r) {
        E x = list.get(r);
        int i = l - 1;

        for (int j = l; j <= r - 1; j++) {
            lomutoKC++;
            if (list.get(j).compareTo(x) <= 0) {
                i++;
                lomutoSwaps++;
                E temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }

        lomutoSwaps++;
        E temp = list.get(i + 1);
        list.set(i + 1, list.get(r));
        list.set(r, temp);

        return i + 1;
    }

    // Helper method to print the first 20 elements
    public static <E> void printFirst20(ArrayList<E> list) {
        int limit = Math.min(20, list.size());
        System.out.print("[");
        for (int i = 0; i < limit; i++) {
            System.out.print(list.get(i) + (i < limit - 1 ? ", " : ""));
        }
        System.out.println(list.size() > 20 ? ", ...]" : "]");
    }

    public static void runExperiment(String desc, ArrayList<Integer> original) {
        System.out.println("==================================================");
        System.out.println("Test Case: " + desc + " | Size: " + original.size());
        System.out.println("==================================================");

        // Hoare Run
        ArrayList<Integer> hoareList = new ArrayList<>(original);
        hoareKC = 0;
        hoareSwaps = 0;
        quicksortHoare(hoareList, 0, hoareList.size() - 1);
        System.out.print("Hoare Sorted (First 20): ");
        printFirst20(hoareList);
        System.out.println("Hoare KCs:   " + hoareKC);
        System.out.println("Hoare Swaps: " + hoareSwaps);

        // Lomuto Run
        ArrayList<Integer> lomutoList = new ArrayList<>(original);
        lomutoKC = 0;
        lomutoSwaps = 0;
        quicksortLomuto(lomutoList, 0, lomutoList.size() - 1);
        System.out.print("Lomuto Sorted (First 20): ");
        printFirst20(lomutoList);
        System.out.println("Lomuto KCs:   " + lomutoKC);
        System.out.println("Lomuto Swaps: " + lomutoSwaps);
        System.out.println();
    }

    public static void main(String[] args) {
        int[] sizes = {100, 1000, 10000};
        Random rng = new Random(42); // fixed seed for reproducibility

        for (int size : sizes) {
            // (1) Distinct numbers in ascending order
            ArrayList<Integer> asc = new ArrayList<>(size);
            for (int i = 1; i <= size; i++) asc.add(i);
            runExperiment("(1) Ascending Order", asc);

            // (2) Distinct numbers in descending order
            ArrayList<Integer> desc = new ArrayList<>(size);
            for (int i = size; i >= 1; i--) desc.add(i);
            runExperiment("(2) Descending Order", desc);

            // (3) All the same number
            ArrayList<Integer> same = new ArrayList<>(size);
            for (int i = 0; i < size; i++) same.add(42);
            runExperiment("(3) All Same Number", same);

            // (4) Random numbers between 1 and 100,000
            ArrayList<Integer> rand = new ArrayList<>(size);
            for (int i = 0; i < size; i++) rand.add(rng.nextInt(100000) + 1);
            runExperiment("(4) Random Numbers", rand);
        }
    }
}