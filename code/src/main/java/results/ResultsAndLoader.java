package results;

import ads.ADSScenario;

public abstract class ResultsAndLoader<T extends ADSScenario> {
    public abstract T loadScenario(String scenarioPath);

    public static ResultsAndLoader getResultsAndLoader(Class<? extends ResultsAndLoader> resultsAndLoaderClass) {
        //if (resultsAndLoaderClass == PathPlannerResultsAndLoader.class) {
        //    return new PathPlannerResultsAndLoader();
        //}
        throw new Error("ResultsAndLoader unknown");
    }
}
