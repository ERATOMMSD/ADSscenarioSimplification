package ads;

import java.util.List;

public abstract class ADSScenario {
    abstract public void removeDynamicObjects(List<Integer> indexes);

    abstract public int getNumDynamicObjects();

    abstract public List<String> getNumDynamicObjectsIDs();
}
