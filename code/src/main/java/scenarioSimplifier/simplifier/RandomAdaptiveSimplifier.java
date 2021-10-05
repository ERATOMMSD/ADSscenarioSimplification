package scenarioSimplifier.simplifier;

import ads.ADSResult;
import results.ResultsAndLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomAdaptiveSimplifier extends AdaptiveSimplifier {

    public RandomAdaptiveSimplifier(String pathOriginalScenario, ADSResult originalResult, ResultsAndLoader resultsAndLoader) throws IOException {
        super(pathOriginalScenario, originalResult, resultsAndLoader);
    }

    @Override
    protected void fixOrderDOs() {
        indexDO = new Integer[statusDOs.length];
        for (int i = 0; i < indexDO.length; i++) {
            indexDO[i] = i;
        }
        List<Integer> indexDOasList = Arrays.asList(indexDO);
        Collections.shuffle(indexDOasList);
        indexDOasList.toArray(indexDO);
    }

    @Override
    public String simplifierID() {
        return "RndAdp";
    }
}
