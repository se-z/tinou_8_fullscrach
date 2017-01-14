package services;


/**
 * Created by seijihagawa on 2017/01/12.
 */
public class Block {
	private String mID;
	private Shape mShape;
	private boolean mHeavy;
	
	/**
	 * 
	 * @param aID ブロックの名前
	 * @param aShape 形の名前
	 * @param aHeavy 重いならtrue
	 */
	Block(String aID,String aShape,boolean aHeavy){
		mID=aID;
		mShape=Shape.valueOf(aShape);
		mHeavy=aHeavy;
	}
	
	/**
	 * 
	 * @return 重いならtrue
	 */
	public boolean isHeavey(){
		return mHeavy;
	}
	
	public String getID(){
		return mID;
	}
	public String getShapeName(){
		return mShape.getShapeName();
	}
	public boolean canBeOn(){
		return mShape.canBeOn();
	}
}
