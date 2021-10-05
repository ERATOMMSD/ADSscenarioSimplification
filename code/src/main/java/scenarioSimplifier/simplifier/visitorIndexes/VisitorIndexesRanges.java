package scenarioSimplifier.simplifier.visitorIndexes;

import java.util.ArrayList;
import java.util.List;

public class VisitorIndexesRanges {
    private final NodeIndexesRange root;
    private NodeIndexesRange current;
    private final List<NodeIndexesRange> visited;
    //private Map<NodeIndexesRange, Integer> splitNumberByRange;

    public VisitorIndexesRanges(int start, int end, int splitNumberRoot) {
        root = new NodeIndexesRange(start, end, splitNumberRoot);
        current = root;
        visited = new ArrayList<>();
        visited.add(current);
        //this.splitNumberByRange = new HashMap<>();
        //this.splitNumberByRange.put(root, splitNumberRoot);
    }

    public NodeIndexesRange getNext(boolean increaseSplit) {
        if (increaseSplit) {
            //int newSplit = splitNumberByRange.get(current);
            int newSplit = current.getSplitNumber();
            newSplit = newSplit + 1;
            current.setSplitNumber(newSplit);
            //splitNumberByRange.put(current, newSplit);
        }
        List<NodeIndexesRange> partitions = current.getPartitions();
        if (partitions != null) {
            for (NodeIndexesRange nir : partitions) {
                if (nir != null && !visited.contains(nir)) {
                    current = nir;
                    visited.add(nir);
                    return current;
                }
            }
        }
        NodeIndexesRange parent = current;
        while ((parent = parent.getParent()) != null) {
            for (NodeIndexesRange partition : parent.getPartitions()) {
                if (partition != null && !visited.contains(partition)) {
                    current = partition;
                    visited.add(current);
                    //splitNumberByRange.put(current, parent.getSplitNumber());
                    return current;
                }
            }
        }
        current = null;
        return current;
    }

    public NodeIndexesRange getNextNotIncluded() {
        NodeIndexesRange rootSubTree = current;
        NodeIndexesRange nir = null;
        while ((nir = getNext(false)) != null) {
            if (!rootSubTree.isIncluding(nir)) {
                return nir;
            }
        }
        current = null;
        return current;
    }

    public NodeIndexesRange getCurrent() {
        return current;
    }
}
