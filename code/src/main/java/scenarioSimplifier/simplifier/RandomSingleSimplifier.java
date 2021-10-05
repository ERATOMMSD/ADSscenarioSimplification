package scenarioSimplifier.simplifier;

import ads.ADSResult;
import results.ResultsAndLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSingleSimplifier extends SingleSimplifier {
    private final Random rnd;

    public RandomSingleSimplifier(String pathOriginalScenario, ADSResult originalResult, ResultsAndLoader resultsAndLoader) throws IOException {
        super(pathOriginalScenario, originalResult, resultsAndLoader);
        rnd = new Random();
    }

    protected int getIndexDOtoRemove(List<String> dynamicObjects) {
        List<Integer> availableIndexes = new ArrayList<>();
        for (int i = 0; i < dynamicObjects.size(); i++) {
            if (!removedOrTried.contains(dynamicObjects.get(i))) {
                availableIndexes.add(i);
            }
        }
        Integer indexDOtoRemove = availableIndexes.get(rnd.nextInt(availableIndexes.size()));
        return indexDOtoRemove;
    }

    @Override
    public String simplifierID() {
        return "RndSin";
    }
}
