package scenarioSimplifier;

import ads.ADSResult;
import ads.ADSRunner;
import logger.CustomLogger;
import results.ResultsAndLoader;
import scenarioSimplifier.resultsComparator.TestResultComparator;
import scenarioSimplifier.simplifier.Simplifier;
import scenarioSimplifier.utils.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ADSscenarioSimplifier {
    protected static final Logger logger = CustomLogger.getLogger();
    private final String pathOriginalScenario;
    private final int timeout;
    private final Class<? extends Simplifier> simplifierClass;
    private final Class<? extends TestResultComparator> resultsComparatorClass;
    private final Class<? extends ADSRunner> adsRunnerClass;
    private final Class<? extends ResultsAndLoader> resultsAndLoaderClass;
    private final String basePath;

    /**
     * @param pathOriginalScenario   relative  path to the original scenario
     * @param simplifierClass        class extending Simplifier that implements a simplification policy
     * @param resultsComparatorClass class extending TestResultComparator that implements a comparison policy between two test results
     * @throws IOException
     */
    public ADSscenarioSimplifier(String pathOriginalScenario, int timeout, Class<? extends Simplifier> simplifierClass, Class<? extends TestResultComparator> resultsComparatorClass, Class<? extends ADSRunner> adsRunnerClass, Class<? extends ResultsAndLoader> resultsAndLoaderClass) {
        this.pathOriginalScenario = pathOriginalScenario;
        this.timeout = timeout;
        this.simplifierClass = simplifierClass;
        this.resultsComparatorClass = resultsComparatorClass;
        this.adsRunnerClass = adsRunnerClass;
        this.resultsAndLoaderClass = resultsAndLoaderClass;
        this.basePath = "";
    }

    public void simplify() throws IOException {
        String pathOriginalScenarioStr = Paths.get(".").relativize(Paths.get(basePath, pathOriginalScenario)).toString();
        Utils.setupLoggerAndPathPP();

        try {
            ResultsAndLoader resultsAndLoader = ResultsAndLoader.getResultsAndLoader(resultsAndLoaderClass);
            ADSRunner adsRunner = ADSRunner.getADSrunner(adsRunnerClass, pathOriginalScenarioStr);
            pathOriginalScenarioStr = adsRunner.getScenarioFileName();//update the scenario path if this is changed by the runner
            ADSResult resultOriginalScenario = adsRunner.run(timeout);
            assert resultOriginalScenario != null : pathOriginalScenarioStr;
            Simplifier simplifier = Simplifier.getSimplifier(simplifierClass, pathOriginalScenarioStr, resultOriginalScenario, resultsAndLoader);
            TestResultComparator testComparator = TestResultComparator.getComparator(resultsComparatorClass);

            Path folderResultsLogs = setLoggers(pathOriginalScenarioStr, resultOriginalScenario, simplifier);

            logger.info("Scenario\tmetric\tsameResult\tnumDynObjs");
            String measureResult = null;
            if (resultOriginalScenario.isError()) {
                measureResult = "error";
            } else {
                measureResult = String.valueOf(resultOriginalScenario.getMaxDanger().getTestMeasure());
            }
            logger.info(Paths.get(pathOriginalScenarioStr).getFileName().toString().replaceAll(".json", "") + "\t" + measureResult + "\t" + "orig" + "\t" + resultOriginalScenario.getNumDynamicObjects());
            if (resultOriginalScenario.isError()) {
                return;
            }
            while (simplifier.hasNextScenario()) {
                try {
                    String simplifiedScenarioPath = simplifier.getNextScenario();
                    if (simplifiedScenarioPath == null) {
                        logger.info("no scenario found!");
                        break;
                    }
                    ADSResult simplScenResult = simplifier.runCurrentSuggestedScenario(timeout);
                    if (simplScenResult.isError()) {
                        measureResult = "error";
                    } else {
                        measureResult = String.valueOf(simplScenResult.getMaxDanger().getTestMeasure());
                    }
                    boolean isSameResult = testComparator.sameResult(resultOriginalScenario, simplScenResult);
                    logger.info(Paths.get(simplifiedScenarioPath).getFileName().toString().replaceAll(".json", "") + "\t" + measureResult + "\t" + isSameResult + "\t" + simplScenResult.getNumDynamicObjects());
                    if (simplScenResult.isError()) {
                        continue;
                    }
                    simplifier.acceptSimplifiedScenario(isSameResult);
                    saveLogFile(simplScenResult, simplifiedScenarioPath, folderResultsLogs);
                } catch (Exception e) {
                    logger.info("Simplification failed!");
                    logger.info(e.getMessage());
                    e.getStackTrace();
                    return;
                }
            }
            Path pathLastAcceptedScenarioPath = Paths.get(simplifier.getPathLastAcceptedScenario());
            Files.copy(pathLastAcceptedScenarioPath, simplifier.getFolderResults().resolve(pathLastAcceptedScenarioPath.getFileName().toString().replaceAll(".json", "_simplifiedFinal.json")));
        } finally {
            Utils.closeFileHandler();
        }

    }

    private static Path setLoggers(String pathOriginalScenario, ADSResult resultOriginalScenario, Simplifier simplifier) throws IOException {
        Path folderResultsLogs = simplifier.getFolderResultsLogs();
        saveLogFile(resultOriginalScenario, pathOriginalScenario, folderResultsLogs);
        FileHandler fh = new FileHandler(simplifier.getFolderResults().resolve(Utils.LOG_FILENAME).toString());
        logger.addHandler(fh);
        fh.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage() + "\n";
            }
        });
        return folderResultsLogs;
    }

    private static void saveLogFile(ADSResult result, String pathScenario, Path folderLogs) throws IOException {
        String logFileName = folderLogs.resolve(Paths.get(pathScenario).getFileName().toString().replaceAll(".json", ".txt")).toString();
        BufferedWriter bw = new BufferedWriter(new FileWriter(logFileName));
        bw.append(result.printLog());
        bw.flush();
        bw.close();
    }

}
