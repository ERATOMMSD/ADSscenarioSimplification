package scenarioSimplifier.resultsComparator;

import ads.ADSResult;
import ads.testMeasure.DangerMeasure;

public class MaxDangerComparator implements TestResultComparator {
    private final static MaxDangerComparator INSTANCE = new MaxDangerComparator();
    private static final double DELTA = 0.001;

    private MaxDangerComparator() {
    }

    @Override
    public boolean sameResult(ADSResult origResult, ADSResult simplScenResult) {
        DangerMeasure origScenDangerMeasure = origResult.getMaxDanger();
        DangerMeasure simplScenDangerMeasure = simplScenResult.getMaxDanger();
        boolean sameResult = (origScenDangerMeasure.isCollision() == simplScenDangerMeasure.isCollision()) &&
                Math.abs(origScenDangerMeasure.getTestMeasure() - simplScenDangerMeasure.getTestMeasure()) < DELTA;
        return sameResult;
    }

    public static MaxDangerComparator getINSTANCE() {
        return INSTANCE;
    }
}
