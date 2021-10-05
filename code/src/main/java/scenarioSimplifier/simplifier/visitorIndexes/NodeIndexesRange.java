package scenarioSimplifier.simplifier.visitorIndexes;

import java.util.ArrayList;
import java.util.List;

public class NodeIndexesRange {
    private final int start;
    private final int end;
    private List<NodeIndexesRange> partitions;
    private NodeIndexesRange parent;
    private int splitNumber;

    public NodeIndexesRange(int start, int end, int splitNumber) {
        this.start = start;
        this.end = end;
        this.splitNumber = splitNumber;
    }

    public NodeIndexesRange(int start, int end, int splitNumber, NodeIndexesRange parent) {
        this(start, end, splitNumber);
        this.parent = parent;
    }

    public List<NodeIndexesRange> getPartitions() {
        if (partitions == null && start < end) {
            partitions = new ArrayList<>();
            int partitionSize = (int) Math.ceil((end - start) / splitNumber);
            int newStart = start;
            while (newStart <= end) {
                int newEnd = newStart + partitionSize;
                if (newEnd > end) {
                    newEnd = end;
                }
                partitions.add(new NodeIndexesRange(newStart, newEnd, splitNumber, this));
                newStart = newEnd + 1;
            }
        }
        return partitions;
    }

    /**
     * @param otherRange
     * @return whether the current range includes otherRange
     */
    public boolean isIncluding(NodeIndexesRange otherRange) {
        return otherRange != null && start <= otherRange.start && end >= otherRange.end;
    }

    public boolean isIncluding(int index) {
        return start <= index && end >= index;
    }

    public NodeIndexesRange getParent() {
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

    public void setSplitNumber(int splitNumber) {
        this.splitNumber = splitNumber;
    }

    public int getSplitNumber() {
        return splitNumber;
    }
}
