package jaid.os;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static jaid.os.AvailabilityUtil.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

class AvailabilityUtilTest {

    @Test
    public void testFindMostSuitableNode() {
        List<Node> nodes = List.of(
                new Node("Node1", List.of(new Capacity("CPU", 50), new Capacity("Disk", 500))),
                new Node("Node2", List.of(new Capacity("CPU", 20), new Capacity("Disk", 800))),
                new Node("Node3", List.of(new Capacity("CPU", 52), new Capacity("Disk", 300))),
                new Node("Node4", List.of(new Capacity("Memory", 64), new Capacity("Disk", 600)))
        );

        Capacity accommodatableCapacity = new Capacity("Disk", 600);
        Optional<Node> suitableNode = builder().build().findMostSuitableNode(nodes, accommodatableCapacity);
        assertThat(suitableNode).isPresent();
        // Node 2 has the most disk available
        assertThat(suitableNode.get().name()).isEqualTo("Node2");

        Capacity unaccommodatableCapacity = new Capacity("Disk", 1000);
        Optional<Node> unsuitableNode = builder().build().findMostSuitableNode(nodes, unaccommodatableCapacity);
        assertThat(unsuitableNode).isEmpty();

        // Node 3 has slightly more CPU but it is within the level, so tiebreak using Disk
        suitableNode = builder().build().findMostSuitableNode(nodes, new Capacity("CPU", 1));
        assertThat(suitableNode).isPresent();
        assertThat(suitableNode.get().name()).isEqualTo("Node1");

        // However if we use enough levels then it will be in a different level with no need to tiebreak
        suitableNode = builder().numLevels(100).build().findMostSuitableNode(nodes, new Capacity("CPU", 1));
        assertThat(suitableNode).isPresent();
        assertThat(suitableNode.get().name()).isEqualTo("Node3");
    }

    @Test
    public void testResourceAvailabilityWithDifferentNumLevels() {
        Capacity capacity1 = new Capacity("ResourceA", 50);
        Capacity capacity2 = new Capacity("ResourceA", 30);
        Node node = new Node("TestNode", Arrays.asList(capacity1, capacity2));
        for (double numLevels = 1; numLevels <= 10; numLevels++) {
            AvailabilityUtil util = AvailabilityUtil.builder().numLevels(numLevels).build();
            double availability = util.resourceAvailability(node, new Capacity("ResourceA", 30));
            double divisor = 100 / numLevels;
            double expectedForCapacity1 = (int)((50 + 30) / divisor);
            double expectedForCapacity2 = (int)((30 + 30) / divisor);
            double expected = Math.max(expectedForCapacity1, expectedForCapacity2);
            assertEquals("Failed at numLevels=" + numLevels, expected, availability, 0.01);
        }
    }
}