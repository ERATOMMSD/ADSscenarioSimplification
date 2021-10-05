package ads.testMeasure;

public class DangerMeasure extends TestMeasure {
    private final double distance;
    private final double speed;
    private final boolean collision;

    public DangerMeasure(double distance, double speed, boolean crash) {
        this.distance = distance;
        this.speed = speed;
        this.collision = crash;
    }

    public boolean bigger(TestMeasure other) {
        if (other == null) return true;
        // if one collided and the other didn't, we don't need to measure anything else
        if (this.collision && !((DangerMeasure) other).collision) return true;
        if (!this.collision && ((DangerMeasure) other).collision) return false;
        return this.getTestMeasure() > other.getTestMeasure();
    }

    public boolean smaller(TestMeasure other) {
        if (other == null) return true;
        if (this.collision && !((DangerMeasure) other).collision) return false;
        if (!this.collision && ((DangerMeasure) other).collision) return true;

        return this.getTestMeasure() < other.getTestMeasure();
    }

    public double getTestMeasure() {
        if (collision) {
            return speed;
        } else {
            return speed / Math.pow(distance, 2);
        }
    }

    public boolean isCollision() {
        return collision;
    }

}
