package services;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by seijihagawa on 2017/01/12.
 */
public class Operator {
	private HashMap<String,int[]>mSpaceSize;//座標空間の広さ
	private Block mBlocks[];
	private Space mTargetSpace;//目標空間
	
	/**
	 * 
	 * @param aMinX x座標の最小値
	 * @param aMaxX x座標の最大値
	 * @param aMinY y座標の最小値
	 * @param aMaxY y座標の最大値
	 */
	Operator(int aMinX,int aMaxX,int aMinY,int aMaxY,Block[] aBlocks){
		mSpaceSize=new HashMap<String,int[]>();
		int tXRange[]={aMinX,aMaxX};
		int tYRange[]={aMinY,aMaxY};
		mSpaceSize.put("x", tXRange);
		mSpaceSize.put("y", tYRange);
		
		mBlocks=aBlocks;
	}
	
	/**
	 * 目標空間をセット
	 * @param aSpace
	 */
	public void setTargetSpace(Space aSpace){
		mTargetSpace=aSpace;
	}
	
	/**
	 * どのblockに着目するかを判断し、着目したblockが移動できる座標の一覧を返す.
	 * @param aCurrentSpace ブロックを移動させる前の状態
	 * @param aSeries 移動させたブロックの系列
	 * @param aRootBlockID 副目標に設定されているブロックのID
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Space[] findPositions(Space aCurrentSpace,ArrayList<String> aSeries,String aRootBlockID) throws CloneNotSupportedException{
		String aBlockID=decideMovedBlock(aCurrentSpace,aSeries,aRootBlockID);
		
		if(!aCurrentSpace.isClear(aBlockID))//移動させるブロックの上にブロックが載っている
			return new Space[0];
		
		//全てのブロックが四角形だと仮定したときにブロックを置くことができる座標のリストを受け取る
		ArrayList<int[]>tCandidate=aCurrentSpace.searchPositionBePlaced();
		
		//移動させるブロックの真上を候補から外す
		int[] tPosition=aCurrentSpace.getPosition(aBlockID);
		removeFromList(tCandidate,tPosition[0],tPosition[1]+1);
		
		//移動先の真下が、上に物体を乗せることができないブロックの座標を候補から外す
		for(int[] tBelowPosition:tCandidate){
			String tBelowBlock=aCurrentSpace.getBlockID(tBelowPosition[0], tBelowPosition[1]-1);
			if(!canBeOn(tBelowBlock)){
				removeFromList(tCandidate,tBelowPosition[0],tBelowPosition[1]);
			}
		}
		
		//移動させるブロックが重いブロックなら、移動できない座標を候補から外す
		if(isHeavy(aBlockID)){
			removeHighPosition(tCandidate,tPosition[0],tPosition[1]);
		}
		
		ArrayList<Space> tNewSpaceList=new ArrayList<Space>();
		//移動後の状態のリストを作成
		for(int[] tCandidatePosition:tCandidate){
			Space tNewSpace=aCurrentSpace.cloneSpace();
			if(tNewSpace.moveBlock(aBlockID, tCandidatePosition[0], tCandidatePosition[1])){
				tNewSpaceList.add(tNewSpace);
			}
		}
		
		//評価関数で状態を並び替え、結果を返す
		return evaluateSpace(tNewSpaceList,aRootBlockID);
	}
	
	/**
	 * 移動させるブロックを決める
	 * @param aCurrentSpace
	 * @param aSeries
	 * @param aRootBlockID
	 * @return
	 */
	public String decideMovedBlock(Space aCurrentSpace,ArrayList<String> aSeries,String aRootBlockID){
		
	}
	
	/**
	 * 評価関数
	 * @param tSpace 評価するSpaceのリスト
	 * @param aRootBlockID 副目標として設定されているブロック
	 * @return
	 */
	abstract protected Space[] evaluateSpace(ArrayList<Space> tSpace,String aRootBlockID);
	
	/**
	 * 移動させるブロックが重いブロックのときに、移動できない座標をリストから削除する
	 * @param aList
	 * @param aX 移動前のx座標
	 * @param aY 移動前のy座標
	 */
	private void removeHighPosition(ArrayList<int[]> aList,int aX,int aY){
		
	}
	
	/**
	 * 引数のリストから、引数の座標の要素を削除する.
	 * @param aList
	 * @param aX
	 * @param aY
	 */
	private void removeFromList(ArrayList<int[]> aList,int aX,int aY){
		for(int[] tPosition:aList){
			if(tPosition[0]==aX&&tPosition[1]==aY){
				aList.remove(tPosition);
				return;
			}
		}
	}
	
	/**
	 * 指定したブロックは上にブロックを置くことができるかどうか.
	 * @param aID
	 * @return
	 */
	public boolean canBeOn(String aID){
		for(Block tBlock:mBlocks){
			if(tBlock.getID().equals(aID)){
				return tBlock.canBeOn();
			}
		}
		return false;
	}
	
	/**
	 * 指定したブロックが重いかどうか
	 * @param aID
	 * @return
	 */
	public boolean isHeavy(String aID){
		for(Block tBlock:mBlocks){
			if(tBlock.getID().equals(aID)){
				return tBlock.isHeavy();
			}
		}
		return false;
	}
}
