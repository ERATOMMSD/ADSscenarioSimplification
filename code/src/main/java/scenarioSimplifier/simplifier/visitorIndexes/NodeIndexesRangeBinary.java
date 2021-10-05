package scenarioSimplifier.simplifier.visitorIndexes;

public class NodeIndexesRangeBinary {
    private final int start;
    private final int end;
    private NodeIndexesRangeBinary left;
    private NodeIndexesRangeBinary right;
    private NodeIndexesRangeBinary parent;

    public NodeIndexesRangeBinary(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public NodeIndexesRangeBinary(int start, int end, NodeIndexesRangeBinary parent) {
        this(start, end);
        this.parent = parent;
    }

    public NodeIndexesRangeBinary getLeft() {
        if (left == null && start != end && start <= ((start + end) / 2)) {
            left = new NodeIndexesRangeBinary(start, (start + end) / 2, this);
        }
        return left;
    }

    public NodeIndexesRangeBinary getRight() {
        if (right == null && start != end && ((start + end) / 2) + 1 <= end) {
            right = new NodeIndexesRangeBinary(((start + end) / 2) + 1, end, this);
        }
        return right;
    }

    /**
     * @param otherRange
     * @return whether the current range includes otherRange
     */
    public boolean isIncluding(NodeIndexesRangeBinary otherRange) {
        return otherRange != null && start <= otherRange.start && end >= otherRange.end;
    }

    public boolean isIncluding(int index) {
        return start <= index && end >= index;
    }

    public NodeIndexesRangeBinary getParent() {
        return parent;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getSize() {
        return (end - start) + 1;
    }

    @Override
    public String toString() {
        return start + ", " + end;
    }
}
