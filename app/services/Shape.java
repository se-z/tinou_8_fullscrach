package services;


/**
 * ここの状態を変更した
 */
public enum Shape {
    四角(true),
    三角(false),
    台形(true);

    private final boolean CanBeOn;

    private Shape(boolean aCanBeOn) {
        CanBeOn = aCanBeOn;
    }

    /**
     * 形の名前を返す
     *
     * @return
     */
    public String getShapeName() {
        return this.name();
    }

    /**
     * 上にブロックを乗せることが出来るならtrue
     *
     * @return
     */
    public boolean canBeOn() {
        return CanBeOn;
    }
}