/*
 w2051896
 20231206
 Vineth Arandarage
 */

package org.example;
import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter the input file name or 'exit' to quit:");
            String fileName = scanner.nextLine();

            if (fileName.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }

            try {
                File file = new File("benchmarks/" + fileName);
                if (!file.exists()) {
                    throw new IllegalArgumentException("File not found: " + file.getPath());
                }
                Scanner fileScanner = new Scanner(file);

                if (!fileScanner.hasNextInt()) {
                    throw new IllegalArgumentException("Invalid file format: Expected number of nodes.");
                }
                int n = fileScanner.nextInt();
                if (n <= 1) {
                    throw new IllegalArgumentException("Number of nodes must be at least 2.");
                }
                FlowNetwork network = new FlowNetwork(n);

                // Detect indexing style from the first edge
                boolean isOneBased = false;
                if (fileScanner.hasNextInt()) {
                    int firstNode = fileScanner.nextInt();
                    isOneBased = (firstNode == 1); // If first node is 1, assume 1-based indexing
                    fileScanner = new Scanner(file); // Reset scanner
                    fileScanner.nextInt(); // Skip n again
                }

                int lineNumber = 1;
                boolean hasSourceEdge = false;
                while (fileScanner.hasNext()) {
                    if (!fileScanner.hasNextInt()) {
                        throw new IllegalArgumentException("Invalid format at line " + (lineNumber + 1) + ": Expected integer for node index.");
                    }
                    int u = fileScanner.nextInt();
                    if (!fileScanner.hasNextInt()) {
                        throw new IllegalArgumentException("Invalid format at line " + (lineNumber + 1) + ": Expected target node index.");
                    }
                    int v = fileScanner.nextInt();
                    if (!fileScanner.hasNextInt()) {
                        throw new IllegalArgumentException("Invalid format at line " + (lineNumber + 1) + ": Expected capacity.");
                    }
                    int capacity = fileScanner.nextInt();
                    lineNumber++;

                    // Convert to 0-based if needed
                    if (isOneBased) {
                        u--;
                        v--;
                    }

                    if (u < 0 || u >= n || v < 0 || v >= n) {
                        throw new IllegalArgumentException("Invalid node index at line " + lineNumber + ": u=" + (isOneBased ? u+1 : u) + ", v=" + (isOneBased ? v+1 : v) + 
                            " (must be " + (isOneBased ? "1" : "0") + " to " + (isOneBased ? n : n-1) + ").");
                    }
                    if (capacity < 0) {
                        throw new IllegalArgumentException("Negative capacity at line " + lineNumber + ": " + capacity);
                    }
                    if (u == v) {
                        throw new IllegalArgumentException("Self-loop detected at line " + lineNumber + ": u=" + (isOneBased ? u+1 : u) + ", v=" + (isOneBased ? v+1 : v));
                    }

                    network.addEdge(u, v, capacity);
                    if (u == 0) hasSourceEdge = true;
                }
                fileScanner.close();

                // Optional: Warn if source has no outgoing edges
                if (!hasSourceEdge) {
                    System.err.println("Warning: Source node (" + (isOneBased ? "1" : "0") + ") has no outgoing edges. Maximum flow will be 0.");
                }

                EdmondsKarp ek = new EdmondsKarp(network, fileName);
                System.out.println("File processed successfully.");
                break;

            } catch (IllegalArgumentException e) {
                System.err.println("Parsing error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        }

        scanner.close();
    }
}



