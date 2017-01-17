package services;

import java.util.ArrayList;

//移動先の候補の座標の優先順位をランダムに決定する
public class RandomOperator extends Operator{

	RandomOperator(int aMinX, int aMaxX, int aMinY, int aMaxY, Block[] aBlocks) {
		super(aMinX, aMaxX, aMinY, aMaxY, aBlocks);
	}

	protected Space[] evaluateSpace(ArrayList<Space> tSpace, String aRootBlockID){
		int tSize=tSpace.size();
		Space[] tReturned=new Space[tSize];
		
		for(int i=0;i<tSize;i++){
			tReturned[i]=tSpace.remove((int)(Math.random()*(tSize-i)));
		}
		return tReturned;
	}
}
