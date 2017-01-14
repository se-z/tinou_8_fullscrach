package services;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by seijihagawa on 2017/01/12.
 */
public class Space {
	private HashMap<String,Position> mBlocks;//ブロックの座標
	private HashMap<String,int[]> mSpaceSize;//座標空間の広さ
	private HashMap<Integer,Integer> mXDepth;//穴の位置と深さ
	private ArrayList<String> mFixBlock;//副目標を達成し固定されたブロック
	private String mLatestMovedBlock;//最後に移動したブロックのID
	private HashMap<String,Boolean> mUpwardFlag;//上にブロックが乗っているかのフラグ
	class Position{//座標の情報を持つクラス
		private int mX;
		private int mY;
		Position(int aX,int aY){
			mX=aX;
			mY=aY;
		}
		public void setPosition(int aX,int aY){
			mX=aX;
			mY=aY;
		}
		public int getX(){
			return mX;
		}
		public int getY(){
			return mY;
		}
	}
	/**
	 * 
	 * @param aMinX x座標の最小値
	 * @param aMaxX x座標の最大値
	 * @param aMinY y座標の最小値
	 * @param aMaxY y座標の最大値
	 */
	Space(int aMinX,int aMaxX,int aMinY,int aMaxY){
		mSpaceSize=new HashMap<String,int[]>();
		int tXRange[]={aMinX,aMaxX};
		int tYRange[]={aMinY,aMaxY};
		mSpaceSize.put("x", tXRange);
		mSpaceSize.put("y", tYRange);
		
		mBlocks=new HashMap<String,Position>();
		mXDepth=new HashMap<Integer,Integer>();
		mFixBlock=new ArrayList<String>();
		mUpwardFlag=new HashMap<String,Boolean>();
	}
	
	/**
	 * ブロックを追加する
	 * @param aID
	 * @param aX
	 * @param aY
	 */
	public void addBlock(String aID,int aX,int aY){
		Position tPosition=new Position(aX,aY);
		mBlocks.put(aID,tPosition);
		
		//フラグのセット
		
		
	}
}
