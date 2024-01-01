package jaid.os;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Finds the best available node from a list, given a capacity of some resource that is needed and present on each.
 * Best is defined as being in {@link #numLevels level} with the highest availability of that resource (once
 * the new usage is taken into account), and having the most availability of other resources of nodes in that band.
 */
public class AvailabilityUtil {

    /**
     * Split the usage of the desired resource into this many levels. Usages on the same level will be considered
     * equal, and will be tie-broken by using availability of other resources.
     */
    private final double numLevels;

    private AvailabilityUtil(double numLevels) {
        this.numLevels = numLevels;
    }

    public Optional<Node> findMostSuitableNode(final List<Node> nodes, final Capacity newCapacity) {
        return nodes.stream()
                .filter(node -> canAccommodateCapacity(node, newCapacity))
                .max(Comparator.comparing((Node node) -> resourceAvailability(node, newCapacity))
                        .thenComparing(AvailabilityUtil::sumOtherResourceAvailability));
    }

    private static boolean canAccommodateCapacity(final Node node, final Capacity targetCapacity) {
        return node.capacities().stream().anyMatch(nodeCapacity ->
                nodeCapacity.resource().equals(targetCapacity.resource()) && nodeCapacity.level() >= targetCapacity.level());
    }

    double resourceAvailability(final Node node, final Capacity newCapacity) {
        return node.capacities().stream()
                .filter(capacity -> capacity.resource().equals(newCapacity.resource()))
                .mapToDouble(capacity -> (int) ((capacity.level() + newCapacity.level()) / (100 / numLevels)))
                .max()
                .orElse(0);
    }

    /**
     * To tiebreak, find the node with the most availability of other resources
     */
    private static double sumOtherResourceAvailability(final Node node) {
        return node.capacities().stream()
                .mapToDouble(Capacity::level)
                .max()
                .orElse(0);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private double numLevels = 20;

        public Builder numLevels(double numLevels) {
            this.numLevels = numLevels;
            return this;
        }

        public AvailabilityUtil build() {
            return new AvailabilityUtil(numLevels);
        }
    }
}

record Capacity(String resource, double level) {}

record Node(String name, List<Capacity> capacities) {}