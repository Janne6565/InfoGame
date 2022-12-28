public class position {
    public double x;
    public double y;

    public position(double pX, double pY) {
        x = pX;
        y = pY;
    }

    public boolean greaterThan(position pos) {
        if (pos.x < x) { // ja, man könnte das wesentlich kleiner schreiben. Dann wird es aber schnell unübersichtlich
            if (pos.y < y) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean lessThan(position pos) {
        if (pos.x > x) {
            if (pos.y > y) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public position addOn(double addX, double addY) {
        return new position(x + addX, y + addY);
    }
}
