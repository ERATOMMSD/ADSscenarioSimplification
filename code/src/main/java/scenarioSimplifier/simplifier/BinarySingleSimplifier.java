package scenarioSimplifier.simplifier;

import ads.ADSResult;
import results.ResultsAndLoader;
import scenarioSimplifier.simplifier.visitorIndexes.NodeIndexesRangeBinary;

import java.io.IOException;

public abstract class BinarySingleSimplifier extends BinarySimplifier {
    private NodeIndexesRangeBinary rangeToDoSingle;

    public BinarySingleSimplifier(String pathOriginalScenario, ADSResult originalResult, ResultsAndLoader resultsAndLoader) throws IOException {
        super(pathOriginalScenario, originalResult, resultsAndLoader);
        rangeToDoSingle = null;
    }

    @Override
    protected void negativeCase(NodeIndexesRangeBinary currentRange) {
        if (rangeToDoSingle == null) {
            rangeToDoSingle = currentRange;
            //System.out.println("rangeToDoSingle updated " + rangeToDoSingle);
        }
        super.negativeCase(currentRange);
    }

    @Override
    protected NodeIndexesRangeBinary getNextInterval() {
        NodeIndexesRangeBinary next = null;
        String scenarioID = null;
        if (rangeToDoSingle != null) {
            do {
                next = vir.getNext();
                scenarioID = getIdScenario(next);
            }
            while (next != null && rangeToDoSingle.isIncluding(next) && (next.getSize() > 1 || generatedScenariosCombinations.contains(scenarioID)));
            if (!rangeToDoSingle.isIncluding(next)) {
                rangeToDoSingle = null;
                //System.out.println("rangeToDoSingle reset");
            }
        } else {
            //System.out.println("selection non in subrange");
            next = vir.getNext();
            scenarioID = getIdScenario(next);
        }
        assert scenarioID == null || !generatedScenariosCombinations.contains(scenarioID) : generatedScenariosCombinations + "\n" + scenarioID;
        //System.out.println("Selected " + next + " size " + (next != null ? next.getSize() : "null"));
        //System.out.println(scenarioID);
        generatedScenariosCombinations.add(scenarioID);
        return next;
    }

    protected NodeIndexesRangeBinary getNextIntervalNotIncluded() {
        NodeIndexesRangeBinary next = null;
        String scenarioID = null;
        if (rangeToDoSingle != null) {
            do {
                next = vir.getNext();
                scenarioID = getIdScenario(next);
            }
            while (next != null && rangeToDoSingle.isIncluding(next) && (next.getSize() > 1 || generatedScenariosCombinations.contains(scenarioID)));
            if (!rangeToDoSingle.isIncluding(next)) {
                rangeToDoSingle = null;
                //System.out.println("rangeToDoSingle reset in getNextIntervalNotIncluded");
            }
        } else {
            //System.out.println("selection non in subrange in getNextIntervalNotIncluded");
            next = vir.getNextNotIncluded();
            scenarioID = getIdScenario(next);
        }
        assert scenarioID == null || !generatedScenariosCombinations.contains(scenarioID) : generatedScenariosCombinations + "\n" + scenarioID;
        //System.out.println("Selected in getNextIntervalNotIncluded " + next + " size " + (next != null ? next.getSize() : "null"));
        //System.out.println(scenarioID);
        generatedScenariosCombinations.add(scenarioID);
        return next;
    }
}

