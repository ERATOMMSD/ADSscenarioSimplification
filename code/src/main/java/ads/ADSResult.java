package ads;

import ads.testMeasure.DangerMeasure;

import java.util.ArrayList;
import java.util.List;

abstract public class ADSResult {
    protected final List<DangerMeasure> dynamicOjectsMaxDangerMeasure;
    private final int numDynamicObjects;

    public ADSResult(int numDynamicObjects) {
        this.dynamicOjectsMaxDangerMeasure = new ArrayList<>();
        this.numDynamicObjects = numDynamicObjects;
    }

    public List<DangerMeasure> getDynamicOjectsMaxDanger() {
        return dynamicOjectsMaxDangerMeasure;
    }

    public int getNumDynamicObjects() {
        return numDynamicObjects;
    }

    abstract public boolean isError();

    abstract public DangerMeasure getMaxDanger();

    abstract public String printLog();
}

