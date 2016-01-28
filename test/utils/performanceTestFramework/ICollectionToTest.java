package utils.performanceTestFramework;

import java.util.Collection;

/**
 */
public interface ICollectionToTest<T extends Collection> {
    T createNewCollection();
}
