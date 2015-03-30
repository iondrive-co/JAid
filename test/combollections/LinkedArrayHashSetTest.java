package combollections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.not;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;

/**
 */
@RunWith(JUnit4.class)
public class LinkedArrayHashSetTest extends TestCase {

    LinkedArrayHashSet linkedArrayHashSet;
    List<Object> allTestElements;
    static final Object O1 = newObjectNamed("O1");
    static final Object O2 = newObjectNamed("O2");
    static final Object O3 = newObjectNamed("O3");
    static final Object O4 = newObjectNamed("O4");

    @Before
    public void initObjects() {
        linkedArrayHashSet = new LinkedArrayHashSet();
        allTestElements = new ArrayList<Object>(){{add(O1); add(O2);add(O3); add(O4);}};
    }

    private static Object newObjectNamed(final String name) {
        return new Object() {
            @Override
            public String toString() {
                return name;
            }
        };
    }

    @Test
    public void addElement() {
        checkSizeIs(0);
        assertThat(linkedArrayHashSet.add(O1), is(true));
        checkSizeIs(1);
        checkOnlyElementContainedAt(0, O1);
    }

    @Test
    public void addElements() {
        assertThat(linkedArrayHashSet.add(O1), is(true));
        assertThat(linkedArrayHashSet.add(O2), is(true));
        checkSizeIs(2);
        checkElementsOnlyContainedAt(rangeClosedOpen(0, 2));
    }

    @Test
    public void addManyElements() {
        final Collection<Object> zeroToShortMax = (Collection)rangeClosedOpen(0, Short.MAX_VALUE);
        for (Object i: zeroToShortMax) {
            assertThat(linkedArrayHashSet.add(i), is(true));
        }
        checkSizeIs(Short.MAX_VALUE);
        // TODO contained at?
        checkElementsContained(zeroToShortMax);
    }

    @Test
    public void addAllElements() {
        assertThat(linkedArrayHashSet.addAll(allTestElements), is(true));
        checkSizeIs(allTestElements.size());
        checkElementsOnlyContainedAt(rangeClosedOpen(0, allTestElements.size()));
    }

    @Test
    public void addElementAt() {
        assertThat(linkedArrayHashSet.add(O1), is(true));
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
            linkedArrayHashSet.add(5, new Object());
            fail("Should have thrown an Exception");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void setElement() {
        try {
            linkedArrayHashSet.set(0, O1);
            fail("Should have thrown an Exception");
        } catch (IndexOutOfBoundsException e) {
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
        assertThat(linkedArrayHashSet.remove(O1), is(true));
        checkSizeIs(0);
        checkElementsNotContained(O1);
        assertThat(linkedArrayHashSet.remove(O1), is(false));
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
        checkElementsContained(Arrays.asList(O4));

        linkedArrayHashSet.remove(O4);
        checkSizeIs(0);
        checkElementsNotContained(O4);
    }

    @Test
    public void removeFromManyElements() {
        final Collection<Object> zeroTo41 = (Collection)rangeClosedOpen(0, 42);
        final Collection<Object> fourtyThreeToShortMax = (Collection)rangeClosedOpen(43, Short.MAX_VALUE + 1);
        Collection<Object> zeroToShortMax = new ArrayList(Short.MAX_VALUE) {{
            addAll(zeroTo41);
            add(42);
            addAll(fourtyThreeToShortMax);
        }};
        for (Object i: zeroToShortMax) {
            assertThat(linkedArrayHashSet.add(i), is(true));
        }

        checkElementsContained(zeroToShortMax);
        linkedArrayHashSet.remove(42);
        checkElementsContained(zeroTo41);
        checkElementsContained(fourtyThreeToShortMax);
        checkElementsNotContained(42);
        checkSizeIs(Short.MAX_VALUE );
    }

    @Test
    public void removeAllElements() {
        linkedArrayHashSet.addAll(Arrays.asList(O1, O2, O3, O4));
        linkedArrayHashSet.removeAll(Arrays.asList(O3));
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
    //--- Sublists behaviour is undefined when the backing list is structurally
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
        checkElementsContained(Arrays.asList(O2), subList);
    //---
    }

    @Test
    public void endSublist() {
        linkedArrayHashSet.addAll(Arrays.asList(O1, O2, O3));
        List<Object> subList = linkedArrayHashSet.subList(1, 3); // List(02, 03)
        checkSizeIs(2, subList);
        checkElementsNotContained(subList, O1, O4);
        checkElementContainedAt(0, O2, subList);
        checkElementContainedAt(1, O3, subList);
    //--- Sublists behvaiour is undefined when the backing list is structurally
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
        checkElementsContained(Arrays.asList(O3), subList);
    //---
    }

    @Test
    public void invalidSublist() {

    }

    private void checkSizeIs(int expectedSize) {
        checkSizeIs(expectedSize, linkedArrayHashSet);
    }
    private void checkSizeIs(int expectedSize, List<Object> toTest) {
        assertThat(toTest.isEmpty(), is(expectedSize <= 0));
        assertThat(toTest.size(), is(expectedSize));
        assertThat(toTest.toArray().length, is(expectedSize));
        assertThat(toTest.iterator().hasNext(), is(expectedSize > 0));
        assertThat(toTest.listIterator().hasNext(), is(expectedSize > 0));
        assertThat(toTest.listIterator(expectedSize).hasNext(), is(false));
        assertThat(toTest.subList(0, expectedSize).isEmpty(), is(expectedSize <= 0));
        assertThat(toTest.subList(0, expectedSize).size(), is(expectedSize));
    }

    private void checkElementContainedAt(int expectedPosition, Object element) {
        checkElementContainedAt(expectedPosition, element, linkedArrayHashSet);
    }

    private void checkElementContainedAt(int expectedPosition, Object element, List<Object> target) {
        checkElementsContained(Collections.singletonList(element), target);
        assertThat(target.get(expectedPosition), is(element));
        assertThat(target.indexOf(element), is(expectedPosition));
        assertThat(target.lastIndexOf(element), is(expectedPosition));
        assertThat(target.toArray()[expectedPosition], is(element));
        assertThat(target.subList(expectedPosition, expectedPosition + 1).get(0), is(element));
    }

    private void checkOnlyElementContainedAt(int expectedPosition, Object element) {
        checkOnlyElementContainedAt(expectedPosition, element, linkedArrayHashSet);
    }

    private void checkOnlyElementContainedAt(int expectedPosition, Object element, List<Object> target) {
        checkElementContainedAt(expectedPosition, element, target);
        for (Object item: target) {
            assertThat("Returned " + item + " which was not " + element, item, is(element));
        }
    }

    private void checkElementsOnlyContainedAt(List<Integer> expectedPositions) {
        Collection<Object> containedElements = new ArrayList<Object>(linkedArrayHashSet.size());
        for (Integer expectedPosition: expectedPositions) {
            Object containedElement = allTestElements.get(expectedPosition);
            checkElementContainedAt(expectedPosition, containedElement);
            containedElements.add(containedElement);
        }
        for (Object item: linkedArrayHashSet) {
            assertThat("Returned " + item + " which was not contained in " + containedElements, containedElements.contains(item), is(true));
        }
    }

    private void checkElementsContained(Collection<Object> elements) {
        checkElementsContained(elements, linkedArrayHashSet);
    }

    private void checkElementsContained(Collection<Object> elements, List<Object> target) {
        for (Object element: elements) {
            assertThat("Wrong result for contains(" + element + ")", target.contains(element), is(true));
            assertThat("toArray() missing " + element, target.toArray(), hasItemInArray(element));
            assertThat("toArray() missing " + element, target.toArray(new Object[target.size()]), hasItemInArray(element));
            assertThat("subList missing " + element,target.subList(0, target.size()).contains(element), is(true));
        }
        assertThat(target.containsAll(elements), is(true));
        assertThat(target.subList(0, target.size()).containsAll(elements), is(true));
    }

    private void checkElementsNotContained(Object... elemItems) {
        checkElementsNotContained(linkedArrayHashSet, elemItems);
    }

    private void checkElementsNotContained(List<Object> target, Object... elemItems) {
        Collection<Object> elements = Arrays.asList(elemItems);
        for (Object element: elements) {
            assertThat("Wrong result for contains(" + element + ")", target.contains(element), is(false));
            assertThat("toArray() missing " + element, target.toArray(), not(hasItemInArray(element)));
            assertThat("subList missing " + element,target.subList(0, target.size()).contains(element), is(false));
        }
        assertThat(target.containsAll(elements), is(false));
        assertThat(target.subList(0, target.size()).containsAll(elements), is(false));
    }

    // Includes begin, does not include end
    private List<Integer> rangeClosedOpen(int begin, int end) {
        List<Integer> ret = new ArrayList<Integer>((end - begin) + 1);
        for(int i = begin; i < end; ret.add(i++));
        return ret;
    }
}
