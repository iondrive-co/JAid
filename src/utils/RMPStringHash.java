/*
 * (c)
 */
package utils;

import static java.lang.Integer.compare;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides minimum perfect hashing function from Strings to longs, and recovery of an original String given a long.
 */
public class RMPStringHash extends AbstractRMPHash<String> {
    // The position of each strings original hash value holds the multiplier for the secondary hash function.
    private final Map<Integer, Integer> intermediateHashToMultiplier = new HashMap<>();
    // Each string in the position determined by the secondary hash function.
    // TODO we do not need to actually store this
    private final Map<Integer, String> finalHashToValue = new HashMap<>();

    public RMPStringHash(final byte id, final Set<String> allVals) {
        super(id);
        buildMaps(allVals, intermediateHashToMultiplier, finalHashToValue);
    }

    @Override
    protected int hashInternal(final String toHash) {
        return hash(toHash, intermediateHashToMultiplier.get(hash(toHash, 31)));
    }

    @Override
    public String unHash(final long toUnhash) {
        return finalHashToValue.get((int) toUnhash);
    }

    private static int hash(final String toHash, final int multiplier) {
        int hash = 0;
        for (final char c : toHash.toCharArray()) {
            hash = multiplier * hash + c;
        }
        return hash;
    }

    private static int getProbablePrime(final int num, final List<BigInteger> relPrimes) {
        if (num < relPrimes.size()) {
            return relPrimes.get(num).intValue();
        } else {
            assert (num == relPrimes.size());
            final BigInteger nextProbablePrime = relPrimes.get(relPrimes.size() - 1).nextProbablePrime();
            relPrimes.add(nextProbablePrime);
            return nextProbablePrime.intValue();
        }
    }

    /**
     * Determine the perfect hash of each string in all values using the intermediate hash map, and store the
     * string against that hash position in the final hash map.
     */
    private static void buildMaps(final Set<String> allVals, final Map<Integer, Integer> intermediateHashToMultiplier,
                                  final Map<Integer, String> finalHashToValue) {
        final Map<Integer, Set<String>> hashToVals = new HashMap<>();
        allVals.forEach(val -> {
            final int hashCode = val.hashCode();
            Set<String> existing = hashToVals.get(hashCode);
            if (existing == null) {
                existing = new HashSet<>();
                hashToVals.put(hashCode, existing);
            }
            existing.add(val);
        });
        // Some numbers that are probably relatively prime with the number of values. Generated and stored as needed.
        final List<BigInteger> relPrimes = new ArrayList<>(Collections.singletonList(BigInteger.valueOf(31)));
        // The current hash function multiplier, mapped to any strings which that multiplier produces free hash slots.
        final Map<Integer, String> freeValsAtMultiplier = new HashMap<>();
        hashToVals.entrySet().stream().sorted((a, b) -> compare(b.getValue().size(), a.getValue().size())).forEach(entry -> {
            int curMultiplier = -1;
            for (int i = 0; freeValsAtMultiplier.size() < entry.getValue().size(); i++) {
                freeValsAtMultiplier.clear();
                curMultiplier = getProbablePrime(i, relPrimes);
                for (final String val : entry.getValue()) {
                    final int finalPosition = hash(val, curMultiplier);
                    if (finalHashToValue.containsKey(finalPosition)) {
                        // Already in use, break and try again with next multiplier.
                        break;
                    } else {
                        freeValsAtMultiplier.put(finalPosition, val);
                    }
                }
            }
            intermediateHashToMultiplier.put(entry.getKey(), curMultiplier);
            // Store each value at the position ouput by hashing it using curMultiplier
            freeValsAtMultiplier.entrySet().forEach(e -> finalHashToValue.put(e.getKey(), e.getValue()));
            freeValsAtMultiplier.clear();
        });
    }
}
