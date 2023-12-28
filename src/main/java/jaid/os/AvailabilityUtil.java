package jaid.os;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Finds the best available node from a list, given a resource that is needed and present in some quantity on each.
 * Best is defined as being in the BAND_PERCENTAGE nodes that would have the highest availability of that resource once
 * the new usage is taken into account, and having the most availability of other resources of nodes in that band.
 */
public class AvailabilityUtil {

    private static final double BAND_PERCENTAGE = 5.0;

    public static Optional<Node> findMostSuitableNode(final List<Node> nodes, final Resource newResource) {
        return nodes.stream()
                .filter(node -> canAccommodateResource(node, newResource))
                .max(Comparator.comparing((Node node) -> resourceAvailability(node, newResource))
                        .thenComparing(AvailabilityUtil::sumOtherResourceAvailability));
    }

    private static boolean canAccommodateResource(final Node node, final Resource targetResource) {
        return node.resources().stream().anyMatch(nodeResource ->
                nodeResource.type().equals(targetResource.type()) && nodeResource.amount() >= targetResource.amount());
    }

    private static double resourceAvailability(final Node node, final Resource newResource) {
        return node.resources().stream()
                .filter(resource -> resource.type().equals(newResource.type()))
                .mapToDouble(resource -> (int) ((resource.amount() + newResource.amount()) / BAND_PERCENTAGE))
                .max()
                .orElse(0);
    }

    /**
     * To tiebreak, find the node with the most availability of other resources
     */
    private static double sumOtherResourceAvailability(final Node node) {
        return node.resources().stream()
                .mapToDouble(Resource::amount)
                .max()
                .orElse(0);
    }
}

/**
 * The amount is the amount of the resource available for a Node, or the amount required for request
 */
record Resource(String type, double amount) {}

record Node(String name, List<Resource> resources) {}