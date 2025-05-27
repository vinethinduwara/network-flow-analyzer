/*
 w2051896
 20231206
 Vineth Arandarage
 */
package org.example;
import java.util.ArrayList;
import java.util.List;

public class FlowNetwork {
    private final int numNodes;
    private final List<List<Edge>> adjList;
    private final List<Edge> allEdges;

    public static class Edge {
        private final int from;
        private final int to;
        private final int capacity;
        private int flow;
        private Edge reverse;

        public Edge(int from, int to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = 0;
        }

        public int getFrom() { return from; }
        public int getTo() { return to; }
        public int getCapacity() { return capacity; }
        public int getFlow() { return flow; }
        public int getResidualCapacity() { return capacity - flow; }
        public void setReverse(Edge reverse) { this.reverse = reverse; }
        public void addFlow(int delta) {
            flow += delta;
            reverse.flow -= delta;
        }
    }

    public FlowNetwork(int n) {
        this.numNodes = n;
        this.adjList = new ArrayList<>(n);
        this.allEdges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int capacity) {
        Edge forward = new Edge(u, v, capacity);
        Edge backward = new Edge(v, u, 0);
        forward.setReverse(backward);
        backward.setReverse(forward);
        adjList.get(u).add(forward);
        adjList.get(v).add(backward);
        allEdges.add(forward);
        allEdges.add(backward);
    }

    public boolean removeEdge(int u, int v) {
        List<Edge> edges = adjList.get(u);
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            if (e.getTo() == v) {
                Edge reverse = e.reverse;
                adjList.get(v).remove(reverse);
                edges.remove(i);
                allEdges.remove(e);
                allEdges.remove(reverse);
                return true;
            }
        }
        return false;
    }

    public Edge findEdge(int u, int v) {
        for (Edge e : adjList.get(u)) {
            if (e.getTo() == v) {
                return e;
            }
        }
        return null;
    }

    public List<Edge> getEdges(int v) {
        return adjList.get(v);
    }

    public int getNumNodes() {
        return numNodes;
    }

    public List<Edge> getAllEdges() {
        return allEdges;
    }
}
