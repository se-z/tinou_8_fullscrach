package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by seijihagawa on 2017/01/12.
 */
public class Space {
	private HashMap<String,Position> mBlocks;//ブロックの座標
	private HashMap<String,int[]> mSpaceSize;//座標空間の広さ
	private HashMap<Integer,Integer> mXDepth;//穴の位置と深さ
	private ArrayList<String> mFixedBlocks;//副目標を達成し固定されたブロック
	private HashMap<String,Boolean> mUpwardFlag;//上にブロックが乗っているかのフラグ
	private class Position{//座標の情報を持つクラス
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
		mFixedBlocks=new ArrayList<String>();
		mUpwardFlag=new HashMap<String,Boolean>();
		
		for(int i=aMinX;i<=aMaxX;i++){
			mXDepth.put(i, 0);
		}
	}
	
	/**
	 * ブロックを追加する
	 * @param aID
	 * @param aX
	 * @param aY
	 * @return
	 */
	public boolean addBlock(String aID,int aX,int aY){
		if(mBlocks.containsKey(aID))//既に存在するブロックだった
			return false;
		
		if(!placeBlock(aID,aX,aY))
			return false;
		
		mUpwardFlag.put(aID,false);
		return true;
			
	}
	
	/**
	 * 指定した座標にあるブロックのIDを返す.
	 * ブロックがなければnullを返す
	 * @param aX
	 * @param aY
	 * @return
	 */
	public String getBlockID(int aX,int aY){
		for(Iterator<String>i=mBlocks.keySet().iterator();i.hasNext();){
			String tKey=i.next();
			Position tPosition=mBlocks.get(tKey);
			if(tPosition.getX()==aX&&tPosition.getY()==aY)
				return tKey;
		}
		return null;
	}
	
	
	/**
	 * 指定したブロックの座標を返す.
	 * @param aID
	 * @return
	 */
	public int[] getPosition(String aID){
		Position tPosition=mBlocks.get(aID);
		int[] tXY={tPosition.getX(),tPosition.getY()};
		return tXY;
	}
	
	/**
	 * 指定したブロックの上に何も載っていなければtrue.
	 * @param aID
	 * @return
	 */
	public boolean isClear(String aID){
		return mUpwardFlag.get(aID);
	}
	
	/**
	 * 指定した位置の地面の高さを返す(穴を掘っていると高さがマイナスになる).
	 * @param aX
	 * @return
	 */
	public int getGroudHight(int aX){
		return -mXDepth.get(aX);
	}
	
	/**
	 * 指定した座標の一番上にあるブロックの上の座標を返す,
	 * ブロックがなければ地面の高さを返す.
	 * @param aX
	 * @return
	 */
	public int getTop(int aX){
		for(Iterator<String>i=mBlocks.keySet().iterator();i.hasNext();){
			String tKey=i.next();
			Position tPosition=mBlocks.get(tKey);
			if(tPosition.getX()!=aX)
				continue;
			if(mUpwardFlag.get(tKey))
				continue;
			return tPosition.getY()+1;
		}
		return -mXDepth.get(aX);
	}
	
	/**
	 * ブロックを固定する
	 * @param aID 固定するブロックのID
	 */
	public void fixBlock(String aID){
		if(!mBlocks.containsKey(aID))//存在しないブロック名だった
			return;
		mFixedBlocks.add(aID);
	}
	
	
	/**
	 * 穴を掘る.
	 * ブロックがあって掘れなかった場合はfalse,
	 * 掘ることができたとき、または既に掘られていた場合はtrue
	 * @param aX 穴を掘る位置のx座標
	 * @param aDepth 穴の深さ
	 * @return
	 */
	public boolean dig(int aX,int aDepth){
		for(Iterator<String>i=mBlocks.keySet().iterator();i.hasNext();){
			String tKey=i.next();
			Position tPosition=mBlocks.get(tKey);
			if(tPosition.getX()!=aX)
				continue;
			if(tPosition.getY()>=-aDepth)//掘ろうとしている場所にブロックがある
				return false;
		}
		
		if(mXDepth.get(aX)<aDepth){
			mXDepth.put(aX,aDepth);
		}
		return true;
	}
	
	/**
	 * 次にブロックを移動させるときの、移動先の候補地を探す.
	 * @return 候補地のリスト
	 */
	public ArrayList<int[]> searchPositionBePlaced(){
		ArrayList<int[]> tReturned = new ArrayList<int[]>();
		
		int[] tXRange=mSpaceSize.get("x");
		HashMap<Integer,Boolean> tPlacedFlag=new HashMap<Integer,Boolean>();//それぞれのx座標にブロックがあるかどうか
		for(int i=tXRange[0];i<=tXRange[1];i++){
			tPlacedFlag.put(i, false);
		}
		
		//上に何もないブロックの上の座標を候補地に追加
		for(Iterator<String>i=mUpwardFlag.keySet().iterator();i.hasNext();){
			String tKey=i.next();
			if(mUpwardFlag.get(tKey)==false){
				Position tPosition=mBlocks.get(tKey);
				int[] tCandidate=new int[2];
				tCandidate[0]=tPosition.getX();
				tCandidate[1]=tPosition.getY()+1;
				tReturned.add(tCandidate);
				tPlacedFlag.put(tPosition.getX(), true);
			}
		}
		
		//何も置かれていない位置と、その位置の地面より下の位置を候補地に追加
		for(int i=tXRange[0];i<=tXRange[1];i++){
			if(tPlacedFlag.get(i))//ブロックが置かれている座標
				continue;
			
			int tGround=-mXDepth.get(i);//地面の高さ
			int tMinYRange=mSpaceSize.get("y")[0];//y座標の最小値
			for(int j=tGround;j>=tMinYRange;j--){
				int[] tCandidate=new int[2];
				tCandidate[0]=i;
				tCandidate[1]=j;
				tReturned.add(tCandidate);
			}
		}
		
		return tReturned;
	}
	
	/**
	 * ブロックを移動させる.
	 * 移動に成功したらtrueを返す
	 * @param aID
	 * @param aX
	 * @param aY
	 * @return
	 */
	public boolean moveBlock(String aID,int aX,int aY){
		if(!mBlocks.containsKey(aID))//存在しないブロックだった
			return false;
		for(String tID:mFixedBlocks){
			if(tID.equals(aID))//固定したブロックだった
				return false;
		}
		
		Position tPrePosition=mBlocks.get(aID);//移動前の座標を記憶
		
		if(!pickUpBlock(aID))//ブロックを持ち上げる
			return false;
		
		if(!placeBlock(aID,aX,aY)){//ブロックを置く
			placeBlock(aID,tPrePosition.getX(),tPrePosition.getY());//元の位置に戻す
			return false;
		}
		
		return true;
	}
	
	/**
	 * ブロックを持ち上げる
	 * @param aID
	 * @return
	 */
	public boolean pickUpBlock(String aID){
		if(mUpwardFlag.get(aID))//上にブロックが載っていた
			return false;
		
		Position tPosition=mBlocks.get(aID);
		String tBelowBlock=getBlockID(tPosition.getX(),tPosition.getY()-1);
		if(tBelowBlock!=null){
			mUpwardFlag.put(tBelowBlock,false);//真下のブロックのフラグをリセット
		}
		mBlocks.remove(aID);
		return true;
	}
	
	/**
	 * ブロックを置く
	 * @param aID
	 * @param aX
	 * @param aY
	 * @return
	 */
	public boolean placeBlock(String aID,int aX,int aY) {
		int[] tXRange=mSpaceSize.get("x");
		if(aX<tXRange[0]&&tXRange[1]<aX)//x座標が値域外
			return false;
		int [] tYRange=mSpaceSize.get("y");
		if(aY<tYRange[0]&&tYRange[1]<aY)//y座標が値域外
			return false;
		
		
		if(-mXDepth.get(aX)<aY){//地面より高い位置に置く
			String tBelowBlock=getBlockID(aX,aY-1);//移動先の真下のブロックのID
			if(tBelowBlock==null)//真下に何もない
				return false;
			if(mUpwardFlag.get(tBelowBlock))//移動先の座標にブロックがある
				return false;
			
			mUpwardFlag.put(tBelowBlock, true);//移動後に真下にあるブロックのフラグをセット
		}
		else if(-mXDepth.get(aX)==aY){//地面の上に置く
			if(getBlockID(aX,aY)!=null)//移動先の座標にブロックがある
				return false;
		}
		else{//地面より低い位置に置く
			if(!dig(aX,-aY))//穴を掘る
				return false;
		}
		
		Position tNewPosition=new Position(aX,aY);
		mBlocks.put(aID,tNewPosition);//ブロックを置く
		
		return true;
	}
	
	/**
	 * 現在の状態を複製して返す.
	 * @return
	 */
	public Space cloneSpace() {
		int[] tXRange=mSpaceSize.get("x");
		int[] tYRange=mSpaceSize.get("y");
		Space tReturned=new Space(tXRange[0],tXRange[1],tYRange[0],tYRange[1]);
		for(String tID:mBlocks.keySet()){
			Position tPosition=mBlocks.get(tID);
			Position tCPPosition=new Position(tPosition.getX(),tPosition.getY());
			tReturned.mBlocks.put(tID, tCPPosition);
		}
		for(Integer tX:mXDepth.keySet()){
			Integer tY=mXDepth.get(tX);
			tReturned.mXDepth.put(tX, tY);
		}
		for(String tID:mFixedBlocks){
			String tBlock=tID;
			tReturned.mFixedBlocks.add(tBlock);
		}
		for(String tID:mUpwardFlag.keySet()){
			tReturned.mUpwardFlag.put(tID, mUpwardFlag.get(tID));
		}
		return tReturned;
	}
}
