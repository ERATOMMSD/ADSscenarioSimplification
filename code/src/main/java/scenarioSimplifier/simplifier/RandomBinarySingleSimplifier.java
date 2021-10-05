package scenarioSimplifier.simplifier;

import ads.ADSResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomBinarySingleSimplifier extends BinarySingleSimplifier {

    public RandomBinarySingleSimplifier(String pathOriginalScenario, ADSResult originalResult) throws IOException {
        super(pathOriginalScenario, originalResult);
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
        return "RndBinSin";
    }
}
