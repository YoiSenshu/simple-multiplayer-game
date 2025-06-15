package pl.yoisenshu.smg.world;

public record Position (
    int x,
    int y
) {
    public float distanceTo(Position other) {
        return (float) Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2));
    }

    public Position add(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy);
    }

    public Position add(Position other) {
        return new Position(this.x + other.x, this.y + other.y);
    }

    public Position scale(float multiplier) {
        return new Position((int) (x * multiplier), (int) (y * multiplier));
    }
}
