package jaid.collection;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class BoundedPriorityQueue {
    private final PriorityQueue<Candidate> queue;
    private final int k;

    public BoundedPriorityQueue(int k) {
        // Min-heap that will remove the candidate with the smallest dot product when needed.
        this.queue = new PriorityQueue<>(Comparator.comparingDouble(c -> c.dotProduct));
        this.k = k;
    }

    public void add(IVector vector, double dotProduct) {
        if (queue.size() < k) {
            queue.offer(new Candidate(vector, dotProduct));
        } else if (dotProduct > queue.peek().dotProduct) {
            queue.poll();
            queue.offer(new Candidate(vector, dotProduct));
        }
    }

    public List<IVector> toList() {
        return queue.stream().map(c -> c.vector).collect(Collectors.toList());
    }

    public int size() {
        return queue.size();
    }

    private static class Candidate {
        final IVector vector;
        final double dotProduct;

        Candidate(IVector vector, double dotProduct) {
            this.vector = vector;
            this.dotProduct = dotProduct;
        }
    }
}
