package scenarioSimplifier.simplifier;

import ads.ADSResult;
import ads.ADSScenario;
import results.ResultsAndLoader;
import scenarioSimplifier.simplifier.visitorIndexes.NodeIndexesRangeBinary;
import scenarioSimplifier.simplifier.visitorIndexes.VisitorIndexesRangesBinary;
import scenarioSimplifier.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class BinarySimplifier extends Simplifier {
    protected DO_STATUS[] statusDOs;
    protected final VisitorIndexesRangesBinary vir;
    protected Integer[] indexDO;
    protected List<String> generatedScenariosCombinations;

    public BinarySimplifier(String pathOriginalScenario, ADSResult originalResult, ResultsAndLoader resultsAndLoader) throws IOException {
        super(pathOriginalScenario, originalResult, resultsAndLoader);
        statusDOs = new DO_STATUS[originalResult.getNumDynamicObjects()];
        for (int i = 0; i < statusDOs.length; i++) {
            statusDOs[i] = DO_STATUS.ADD_TBR;
        }
        vir = new VisitorIndexesRangesBinary(0, statusDOs.length - 1);
        fixOrderDOs();
        vir.getNext();
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
        NodeIndexesRangeBinary currentRange = vir.getCurrent();
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
            NodeIndexesRangeBinary selectedRange = getNextIntervalNotIncluded();
            //if the select range removes all the remaining DOs, we need to skip it
            if (selectedRange != null && ((selectedRange.getSize() + counterRemoved) == indexDO.length)) {
                getNextInterval();
                System.out.println("We cannot remove everything! Discarding " + selectedRange + " accepting " + vir.getCurrent());
            }
            //System.out.println("Selected in accepted " + vir.getCurrent());
        } else {
            negativeCase(currentRange);
        }
    }

    protected void negativeCase(NodeIndexesRangeBinary currentRange) {
        if (currentRange.getStart() == currentRange.getEnd()) {
            statusDOs[currentRange.getStart()] = DO_STATUS.ADD_FIXED;
        } else {
            for (int i = currentRange.getStart(); i <= currentRange.getEnd(); i++) {
                statusDOs[i] = DO_STATUS.ADD_TBR;
            }
        }
        getNextInterval();
    }

    protected NodeIndexesRangeBinary getNextInterval() {
        return vir.getNext();
    }

    protected NodeIndexesRangeBinary getNextIntervalNotIncluded() {
        return vir.getNextNotIncluded();
    }

    private List<Integer> getIndexesDOstoRemove() {
        List<Integer> indexesToRemove = new ArrayList<>();
        NodeIndexesRangeBinary range = vir.getCurrent();
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

    String getIdScenario(NodeIndexesRangeBinary range) {
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
