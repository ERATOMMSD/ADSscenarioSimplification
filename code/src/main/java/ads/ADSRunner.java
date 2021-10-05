package ads;

import results.ResultsAndLoader;

import java.io.IOException;

public abstract class ADSRunner {
    // name of the  scenario file
    protected String scenarioFileName;

    public ADSRunner(String scenarioFileName) {
        this.scenarioFileName = scenarioFileName;
    }

    public abstract ADSResult run(int timeout);

    public static ADSRunner getADSrunner(Class<? extends ADSRunner> runnerClass, String pathOriginalScenario) throws IOException {
        //if (runnerClass == PathPlannerRunner.class) {
        //    return new PathPlannerRunner(pathOriginalScenario);
        //}
        throw new Error("Runner unknown");
    }

    public String getScenarioFileName() {
        return scenarioFileName;
    }

    public abstract ResultsAndLoader getResultsAndLoader();
}

