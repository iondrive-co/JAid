package utils;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * {@link AbstractRMPHash} unit test.
 */
public class AbstractRMPHashUnitTest {

    @Test
    public void version() {
        final byte typeId = 42;
        final byte hashVal = 77;
        final AbstractRMPHash<Object> objectHash = new RMPObjectHash(typeId, hashVal, new Object());
        assertEquals("typeId not encoded", typeId, (byte)(objectHash.hash(new Object()) >>> 56));
        assertFalse("typeId not specified", objectHash.test(41L << 56));
        assertFalse("typeId not specified", objectHash.test((long) Integer.MAX_VALUE << 56));
        assertTrue("typeId specified", objectHash.test((long) typeId << 56));
    }
}