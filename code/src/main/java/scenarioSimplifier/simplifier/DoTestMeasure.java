package scenarioSimplifier.simplifier;

import ads.testMeasure.TestMeasure;

public class DoTestMeasure<T extends TestMeasure> implements Comparable<DoTestMeasure> {
    private final T doMaxDangerMeasure;
    private final int doIndex;

    DoTestMeasure(T doMaxDangerMeasure, int doIndex) {
        this.doMaxDangerMeasure = doMaxDangerMeasure;
        this.doIndex = doIndex;
    }

    public T getDoMaxDanger() {
        return doMaxDangerMeasure;
    }

    public int getDoIndex() {
        return doIndex;
    }

    @Override
    public int compareTo(DoTestMeasure o) {
        return (doMaxDangerMeasure.smaller(o.doMaxDangerMeasure) ? -1 : (doMaxDangerMeasure.bigger(o.doMaxDangerMeasure) ? 1 : 0));
    }
}
