package splitter.domain;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class BalanceReducer {
    private Map<String, List<BalanceSummary>> graphList;
    private ArrayList<BalanceSummary> order;
    private Set<String> visited;

    private static String getAnotherNode(BalanceSummary edge, String node) {
        return edge.getPerson1().equals(node) ? edge.getPerson2() : edge.getPerson1();
    }

    public List<BalanceSummary> reduceTransaction(List<BalanceSummary> edges) {
        convertToGraph(edges);
        dfs();
        return edges;
    }

    private void convertToGraph(List<BalanceSummary> edges) {
        graphList = new HashMap<>();
        for (BalanceSummary edge : edges) {
            graphList.putIfAbsent(edge.getPerson1(), new ArrayList<>());
            graphList.putIfAbsent(edge.getPerson2(), new ArrayList<>());

            graphList.get(edge.getPerson1()).add(edge);
            graphList.get(edge.getPerson2()).add(edge);
        }
    }

    private void dfs() {
        visited = new HashSet<>();
        for (String node : graphList.keySet()) {
            order = new ArrayList<>();
            visit(node, null);
        }
    }

    private void visit(String node, String parent) {
        if (visited.contains(node)) {
            reduce(node);
            return;
        }
        visited.add(node);

        for (BalanceSummary edge : graphList.get(node)) {
            if (edge.getAmount().signum() == 0) continue;

            String anotherNode = getAnotherNode(edge, node);
            if (anotherNode.equals(parent)) continue;

            order.add(edge);

            visit(anotherNode, node);

            order.remove(order.size() - 1);
        }


    }

    private void reduce(String node) {
        if(order.isEmpty()) return;
        BalanceSummary edge = order.get(order.size() - 1);

        BigDecimal amount = edge.getPerson1().equals(node) ? edge.getAmount() : edge.getAmount().negate();

        String cycleNode = node;

        for (int i=order.size()-1; i>=0; i--) {
            BalanceSummary cycleEdge = order.get(i);
            cycleNode = getAnotherNode(cycleEdge, cycleNode);
            cycleEdge.setAmount(cycleEdge.getPerson1().equals(cycleNode)
                    ? cycleEdge.getAmount().add(amount)
                    : cycleEdge.getAmount().subtract(amount));
            if (cycleNode.equals(node)) break;
        }
    }
}
