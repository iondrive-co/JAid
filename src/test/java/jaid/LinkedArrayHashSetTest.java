package jaid;

import jaid.collection.LinkedArrayHashSet;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test of {@link LinkedArrayHashSet}
 */
@RunWith(JUnit4.class)
public class LinkedArrayHashSetTest extends TestCase {

    LinkedArrayHashSet<Object> linkedArrayHashSet;
    List<Object> allTestElements;
    static final NamedObject O1 = new NamedObject("O1");
    static final NamedObject O2 = new NamedObject("O2");
    static final NamedObject O3 = new NamedObject("O3");
    static final NamedObject O4 = new NamedObject("O4");

    @Before
    public void initObjects() {
        linkedArrayHashSet = new LinkedArrayHashSet<>();
        allTestElements = new ArrayList<>(){{add(O1); add(O2);add(O3); add(O4);}};
    }

    @Test
    public void addElement() {
        checkSizeIs(0);
        assertThat(linkedArrayHashSet.add(O1)).isTrue();
        checkSizeIs(1);
        checkOnlyElementContainedAt(0, O1);
    }

    @Test
    public void addElements() {
        assertThat(linkedArrayHashSet.add(O1)).isTrue();
        assertThat(linkedArrayHashSet.add(O2)).isTrue();
        checkSizeIs(2);
        checkElementsOnlyContainedAt(rangeClosedOpen(0, 2));
    }

    @Test
    public void addManyElements() {
        final List<Integer> zeroToShortMax = rangeClosedOpen(0, 16384);
        for (final Integer i: zeroToShortMax) {
            assertThat(linkedArrayHashSet.add(i)).isTrue();
            assertThat(linkedArrayHashSet.get(i)).isEqualTo(i);
        }
        checkSizeIs(16384);
        checkElementsContained(zeroToShortMax);
    }

    @Test
    public void addAllElements() {
        assertThat(linkedArrayHashSet.addAll(allTestElements)).isTrue();
        checkSizeIs(allTestElements.size());
        checkElementsOnlyContainedAt(rangeClosedOpen(0, allTestElements.size()));
    }

    @Test
    public void addElementAt() {
        assertThat(linkedArrayHashSet.add(O1)).isTrue();
        linkedArrayHashSet.add(1, O2);
        checkSizeIs(2);
        checkElementsOnlyContainedAt(rangeClosedOpen(0, 2));

        linkedArrayHashSet.add(1, O3);
        checkSizeIs(3);
        checkElementContainedAt(1, O3);

        linkedArrayHashSet.add(0, O4);
        checkSizeIs(4);
        checkElementContainedAt(0, O4);

        try {
            linkedArrayHashSet.add(5, new NamedObject("5"));
            fail("Should have thrown an Exception");
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void setElement() {
        try {
            linkedArrayHashSet.set(0, O1);
            fail("Should have thrown an Exception");
        } catch (IndexOutOfBoundsException ignored) {
        }
        linkedArrayHashSet.add(O1);
        linkedArrayHashSet.set(0, O2);
        checkSizeIs(1);
        checkOnlyElementContainedAt(0, O2);
    }

    @Test
    public void setElements() {
        linkedArrayHashSet.add(O1);
        linkedArrayHashSet.set(0, O3);
        checkSizeIs(1);
        checkOnlyElementContainedAt(0, O3);
        linkedArrayHashSet.add(O1);
        checkSizeIs(2);
        checkElementContainedAt(0, O3);
        checkElementContainedAt(1, O1);
        linkedArrayHashSet.set(1, O4);
        checkSizeIs(2);
        checkElementContainedAt(0, O3);
        checkElementContainedAt(1, O4);
    }

    @Test
    public void removeElement() {
        linkedArrayHashSet.add(O1);
        assertThat(linkedArrayHashSet.remove(O1)).isTrue();
        checkSizeIs(0);
        checkElementsNotContained(O1);
        assertThat(linkedArrayHashSet.remove(O1)).isFalse();
    }

    @Test
    public void removeElements() {
        linkedArrayHashSet.addAll(Arrays.asList(O1, O2, O3, O4));
        linkedArrayHashSet.remove(O2);
        checkSizeIs(3);
        checkElementsNotContained(O2);
        checkElementsContained(Arrays.asList(O1, O3, O4));
        checkElementContainedAt(0, O1);
        checkElementContainedAt(1, O3);
        checkElementContainedAt(2, O4);

        linkedArrayHashSet.remove(O1);
        checkSizeIs(2);
        checkElementsNotContained(O1);
        checkElementsContained(Arrays.asList(O3, O4));

        linkedArrayHashSet.remove(O3);
        checkSizeIs(1);
        checkElementsNotContained(O3);
        checkElementsContained(List.of(O4));

        linkedArrayHashSet.remove(O4);
        checkSizeIs(0);
        checkElementsNotContained(O4);
    }

    @Test
    public void removeFromManyElements() {
        final int[] testSizes = new int[]{256, 4095, 4138};
        for (final int size: testSizes) {
            System.out.println("Testing " + size);
            final List<Integer> zeroTo41 = rangeClosedOpen(0, 42);
            for (final Object i: zeroTo41) {
                assertThat(linkedArrayHashSet.add(i)).isTrue();
            }
            assertThat(linkedArrayHashSet.add(42)).isTrue();
            final List<Integer> fortyThreeToSize = rangeClosedOpen(43, size + 1);
            for (final Object i: fortyThreeToSize) {
                assertThat(linkedArrayHashSet.add(i)).isTrue();
            }

            checkElementsContained(zeroTo41);
            checkElementsContained(List.of(42));
            checkElementsContained(fortyThreeToSize);
            checkSizeIs(size + 1);
            assertThat(linkedArrayHashSet.remove(42)).isEqualTo(42);
            checkSizeIs(size);
            checkElementsContained(zeroTo41);
            checkElementsNotContained(42);
            checkElementsContained(fortyThreeToSize);
            linkedArrayHashSet.clear();
        }
    }

    @Test
    public void removeAllElements() {
        linkedArrayHashSet.addAll(Arrays.asList(O1, O2, O3, O4));
        linkedArrayHashSet.removeAll(List.of(O3));
        checkSizeIs(3);
        checkElementsNotContained(O3);
        linkedArrayHashSet.removeAll(Arrays.asList(O2, O4));
        checkSizeIs(1);
        checkElementsNotContained(O2, O4);
    }

    @Test
    public void clear() {
        linkedArrayHashSet.addAll(Arrays.asList(O1, O2, O3, O4));
        linkedArrayHashSet.clear();
        checkElementsNotContained(O1, O2, O3, O4);
    }

    @Test
    public void startSublist() {
        linkedArrayHashSet.addAll(Arrays.asList(O1, O2, O3));
        List<Object> subList = linkedArrayHashSet.subList(0, 2);
        checkSizeIs(2, subList);
        checkElementsNotContained(subList, O3, O4);
        checkElementContainedAt(0, O1, subList);
        checkElementContainedAt(1, O2, subList);
        //Sublists behaviour is undefined when the backing list is structurally
        // modified, but still need to check some basics of our implementation
        // Additions do not affect the sublist
        linkedArrayHashSet.add(O4);
        checkSizeIs(2, subList);
        checkElementsNotContained(subList, O3, O4);
        checkElementContainedAt(0, O1, subList);
        checkElementContainedAt(1, O2, subList);
        // Removals outside the range do not affect the sublist
        linkedArrayHashSet.remove(O3);
        checkSizeIs(2, subList);
        checkElementsNotContained(subList, O3, O4);
        checkElementContainedAt(0, O1, subList);
        checkElementContainedAt(1, O2, subList);
        // Removals inside the range can affect the sublist, check it
        // still has the 02 element in it even if its size is wrong
        linkedArrayHashSet.remove(O1);
        checkElementsContained(List.of(O2), subList);
    }

    @Test
    public void endSublist() {
        linkedArrayHashSet.addAll(Arrays.asList(O1, O2, O3));
        List<Object> subList = linkedArrayHashSet.subList(1, 3); // List(02, 03)
        checkSizeIs(2, subList);
        checkElementsNotContained(subList, O1, O4);
        checkElementContainedAt(0, O2, subList);
        checkElementContainedAt(1, O3, subList);
        // Sublists behvaiour is undefined when the backing list is structurally
        // modified, but still need to check some basics of our implementation
        // Additions do not affect the sublist
        linkedArrayHashSet.add(O4);
        checkSizeIs(2, subList);
        checkElementsNotContained(subList, O1, O4);
        checkElementContainedAt(0, O2, subList);
        checkElementContainedAt(1, O3, subList);
        // Removals inside the range can affect the sublist, check it
        // still has the 02 element in it even if its size is wrong
        linkedArrayHashSet.remove(O2);
        checkElementsContained(List.of(O3), subList);
    }

    @Test
    public void invalidSublist() {
      // TODO
    }

    private void checkSizeIs(final int expectedSize) {
        checkSizeIs(expectedSize, linkedArrayHashSet);
    }
    private void checkSizeIs(final int expectedSize, final List<Object> toTest) {
        assertThat(toTest.isEmpty()).isEqualTo(expectedSize <= 0);
        assertThat(toTest).hasSize(expectedSize);
        assertThat(toTest.toArray()).hasSize(expectedSize);
        assertThat(toTest.iterator().hasNext()).isEqualTo((expectedSize > 0));
        assertThat(toTest.listIterator().hasNext()).isEqualTo(expectedSize > 0);
        assertThat(toTest.listIterator(expectedSize).hasNext()).isFalse();
        assertThat(toTest.subList(0, expectedSize).isEmpty()).isEqualTo(expectedSize <= 0);
        assertThat(toTest.subList(0, expectedSize)).hasSize(expectedSize);
    }

    private void checkElementContainedAt(final int expectedPosition, final Object element) {
        checkElementContainedAt(expectedPosition, element, linkedArrayHashSet);
    }

    private void checkElementContainedAt(final int expectedPosition, final Object element, final List<Object> target) {
        checkElementsContained(Collections.singletonList(element), target);
        assertThat(target.get(expectedPosition)).isEqualTo(element);
        assertThat(target.indexOf(element)).isEqualTo(expectedPosition);
        assertThat(target.lastIndexOf(element)).isEqualTo(expectedPosition);
        assertThat(target.toArray()[expectedPosition]).isEqualTo(element);
        assertThat(target.subList(expectedPosition, expectedPosition + 1).get(0)).isEqualTo(element);
    }

    private void checkOnlyElementContainedAt(final int expectedPosition, final Object element) {
        checkOnlyElementContainedAt(expectedPosition, element, linkedArrayHashSet);
    }

    private void checkOnlyElementContainedAt(final int expectedPosition, final Object element, final List<Object> target) {
        checkElementContainedAt(expectedPosition, element, target);
        for (final Object item: target) {
            assertThat(item).isEqualTo(element);
        }
    }

    private void checkElementsOnlyContainedAt(final List<Integer> expectedPositions) {
        final Collection<Object> containedElements = new ArrayList<>(linkedArrayHashSet.size());
        for (final Integer expectedPosition: expectedPositions) {
            Object containedElement = allTestElements.get(expectedPosition);
            checkElementContainedAt(expectedPosition, containedElement);
            containedElements.add(containedElement);
        }
        for (final Object item: linkedArrayHashSet) {
            assertThat(containedElements).contains(item);
        }
    }

    private void checkElementsContained(final Collection<?> elements) {
        checkElementsContained(elements, linkedArrayHashSet);
    }

    private void checkElementsContained(final Collection<?> elements, final List<Object> target) {
        for (final Object element: elements) {
            assertThat(target.contains(element)).as(element.toString()).isTrue();
            assertThat(target.toArray()).contains(element);
            assertThat(target.toArray(new Object[0])).contains(element);
            assertThat(target.subList(0, target.size()).contains(element)).isTrue();
        }
        assertThat(target.containsAll(elements)).isTrue();
        assertThat(target.subList(0, target.size()).containsAll(elements)).isTrue();
    }

    private void checkElementsNotContained(final Object... elemItems) {
        checkElementsNotContained(linkedArrayHashSet, elemItems);
    }

    private void checkElementsNotContained(final List<Object> target, final Object... elemItems) {
        final Collection<Object> elements = Arrays.asList(elemItems);
        for (final Object element: elements) {
            assertThat(target.contains(element)).isFalse();
            assertThat(target).doesNotContain(element);
            assertThat(target.toArray()).doesNotContain(element);
            assertThat(target.subList(0, target.size()).contains(element)).isFalse();
        }
        assertThat(target.containsAll(elements)).isFalse();
        assertThat(target.subList(0, target.size()).containsAll(elements)).isFalse();
    }

    // Includes begin, does not include end
    private List<Integer> rangeClosedOpen(final int begin, final int end) {
        return IntStream.range(begin, end).boxed().collect(toList());
    }
}
