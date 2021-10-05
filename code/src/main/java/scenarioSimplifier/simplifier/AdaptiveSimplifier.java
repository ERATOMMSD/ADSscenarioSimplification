package scenarioSimplifier.simplifier;

import ads.ADSResult;
import ads.ADSScenario;
import results.ResultsAndLoader;
import scenarioSimplifier.simplifier.visitorIndexes.NodeIndexesRange;
import scenarioSimplifier.simplifier.visitorIndexes.VisitorIndexesRanges;
import scenarioSimplifier.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class AdaptiveSimplifier extends Simplifier {
    protected DO_STATUS[] statusDOs;
    protected final VisitorIndexesRanges vir;
    protected Integer[] indexDO;
    protected List<String> generatedScenariosCombinations;

    public AdaptiveSimplifier(String pathOriginalScenario, ADSResult originalResult, ResultsAndLoader resultsAndLoader) throws IOException {
        super(pathOriginalScenario, originalResult, resultsAndLoader);
        statusDOs = new DO_STATUS[originalResult.getNumDynamicObjects()];
        for (int i = 0; i < statusDOs.length; i++) {
            statusDOs[i] = DO_STATUS.ADD_TBR;
        }
        vir = new VisitorIndexesRanges(0, statusDOs.length - 1, 2);
        fixOrderDOs();
        vir.getNext(false);
        generatedScenariosCombinations = new ArrayList<>();
        generatedScenariosCombinations.add(getIdScenario(vir.getCurrent()));
    }

    @Override
    public boolean hasNextScenario() {
        return vir.getCurrent() != null;
    }

    @Override
    public String getNextScenario() throws IOException {
        ADSScenario currentSuggestedScenario = resultsAndLoader.loadScenario(pathOriginalScenario);
        List<String> dynamicObjects = currentSuggestedScenario.getNumDynamicObjectsIDs();
        List<Integer> indexsDOstoRemove = getIndexesDOstoRemove();
        List<String> doIDsToRemove = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("_DOs");
        for (int indexDOtoRemove : indexsDOstoRemove) {
            String id = dynamicObjects.get(indexDOtoRemove);
            String idOnlyNumber = id.replaceAll("do", "");
            doIDsToRemove.add(id);
            sb.append("_").append(idOnlyNumber);
        }
        removeDynamicObjects(currentSuggestedScenario, indexsDOstoRemove);
        pathCurrentSuggestedScenario = Utils.saveScenarioToFile(currentSuggestedScenario, folderResultsGenerated.resolve(Paths.get(simplifierID() + "_" + counter + sb + ".json")));
        counter++;
        return pathCurrentSuggestedScenario;
    }

    public void acceptSimplifiedScenario(boolean acceptScenario) throws IOException {
        simplifiedScenarioAccepted = acceptScenario;
        NodeIndexesRange currentRange = vir.getCurrent();
        if (acceptScenario) {
            pathLastAcceptedScenario = pathCurrentSuggestedScenario;
            Path pathLastAcceptedScenarioPath = Paths.get(pathLastAcceptedScenario);
            Files.copy(pathLastAcceptedScenarioPath, folderResultsAccepted.resolve(pathLastAcceptedScenarioPath.getFileName()));
            int counterRemoved = 0;
            for (int i = currentRange.getStart(); i <= currentRange.getEnd(); i++) {
                if (statusDOs[i] == DO_STATUS.REMOVED_TRIAL) {
                    statusDOs[i] = DO_STATUS.REMOVED_FIXED;
                }
            }
            for (int i = 0; i < statusDOs.length; i++) {
                if (statusDOs[i] == DO_STATUS.REMOVED_FIXED) {
                    counterRemoved++;
                }
            }
            NodeIndexesRange selectedRange = getNextIntervalNotIncluded();
            //if the select range removes all the remaining DOs, we need to skip it
            if (selectedRange != null && ((selectedRange.getSize() + counterRemoved) == indexDO.length)) {
                getNextInterval(false);
                System.out.println("We cannot remove everything! Discarding " + selectedRange + " accepting " + vir.getCurrent());
            }
            //System.out.println("Selected in accepted " + vir.getCurrent());
        } else {
            negativeCase(currentRange);
        }
    }

    protected void negativeCase(NodeIndexesRange currentRange) {
        if (currentRange.getStart() == currentRange.getEnd()) {
            statusDOs[currentRange.getStart()] = DO_STATUS.ADD_FIXED;
        } else {
            for (int i = currentRange.getStart(); i <= currentRange.getEnd(); i++) {
                statusDOs[i] = DO_STATUS.ADD_TBR;
            }
        }
        getNextInterval(true);
    }

    protected NodeIndexesRange getNextInterval(boolean increaseSplit) {
        //System.out.println(generatedScenariosCombinations);
        NodeIndexesRange next = vir.getNext(increaseSplit);
        String scenarioID = getIdScenario(next);
        while (generatedScenariosCombinations.contains(scenarioID)) {
            next = vir.getNext(false);
            scenarioID = getIdScenario(next);
        }
        //System.out.println(scenarioID);
        if (scenarioID != null) {
            generatedScenariosCombinations.add(scenarioID);
        }
        return next;
    }

    protected NodeIndexesRange getNextIntervalNotIncluded() {
        NodeIndexesRange next = vir.getNextNotIncluded();
        String scenarioID = getIdScenario(next);
        while (generatedScenariosCombinations.contains(scenarioID)) {
            next = vir.getNext(false);
            scenarioID = getIdScenario(next);
        }
        //System.out.println(scenarioID);
        if (scenarioID != null) {
            generatedScenariosCombinations.add(scenarioID);
        }
        return next;
    }

    private List<Integer> getIndexesDOstoRemove() {
        List<Integer> indexesToRemove = new ArrayList<>();
        NodeIndexesRange range = vir.getCurrent();
        for (int i = 0; i < statusDOs.length; i++) {
            if (statusDOs[i] == DO_STATUS.REMOVED_FIXED) {
                indexesToRemove.add(indexDO[i]);
            } else if (i >= range.getStart() && i <= range.getEnd()) {
                indexesToRemove.add(indexDO[i]);
                statusDOs[i] = DO_STATUS.REMOVED_TRIAL;
            }
        }
        return indexesToRemove;
    }

    protected abstract void fixOrderDOs();

    private String getIdScenario(NodeIndexesRange range) {
        if (range != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < statusDOs.length; i++) {
                if ((statusDOs[i] == DO_STATUS.REMOVED_FIXED) || range.isIncluding(i)) {
                    sb.append("_").append(i);
                }
            }
            return sb.toString();
        }
        return "";
    }
}
