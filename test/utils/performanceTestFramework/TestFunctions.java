package utils.performanceTestFramework;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class TestFunctions {
    public ICollectionToTest createFn;
    public Collection<TestObject> toTest;
    public Collection<TestObject> testData;
    public String id;
    public long deadStore;
    public void init(ICollectionToTest testSetupFn, Collection<TestObject> testData) {
        createFn = testSetupFn;
        this.testData = testData;
        this.id = testSetupFn.toString();
    }
    public Runnable setUp(){
        return new Runnable() {
            @Override
            public void run() {
                toTest = createFn.createNewCollection();
            }
        };
    }
    public abstract Runnable run();
    public Runnable tearDown() {
        return new Runnable() {
            @Override
            public void run() {
                toTest = null;
                System.gc();
            }
        };
    }

    public static List<TestObject> rangeClosedOpen(int begin, int end) {
        List<TestObject> ret = new ArrayList<TestObject>(end - begin + 1);
        for(int i = begin; i < end; i++) {
            ret.add(new TestObject());
        }
        return ret;
    }
}
