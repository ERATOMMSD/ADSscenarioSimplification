package scenarioSimplifier.resultsComparator;

import ads.ADSResult;

public interface TestResultComparator {
    boolean sameResult(ADSResult origResult, ADSResult simplScenResult);

    static TestResultComparator getComparator(Class<? extends TestResultComparator> comparatorClass) {
        if (comparatorClass == MaxDangerComparator.class) {
            return MaxDangerComparator.getINSTANCE();
        }
        throw new Error("Comparator unknown");
    }
}
