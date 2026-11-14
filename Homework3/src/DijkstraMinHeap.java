
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * CSCI 4330 - Homework Assignment 3
 * Name: Tyrone Darby
 * Problem 3: Dijkstra's Algorithm using a Min-Heap
 */
public class DijkstraMinHeap {

    private static final long INF = Long.MAX_VALUE;

    // Represents a directed weighted edge
    private static class Edge {
        String to;
        long weight;

        Edge(String to, long weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    // Represents an entry in the min-heap
    private static class Entry {
        String vertex;
        long distance;

        Entry(String vertex, long distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    // Represents the graph
    private static class Graph {
        Map<String, List<Edge>> adj = new HashMap<>();
        int edgeCount = 0;
    }

    // Read graph from adjacency-list file
    private static Graph loadGraph(String filename)
            throws IOException {

        Graph graph = new Graph();

        try (BufferedReader reader =
                     Files.newBufferedReader(Path.of(filename))) {

            String first = reader.readLine();

            if (first == null) {
                throw new IllegalArgumentException(
                        "Empty graph file.");
            }

            int numberOfVertices =
                    Integer.parseInt(first.trim());

            for (int i = 0; i < numberOfVertices; i++) {

                String line = reader.readLine();

                if (line == null) {
                    throw new IllegalArgumentException(
                            "Missing adjacency line " + (i + 1));
                }

                String[] tokens = line.trim().split("\\s+");

                if (tokens.length < 1 ||
                        tokens[0].isBlank() ||
                        tokens.length % 2 == 0) {

                    throw new IllegalArgumentException(
                            "Invalid adjacency line: " + line);
                }

                String vertex = tokens[0];

                if (graph.adj.containsKey(vertex)) {
                    throw new IllegalArgumentException(
                            "Duplicate vertex: " + vertex);
                }

                List<Edge> edges = new ArrayList<>();

                for (int j = 1; j < tokens.length; j += 2) {

                    String neighbor = tokens[j];

                    long weight =
                            Long.parseLong(tokens[j + 1]);

                    if (weight < 0) {
                        throw new IllegalArgumentException(
                                "Negative weight at " + vertex);
                    }

                    edges.add(new Edge(neighbor, weight));
                    graph.edgeCount++;
                }

                graph.adj.put(vertex, edges);
            }
        }

        // Validate destination vertices
        for (Map.Entry<String, List<Edge>> item :
                graph.adj.entrySet()) {

            for (Edge edge : item.getValue()) {

                if (!graph.adj.containsKey(edge.to)) {
                    throw new IllegalArgumentException(
                            "Unknown destination: " + edge.to);
                }
            }
        }

        return graph;
    }

    // Dijkstra's algorithm using a min-heap
    private static void runDijkstra(
            Graph graph, String source, String target) {

        Map<String, Long> dist = new HashMap<>();

        Map<String, String> predecessor = new HashMap<>();

        Map<String, Boolean> visited = new HashMap<>();

        // Min-heap: distance first, alphabetical order second
        PriorityQueue<Entry> queue =
                new PriorityQueue<>(
                        Comparator
                                .comparingLong(
                                        (Entry e) -> e.distance)
                                .thenComparing(e -> e.vertex));

        // Initialize all vertices
        for (String v : graph.adj.keySet()) {

            long d = v.equals(source) ? 0 : INF;

            dist.put(v, d);
            visited.put(v, false);

            queue.add(new Entry(v, d));
        }

        System.out.println(
                "Number of vertices: " + graph.adj.size());

        System.out.println(
                "Number of directed edges: " + graph.edgeCount);

        System.out.println("Source vertex: " + source);

        System.out.println(
                "Extraction order (vertex: distance):");

        // Main Dijkstra loop
        while (!queue.isEmpty()) {

            // EXTRACT-MIN
            Entry current = queue.poll();

            String u = current.vertex;

            // Skip visited vertices and stale entries
            if (visited.get(u) ||
                    current.distance != dist.get(u)) {
                continue;
            }

            visited.put(u, true);

            System.out.println(
                    u + ": " +
                            (current.distance == INF
                                    ? "INF"
                                    : current.distance));

            // Do not relax unreachable vertices
            if (current.distance == INF) {
                continue;
            }

            // RELAX all outgoing edges
            for (Edge edge : graph.adj.get(u)) {

                if (visited.get(edge.to)) {
                    continue;
                }

                // Prevent long integer overflow
                long candidate =
                        current.distance > INF - edge.weight
                                ? INF
                                : current.distance + edge.weight;

                if (candidate < dist.get(edge.to)) {

                    // Update shortest distance
                    dist.put(edge.to, candidate);

                    // Update predecessor
                    predecessor.put(edge.to, u);

                    // Add updated priority to min-heap
                    queue.add(
                            new Entry(edge.to, candidate));
                }
            }
        }

        // Display shortest-path results
        System.out.println("Target vertex: " + target);

        if (dist.get(target) == INF) {

            System.out.println("Reachable: No");

            System.out.println(
                    "No path exists from " +
                            source + " to " + target + ".");

        } else {

            List<String> path = new ArrayList<>();

            // Reconstruct shortest path
            for (String at = target;
                 at != null;
                 at = predecessor.get(at)) {

                path.add(at);
            }

            Collections.reverse(path);

            System.out.println("Reachable: Yes");

            System.out.println(
                    "Shortest path: " +
                            String.join(" -> ", path));

            System.out.println(
                    "Path length: " + dist.get(target));
        }
    }

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            String filename;

            if (args.length > 0) {
                filename = args[0];
            } else {

                System.out.print(
                        "Enter adjacency-list filename: ");

                filename = scanner.nextLine().trim();
            }

            Graph graph = loadGraph(filename);

            String source = "s";

            if (!graph.adj.containsKey(source)) {
                throw new IllegalArgumentException(
                        "Source vertex 's' not found.");
            }

            System.out.print("Enter target vertex: ");

            String target = scanner.nextLine().trim();

            if (!graph.adj.containsKey(target)) {
                throw new IllegalArgumentException(
                        "Target vertex not found: " + target);
            }

            runDijkstra(graph, source, target);

        } catch (IOException | IllegalArgumentException ex) {

            System.err.println(
                    "Error: " + ex.getMessage());
        }
    }
}

