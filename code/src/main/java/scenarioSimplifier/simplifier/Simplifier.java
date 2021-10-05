package scenarioSimplifier.simplifier;

import ads.ADSResult;
import ads.pathPlanner.PathPlannerResult;
import ads.pathPlanner.PathPlannerRunner;
import ads.pathPlanner.scenario.Scenario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public abstract class Simplifier {
    protected String pathOriginalScenario;
    protected String nameScenario;
    protected ADSResult originalResult;

    protected String pathLastAcceptedScenario;
    protected ADSResult lastAcceptedScenarioResult;

    protected String pathCurrentSuggestedScenario;//path of the scenario to be checked
    protected ADSResult currentSuggestedScenarioResult;//simulation results of the scenario to be checked

    protected boolean simplifiedScenarioAccepted;//flag saying whether the current scenario is accepted

    protected int counter;
    protected int numDynamicObjectsOriginalScenario;

    protected Path folderResults;
    protected Path folderResultsLogs;
    protected Path folderResultsGenerated;
    protected Path folderResultsAccepted;

    public Simplifier(String pathOriginalScenario, ADSResult originalResult) throws IOException {
        this.pathOriginalScenario = pathOriginalScenario;
        Path pathOriginalScenarioPath = Paths.get(pathOriginalScenario);
        nameScenario = pathOriginalScenarioPath.getFileName().toString().replaceAll(".json", "");
        this.originalResult = originalResult;
        pathLastAcceptedScenario = pathOriginalScenario;
        lastAcceptedScenarioResult = originalResult;
        numDynamicObjectsOriginalScenario = Utils.loadScenario(pathOriginalScenario).getDynamicObjects().size();
        simplifiedScenarioAccepted = false;
        counter = 0;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String date = simpleDateFormat.format(new GregorianCalendar(Locale.JAPAN).getTime());
        folderResults = Paths.get("results", Paths.get(".").relativize(Paths.get(pathOriginalScenario).getParent()).toString(), nameScenario + "_" + simplifierID() + "_" + date);
        if (!Files.exists(folderResults)) {
            Files.createDirectories(folderResults);
            folderResultsGenerated = folderResults.resolve(Paths.get(Utils.GENERATED_FOLDER_NAME));
            Files.createDirectories(folderResultsGenerated);
            folderResultsAccepted = folderResults.resolve(Paths.get(Utils.ACCEPTED_FOLDER_NAME));
            Files.createDirectories(folderResultsAccepted);
            folderResultsLogs = folderResults.resolve(Paths.get(Utils.LOGS_FOLDER_NAME));
            Files.createDirectories(folderResultsLogs);
        }
        Files.copy(pathOriginalScenarioPath, folderResults.resolve(pathOriginalScenarioPath.getFileName()));
    }

    public abstract boolean hasNextScenario() throws IOException;

    public abstract String getNextScenario() throws IOException;

    public ADSResult runCurrentSuggestedScenario(int timeout) throws IOException {
        PathPlannerRunner ppr = new PathPlannerRunner(pathCurrentSuggestedScenario);
        currentSuggestedScenarioResult = ppr.run(timeout);
        return currentSuggestedScenarioResult;
    }

    public abstract void acceptSimplifiedScenario(boolean acceptScenario) throws IOException;

    public static Simplifier getSimplifier(String simplifierClassId, String pathOriginalScenario, PathPlannerResult resultOriginalScenario) throws IOException {
        Class<? extends Simplifier> simplifierClass = getSimplifierClass(simplifierClassId);
        return getSimplifier(simplifierClass, pathOriginalScenario, resultOriginalScenario);
    }

    public static Class<? extends Simplifier> getSimplifierClass(String simplifierClassId) {
        Class<? extends Simplifier> simplifierClass = null;
        switch (simplifierClassId) {
            case "RndSin":
                simplifierClass = RandomSingleSimplifier.class;
                break;
            case "DanSin":
                simplifierClass = DangerSingleSimplifier.class;
                break;
            case "RndBin":
                simplifierClass = RandomBinarySimplifier.class;
                break;
            case "DanBin":
                simplifierClass = DangerBinarySimplifier.class;
                break;
            case "RndBinSin":
                simplifierClass = RandomBinarySingleSimplifier.class;
                break;
            case "DanBinSin":
                simplifierClass = DangerBinarySingleSimplifier.class;
                break;
            case "RndAdp":
                simplifierClass = RandomAdaptiveSimplifier.class;
                break;
            case "DanAdp":
                simplifierClass = DangerAdaptiveSimplifier.class;
                break;
            default:
                throw new Error("Simplifier unknown");
        }
        return simplifierClass;
    }

    public static Simplifier getSimplifier(Class<? extends Simplifier> simplifierClass, String pathOriginalScenario, ADSResult resultOriginalScenario) throws IOException {
        if (simplifierClass == RandomSingleSimplifier.class) {
            return new RandomSingleSimplifier(pathOriginalScenario, resultOriginalScenario);
        } else if (simplifierClass == DangerSingleSimplifier.class) {
            return new DangerSingleSimplifier(pathOriginalScenario, resultOriginalScenario);
        } else if (simplifierClass == RandomBinarySimplifier.class) {
            return new RandomBinarySimplifier(pathOriginalScenario, resultOriginalScenario);
        } else if (simplifierClass == DangerBinarySimplifier.class) {
            return new DangerBinarySimplifier(pathOriginalScenario, resultOriginalScenario);
        } else if (simplifierClass == RandomBinarySingleSimplifier.class) {
            return new RandomBinarySingleSimplifier(pathOriginalScenario, resultOriginalScenario);
        } else if (simplifierClass == DangerBinarySingleSimplifier.class) {
            return new DangerBinarySingleSimplifier(pathOriginalScenario, resultOriginalScenario);
        } else if (simplifierClass == RandomAdaptiveSimplifier.class) {
            return new RandomAdaptiveSimplifier(pathOriginalScenario, resultOriginalScenario);
        } else if (simplifierClass == DangerAdaptiveSimplifier.class) {
            return new DangerAdaptiveSimplifier(pathOriginalScenario, resultOriginalScenario);
        }
        throw new Error("Simplifier unknown");
    }

    public void removeDynamicObject(Scenario scenario, int index) {
        removeDynamicObjects(scenario, Collections.singletonList(index));
    }

    public void removeDynamicObjects(Scenario scenario, List<Integer> indexes) {
        scenario.removeDynamicObjects(indexes);
    }

    public abstract String simplifierID();

    public String getPathLastAcceptedScenario() {
        return pathLastAcceptedScenario;
    }

    public Path getFolderResults() {
        return folderResults;
    }

    public Path getFolderResultsLogs() {
        return folderResultsLogs;
    }

    protected enum DO_STATUS {
        ADD_TBR, REMOVED_TRIAL, ADD_FIXED, REMOVED_FIXED
    }
}