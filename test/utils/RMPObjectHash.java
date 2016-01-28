package utils;

/**
 * Test utility that produces a single object hash.
 */
public class RMPObjectHash extends AbstractRMPHash<Object> {

    private final int id;
    private final Object result;

    public RMPObjectHash(final byte typeId, final int id, final Object result) {
        super(typeId);
        this.id = id;
        this.result = result;
    }

    @Override
    public Object unHash(final long toUnhash) {
        return result;
    }

    @Override
    protected int hashInternal(final Object toHash) {
        return id;
    }
}
