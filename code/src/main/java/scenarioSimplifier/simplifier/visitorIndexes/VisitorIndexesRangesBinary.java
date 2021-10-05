package scenarioSimplifier.simplifier.visitorIndexes;

import java.util.ArrayList;
import java.util.List;

public class VisitorIndexesRangesBinary {
    private final NodeIndexesRangeBinary root;
    private NodeIndexesRangeBinary current;
    private final List<NodeIndexesRangeBinary> visited;

    public VisitorIndexesRangesBinary(int start, int end) {
        root = new NodeIndexesRangeBinary(start, end);
        current = root;
        visited = new ArrayList<>();
        visited.add(current);
    }

    public NodeIndexesRangeBinary getNext() {
        for (NodeIndexesRangeBinary nir : new NodeIndexesRangeBinary[]{current.getLeft(), current.getRight()}) {
            if (nir != null && !visited.contains(nir)) {
                current = nir;
                visited.add(nir);
                return current;
            }
        }
        NodeIndexesRangeBinary parent = current;
        while ((parent = parent.getParent()) != null) {
            NodeIndexesRangeBinary right = parent.getRight();
            if (right != null && !visited.contains(right)) {
                current = right;
                visited.add(right);
                return current;
            }
        }
        current = null;
        return current;
    }

    public NodeIndexesRangeBinary getNextNotIncluded() {
        NodeIndexesRangeBinary rootSubTree = current;
        NodeIndexesRangeBinary nir = null;
        while ((nir = getNext()) != null) {
            if (!rootSubTree.isIncluding(nir)) {
                return nir;
            }
        }
        current = null;
        return current;
    }

    public NodeIndexesRangeBinary getCurrent() {
        return current;
    }

}
