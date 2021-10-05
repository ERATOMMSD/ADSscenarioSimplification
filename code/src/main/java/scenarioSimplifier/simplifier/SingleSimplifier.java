package scenarioSimplifier.simplifier;

import ads.ADSResult;
import ads.ADSScenario;
import results.ResultsAndLoader;
import scenarioSimplifier.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class SingleSimplifier extends Simplifier {
    protected final List<String> removedOrTried;
    protected final List<String> removed;

    public SingleSimplifier(String pathOriginalScenario, ADSResult originalResult, ResultsAndLoader resultsAndLoader) throws IOException {
        super(pathOriginalScenario, originalResult, resultsAndLoader);
        removedOrTried = new ArrayList<>();
        removed = new ArrayList<>();
    }

    public void acceptSimplifiedScenario(boolean acceptScenario) throws IOException {
        simplifiedScenarioAccepted = acceptScenario;
        if (acceptScenario) {
            removed.add(removedOrTried.get(removedOrTried.size() - 1));
            pathLastAcceptedScenario = pathCurrentSuggestedScenario;
            lastAcceptedScenarioResult = currentSuggestedScenarioResult;
            Path pathLastAcceptedScenarioPath = Paths.get(pathLastAcceptedScenario);
            Files.copy(pathLastAcceptedScenarioPath, folderResultsAccepted.resolve(pathLastAcceptedScenarioPath.getFileName()));
        }
    }

    @Override
    public boolean hasNextScenario() {
        return (removedOrTried.size() < numDynamicObjectsOriginalScenario) && (numDynamicObjectsOriginalScenario > 1) &&
                //this works because we know that we are crashing with another car
                //however, in general the simplification could remove all the other cars
                //TODO make it more general for any simplification setting
                resultsAndLoader.loadScenario(pathLastAcceptedScenario).getNumDynamicObjects() > 1;
    }

    @Override
    public String getNextScenario() throws IOException {
        ADSScenario currentSuggestedScenario = resultsAndLoader.loadScenario(pathLastAcceptedScenario);
        List<String> dynamicObjects = currentSuggestedScenario.getNumDynamicObjectsIDs();
        int indexDOtoRemove = getIndexDOtoRemove(dynamicObjects);
        String doIDtoRemove = dynamicObjects.get(indexDOtoRemove);
        removeDynamicObject(currentSuggestedScenario, indexDOtoRemove);
        StringBuilder sb = new StringBuilder();
        sb.append("_DOs");
        for (String id : removed) {
            String idOnlyNumber = id.replaceAll("do", "");
            sb.append("_").append(idOnlyNumber);
        }
        sb.append("_").append(doIDtoRemove.replaceAll("do", ""));
        pathCurrentSuggestedScenario = Utils.saveScenarioToFile(currentSuggestedScenario, folderResultsGenerated.resolve(Paths.get(simplifierID() + "_" + counter + sb + ".json")));
        removedOrTried.add(doIDtoRemove);
        counter++;
        return pathCurrentSuggestedScenario;
    }

    protected abstract int getIndexDOtoRemove(List<String> dynamicObjects);
}
