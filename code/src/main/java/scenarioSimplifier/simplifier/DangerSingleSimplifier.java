package scenarioSimplifier.simplifier;

import ads.ADSResult;
import ads.testMeasure.DangerMeasure;
import ads.pathPlanner.scenario.DynamicObject;

import java.io.IOException;
import java.util.List;

public class DangerSingleSimplifier extends SingleSimplifier {

    public DangerSingleSimplifier(String pathOriginalScenario, ADSResult originalResult) throws IOException {
        super(pathOriginalScenario, originalResult);
    }

    @Override
    protected int getIndexDOtoRemove(List<DynamicObject> dynamicObjects) {
        List<DangerMeasure> dynamicOjectsMaxDangerMeasure = lastAcceptedScenarioResult.getDynamicOjectsMaxDanger();
        DangerMeasure minMaxDangerMeasure = null;
        int indexDOtoRemove = -1;
        for (int i = 0; i < dynamicOjectsMaxDangerMeasure.size(); i++) {
            if (!removedOrTried.contains(dynamicObjects.get(i).id)) {
                DangerMeasure doMaxDangerMeasure = dynamicOjectsMaxDangerMeasure.get(i);
                if (doMaxDangerMeasure.smaller(minMaxDangerMeasure)) {
                    minMaxDangerMeasure = doMaxDangerMeasure;
                    indexDOtoRemove = i;
                }
            }
        }
        return indexDOtoRemove;
    }

    @Override
    public String simplifierID() {
        return "DanSin";
    }
}
