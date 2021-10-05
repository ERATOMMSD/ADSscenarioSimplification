package ads.testMeasure;

public abstract class TestMeasure {
    public abstract boolean bigger(TestMeasure other);

    public abstract boolean smaller(TestMeasure other);

    public abstract double getTestMeasure();

}
