package jaid.function;

import java.util.function.Supplier;

/**
 * Represents a supplier of {@code float}-valued results.  This is the
 * {@code float}-producing primitive specialization of {@link Supplier}.
 *
 * <p>There is no requirement that a distinct result be returned each
 * time the supplier is invoked.
 *
 * @see Supplier
 * @since 1.8
 */
@FunctionalInterface
public interface FloatSupplier {

    /**
     * Gets a result.
     *
     * @return a result
     */
    float getAsFloat();
}

