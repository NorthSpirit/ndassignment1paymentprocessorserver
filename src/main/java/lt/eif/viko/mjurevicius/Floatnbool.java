package lt.eif.viko.mjurevicius;

public class Floatnbool {
    private boolean boolToHold;
    private float floatToHold;

    public Floatnbool(boolean boolToHold, float floatToHold) {
        this.boolToHold = boolToHold;
        this.floatToHold = floatToHold;
    }

    public boolean isBoolToHold() {
        return boolToHold;
    }

    public void setBoolToHold(boolean boolToHold) {
        this.boolToHold = boolToHold;
    }

    public float getFloatToHold() {
        return floatToHold;
    }

    public void setFloatToHold(float floatToHold) {
        this.floatToHold = floatToHold;
    }
}
