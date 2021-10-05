package scenarioSimplifier.simplifier;

import ads.ADSResult;
import ads.testMeasure.DangerMeasure;
import results.ResultsAndLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DangerAdaptiveSimplifier extends AdaptiveSimplifier {

    public DangerAdaptiveSimplifier(String pathOriginalScenario, ADSResult originalResult, ResultsAndLoader resultsAndLoader) throws IOException {
        super(pathOriginalScenario, originalResult, resultsAndLoader);
    }

    @Override
    protected void fixOrderDOs() {
        List<DangerMeasure> dynamicOjectsMaxDangerMeasure = lastAcceptedScenarioResult.getDynamicOjectsMaxDanger();
        List<DoTestMeasure> doIndexAndDanger = new ArrayList<>();
        for (int i = 0; i < dynamicOjectsMaxDangerMeasure.size(); i++) {
            doIndexAndDanger.add(new DoTestMeasure(dynamicOjectsMaxDangerMeasure.get(i), i));
        }
        Collections.sort(doIndexAndDanger);
        indexDO = new Integer[statusDOs.length];
        for (int i = 0; i < indexDO.length; i++) {
            indexDO[i] = doIndexAndDanger.get(i).getDoIndex();
        }
    }

    @Override
    public String simplifierID() {
        return "DanAdp";
    }

}
