package services;

public enum Shape {
	Box(true),
	Triangle(false),
	Trapezoid(true);
	
	private final boolean CanBeOn;
	
	private Shape(boolean aCanBeOn){
		CanBeOn=aCanBeOn;
	}
	
	/**
	 * 形の名前を返す
	 * @return
	 */
	public String getShapeName(){
		return this.name();
	}
	/**
	 * 上にブロックを乗せることが出来るならtrue
	 * @return
	 */
	public boolean canBeOn(){
		return CanBeOn;
	}
}
