package jaid.os;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static jaid.os.AvailabilityUtil.findMostSuitableNode;
import static org.assertj.core.api.Assertions.assertThat;

class AvailabilityUtilTest {

    @Test
    public void testFindMostSuitableNode() {
        List<Node> nodes = List.of(
                new Node("Node1", List.of(new Resource("CPU", 50), new Resource("Disk", 500))),
                new Node("Node2", List.of(new Resource("CPU", 20), new Resource("Disk", 800))),
                new Node("Node3", List.of(new Resource("CPU", 50), new Resource("Disk", 300))),
                new Node("Node4", List.of(new Resource("Memory", 64), new Resource("Disk", 600)))
        );

        Resource accommodatableResource = new Resource("Disk", 600);
        Optional<Node> suitableNode = findMostSuitableNode(nodes, accommodatableResource);
        assertThat(suitableNode).isPresent();
        // Node 2 has the most disk available
        assertThat(suitableNode.get().name()).isEqualTo("Node2");

        Resource unaccommodatableResource = new Resource("Disk", 1000);
        Optional<Node> unsuitableNode = findMostSuitableNode(nodes, unaccommodatableResource);
        assertThat(unsuitableNode).isEmpty();
    }
}