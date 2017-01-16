package services;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by seijihagawa on 2017/01/12.
 */
public abstract class Operator {
    protected HashMap<String, int[]> mSpaceSize;//座標空間の広さ
    protected Block mBlocks[];
    protected Space mTargetSpace;//目標空間

    /**
     * @param aMinX x座標の最小値
     * @param aMaxX x座標の最大値
     * @param aMinY y座標の最小値
     * @param aMaxY y座標の最大値
     */
    Operator(int aMinX, int aMaxX, int aMinY, int aMaxY, Block[] aBlocks) {
        mSpaceSize = new HashMap<String, int[]>();
        int tXRange[] = {aMinX, aMaxX};
        int tYRange[] = {aMinY, aMaxY};
        mSpaceSize.put("x", tXRange);
        mSpaceSize.put("y", tYRange);

        mBlocks = aBlocks;
    }

    /**
     * 目標空間をセット
     *
     * @param aSpace
     */
    public void setTargetSpace(Space aSpace) {
        mTargetSpace = aSpace;
    }

    /**
     * 移動させることが出来るブロックが１つ以上あるならtrue.
     *
     * @param aCurrentSpace
     * @return
     */
    public boolean canMove(Space aCurrentSpace) {

    }

    /**
     * どのblockに着目するかを判断し、着目したblockが移動できる座標の一覧を返す.
     *
     * @param aCurrentSpace ブロックを移動させる前の状態
     * @param aSeries       移動させたブロックの系列
     * @param aRootBlockID  副目標に設定されているブロックのID
     * @return
     * @throws CloneNotSupportedException
     */
    public Space[] findPositions(Space aCurrentSpace, ArrayList<String> aSeries, String aRootBlockID) throws CloneNotSupportedException {
        String tBlockID = decideMovedBlock(aCurrentSpace, aSeries, aRootBlockID);
    	
        if (aCurrentSpace.isClear(tBlockID))//移動させるブロックの上にブロックが載っている
            return new Space[0];

        //全てのブロックが四角形だと仮定したときにブロックを置くことができる座標のリストを受け取る
        ArrayList<int[]> tCandidate = aCurrentSpace.searchPositionBePlaced();
        ArrayList<int[]> tCandidateCopy=new ArrayList<int[]>();

        //移動させるブロックの真上を候補から外す
        int[] tPosition = aCurrentSpace.getPosition(tBlockID);
        removeFromList(tCandidate, tPosition[0], tPosition[1] + 1);

        copyList(tCandidate,tCandidateCopy);
        //移動先の真下が、上に物体を乗せることができないブロックの座標を候補から外す
        for (int[] tBelowPosition : tCandidateCopy) {
            String tBelowBlock = aCurrentSpace.getBlockID(tBelowPosition[0], tBelowPosition[1] - 1);
            if(tBelowBlock==null)
            	break;
            if (!canBeOn(tBelowBlock)) {
                removeFromList(tCandidate, tBelowPosition[0], tBelowPosition[1]);
            }
        }

        //移動させるブロックが重いブロックなら、移動できない座標を候補から外す
        if (isHeavy(tBlockID)) {
            removeHighPosition(tCandidate, tPosition[0], tPosition[1], aCurrentSpace);
        }

        ArrayList<Space> tNewSpaceList = new ArrayList<Space>();
        //移動後の状態のリストを作成
        for (int[] tCandidatePosition : tCandidate) {
            Space tNewSpace = aCurrentSpace.cloneSpace();
            if (tNewSpace.moveBlock(tBlockID, tCandidatePosition[0], tCandidatePosition[1])) {
                tNewSpaceList.add(tNewSpace);
            }
        }

        //評価関数で状態を並び替え、結果を返す
        return evaluateSpace(tNewSpaceList, aRootBlockID);
    }

    /**
     * 移動させるブロックを決める
     *
     * @param aCurrentSpace
     * @param aSeries
     * @param aRootBlockID
     * @return
     */
    public String decideMovedBlock(Space aCurrentSpace, ArrayList<String> aSeries, String aRootBlockID) {
        //目標地点を受け取る
        int[] tTargetPosition = mTargetSpace.getPosition(aRootBlockID);

        //目標地点にブロックがあるなら、そのブロックかその上にあるブロック
        int tHeight = aCurrentSpace.getTop(tTargetPosition[0]);
        if (tTargetPosition[1] < tHeight) {
            String tBelowBlock = aCurrentSpace.getBlockID(tTargetPosition[0], tHeight - 1);//目標地点の最も上にあるブロック
            if (tBelowBlock != null) {
                return tBelowBlock;
            }
        }

        //副目標に設定されているブロックの上にブロックがあるならそのブロック
        int[] tNowPosition = aCurrentSpace.getPosition(aRootBlockID);//副目標に設定されているブロックの座標
        tHeight = aCurrentSpace.getTop(tNowPosition[0]);
        if (tNowPosition[1] < tHeight - 1) {
            return aCurrentSpace.getBlockID(tNowPosition[0], tHeight - 1);
        }

        //副目標に設定されているブロック
        return aRootBlockID;
    }

    /**
     * 評価関数
     *
     * @param tSpace       評価するSpaceのリスト
     * @param aRootBlockID 副目標として設定されているブロック
     * @return
     */
    abstract protected Space[] evaluateSpace(ArrayList<Space> tSpace, String aRootBlockID);

    /**
     * 移動させるブロックが重いブロックのときに、移動できない座標をリストから削除する.
     * ブロックの前を通して移動させることができない場合
     *
     * @param aList
     * @param aX    移動前のx座標
     * @param aY    移動前のy座標
     */
    public void removeHighPosition(ArrayList<int[]> aList, int aX, int aY, Space aCurrentSpace) {
    	int tTargetHeight=mTargetSpace.getPosition(aCurrentSpace.getBlockID(aX, aY))[1];//移動させるブロックの目標状態での高さ
    	ArrayList<int[]> tCopy=new ArrayList<int[]>();
    	copyList(aList,tCopy);
        for (int[] tPosition : tCopy) {
        	if(tPosition[1]<tTargetHeight){//候補の座標が目標状態での高さより低い
        		removeFromList(aList,tPosition[0],tPosition[1]);
        		continue;
        	}
            int tNowHeight = aY;
            int tI = 1;
            if (tPosition[0] < aX)
                tI = -1;
            for (int tNowX = aX; tNowX != tPosition[0]; tNowX += tI) {
                int tNextHeight = aCurrentSpace.getTop(tNowX + tI);//隣の高さ
                if(tNextHeight<tTargetHeight){//隣の座標が目標状態での高さより低い
                	removeFromList(aList,tPosition[0],tPosition[1]);
                	break;
                }
                if (tNextHeight > tNowHeight) {//隣の座標のほうが高い
                    //穴に落としたブロックを、さらに深い穴に落とす操作を許可するなら、ここに条件を付ければ良いと思う
                	removeFromList(aList,tPosition[0],tPosition[1]);
                    break;
                }
                String tNextBelowBlock = aCurrentSpace.getBlockID(tNowX + tI, tNextHeight - 1);//隣の座標の一番上のブロック
                if (tNextBelowBlock != null) {
                    if (!canBeOn(tNextBelowBlock)) {//隣の座標の一番上のブロックが、上に物体を乗せることが出来ないブロックだった
                    	removeFromList(aList,tPosition[0],tPosition[1]);
                        break;
                    }
                }
                tNowHeight = tNextHeight;
            }
        }
    }

    /**
     * 引数のリストから、引数の座標の要素を削除する.
     *
     * @param aList
     * @param aX
     * @param aY
     */
    private void removeFromList(ArrayList<int[]> aList, int aX, int aY) {
        for (int[] tPosition : aList) {
            if (tPosition[0] == aX && tPosition[1] == aY) {
                aList.remove(tPosition);
                return;
            }
        }
    }
    
    /**
     * リストをコピーする
     * @param aList1 コピーするリスト
     * @param aList2 コピー先のリスト
     */
    private void copyList(ArrayList<int[]> aList1,ArrayList<int[]> aList2){
    	aList2.clear();
    	for(int[] tPosition:aList1){
    		int[] tCpPosition=new int[2];
    		tCpPosition[0]=tPosition[0];
    		tCpPosition[1]=tPosition[1];
    		
    		aList2.add(tCpPosition);
    	}
    }

    /**
     * 指定したブロックは上にブロックを置くことができるかどうか.
     *
     * @param aID
     * @return
     */
    public boolean canBeOn(String aID) {
        for (Block tBlock : mBlocks) {
            if (tBlock.getID().equals(aID)) {
                return tBlock.canBeOn();
            }
        }
        return false;
    }

    /**
     * 指定したブロックが重いかどうか
     *
     * @param aID
     * @return
     */
    public boolean isHeavy(String aID) {
        for (Block tBlock : mBlocks) {
            if (tBlock.getID().equals(aID)) {
                return tBlock.isHeavy();
            }
        }
        return false;
    }
}
