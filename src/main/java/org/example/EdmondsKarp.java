/*
 w2051896
 20231206
 Vineth Arandarage
 */

package org.example;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EdmondsKarp {
    private final FlowNetwork network;
    private int maxFlow;
    private long operationCount;
    private final String fileName;
    private final List<String> stepLog;
    private static final int MAX_PATHS_TO_PRINT = 20;
    private static final int MAX_NODES_TO_PRINT = 10;
    private static final int SMALL_GRAPH_THRESHOLD = 1000;

    public EdmondsKarp(FlowNetwork network, String fileName) {
        this.network = network;
        this.fileName = fileName;
        this.maxFlow = 0;
        this.operationCount = 0;
        this.stepLog = new ArrayList<>();
        computeMaxFlow();
    }

    private void computeMaxFlow() {
        long startTime = System.currentTimeMillis();
        int source = 0;
        int sink = network.getNumNodes() - 1;
        int n = network.getNumNodes();
        int iteration = 0;

        stepLog.add("Processing: benchmarks/" + fileName);
        while (true) {
            int[] parent = new int[n];
            FlowNetwork.Edge[] edgeTo = new FlowNetwork.Edge[n];
            Arrays.fill(parent, -1);

            Queue<Integer> queue = new LinkedList<>();
            queue.offer(source);
            parent[source] = source;
            operationCount++; // Queue operation

            while (!queue.isEmpty() && parent[sink] == -1) {
                int u = queue.poll();
                for (FlowNetwork.Edge e : network.getEdges(u)) {
                    int v = e.getTo();
                    if (parent[v] == -1 && e.getResidualCapacity() > 0) {
                        parent[v] = u;
                        edgeTo[v] = e;
                        queue.offer(v);
                        operationCount++; // Queue operation
                    }
                    operationCount++; // Edge traversal
                }
            }

            if (parent[sink] == -1) break;

            int bottleneck = Integer.MAX_VALUE;
            List<Integer> path = new ArrayList<>();
            int v = sink;
            while (v != source) {
                path.add(0, v);
                bottleneck = Math.min(bottleneck, edgeTo[v].getResidualCapacity());
                v = parent[v];
            }
            path.add(0, source);

            v = sink;
            while (v != source) {
                edgeTo[v].addFlow(bottleneck);
                v = parent[v];
            }

            maxFlow += bottleneck;
            iteration++;
            StringBuilder pathStr = new StringBuilder("Iteration " + iteration + ": Path ");
            for (int i = 0; i < path.size(); i++) {
                pathStr.append(path.get(i));
                if (i < path.size() - 1) pathStr.append(" -> ");
            }
            pathStr.append(", Flow: ").append(bottleneck);
            stepLog.add(pathStr.toString());
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;


        if (n > SMALL_GRAPH_THRESHOLD) {
            stepLog.add("Total Operations: " + operationCount);
            stepLog.add("Execution Time: " + executionTime + " ms");
        }

        stepLog.add("FLOW assignments:");
        List<FlowNetwork.Edge> allEdges = new ArrayList<>();
        for (FlowNetwork.Edge e : network.getAllEdges()) {
            if (e.getCapacity() > 0) {
                allEdges.add(e);
            }
        }
        allEdges.sort((e1, e2) -> {
            if (e1.getFrom() != e2.getFrom()) return Integer.compare(e1.getFrom(), e2.getFrom());
            return Integer.compare(e1.getTo(), e2.getTo());
        });

        stepLog.add("Total forward edges: " + allEdges.size());
        for (FlowNetwork.Edge e : allEdges) {
            stepLog.add("f(" + e.getFrom() + "," + e.getTo() + ") = " + e.getFlow() + ", capacity = " + e.getCapacity());
        }

        for (String line : stepLog) {
            System.out.println(line);
        }
        System.out.println("\nMaximum FLOW: " + maxFlow);
        System.out.println("Execution Time: " + executionTime + " ms");
    }

    public int getMaxFlow() {
        return maxFlow;
    }

    public String getSteps() {
        return String.join("\n", stepLog);
    }
}


