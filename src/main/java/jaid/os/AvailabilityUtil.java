package jaid.os;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Finds the best available node from a list, given a resource that is needed and present in some quantity on each.
 * A potential improvement would be to use resource bands, since we are usually interested in secondary resources more
 * than a tiny difference in the primary.
 */
public class AvailabilityUtil {

    public static Optional<Node> findMostSuitableNode(final List<Node> nodes, final Resource newResource) {
        return nodes.stream()
                .filter(node -> canAccommodateResource(node, newResource))
                .max(Comparator.comparing((Node node) -> resourceAvailability(node, newResource.type()))
                        .thenComparing(AvailabilityUtil::sumOtherResourceAvailability));
    }

    private static boolean canAccommodateResource(final Node node, final Resource targetResource) {
        return node.resources().stream().anyMatch(nodeResource ->
                nodeResource.type().equals(targetResource.type()) && nodeResource.amount() >= targetResource.amount());
    }

    private static double resourceAvailability(final Node node, final String resourceType) {
        return node.resources().stream()
                .filter(resource -> resource.type().equals(resourceType))
                .mapToDouble(Resource::amount)
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