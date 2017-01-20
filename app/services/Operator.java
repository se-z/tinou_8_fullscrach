package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by seijihagawa on 2017/01/12.
 */
public abstract class Operator {
    protected HashMap<String, int[]> mSpaceSize;
    protected HashMap<String, Block> mBlocks;
    protected Space mTargetSpace; // 目標空間の状態

    /**
     * 座標空間の初期化
     *
     * @param aMinX x座標の最小値
     * @param aMaxX x座標の最大値
     * @param aMinY y座標の最小値
     * @param aMaxY y座標の最大値
     */
    Operator(int aMinX, int aMaxX, int aMinY, int aMaxY, HashMap<String, Block> aBlocks) {
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
     * どのblockに着目するかを判断し、着目したblockが移動できる座標の一覧を返す.
     *
     * @param aCurrentSpace     ブロックを移動させる前の状態
     * @param aSeries           移動させたブロックの系列
     * @param aSubTargetBlockID 副目標に設定されているブロックのID
     * @return 移動できる座標がない場合は、空のspace配列を返却
     * @throws CloneNotSupportedException
     */
    public Space[] findPositions(Space aCurrentSpace, ArrayList<String> aSeries, String aSubTargetBlockID) throws CloneNotSupportedException {

        String tMovingBlockID = choiceBlock(aCurrentSpace, aSeries, aSubTargetBlockID);
        if (Objects.equals(tMovingBlockID, null)) {
            return new Space[0];
        }

        // 移動できればおける座標
        ArrayList<int[]> tPlaceableSpace = new ArrayList<>();
        int mXmin = mSpaceSize.get("x")[0];
        int mXmax = mSpaceSize.get("x")[1];
        int mYmin = mSpaceSize.get("y")[0];

        for (int tX = mXmin; tX <= mXmax; tX++) {

            String tID = aCurrentSpace.getTopBlockID(tX);
            int tY = aCurrentSpace.getTopY(tX);

            if (!Objects.equals(tID, null) && mBlocks.get(tID).canBeOn()) {
                int[] tPlaceblePosition = {tX, tY};
                tPlaceableSpace.add(tPlaceblePosition);
                continue;
            }

            //Blockがない場合
            for (int y = tY; y >= mYmin; y--) {
                int[] tPlaceblePosition = {tX, y};
                tPlaceableSpace.add(tPlaceblePosition);
            }
        }


        ArrayList<int[]> tMoveable = new ArrayList<>();
        if (mBlocks.get(tMovingBlockID).isHeavy()) {
            //重いBlockの場合
            for (int[] tPlaceable : tPlaceableSpace) {
                int tX = tPlaceable[0];
                int tY = tPlaceable[1];
                int[] tGoal = {tX, tY};

                //目標空間よりも低い位置に、重たいブロックをおいたら、その時点で、解は達成されなくなる
                if (tY < mTargetSpace.getPosition(tMovingBlockID)[1]) {
                    continue;
                }

                if (aCurrentSpace.getPosition(tMovingBlockID)[0] < tY) {
                    continue;
                }

                if (!canSlideThrough(aCurrentSpace.getPosition(tMovingBlockID), tGoal, aCurrentSpace)) {
                    continue;
                }

                tMoveable.add(tGoal);
            }
        }
        //軽いBlockの場合
        else tMoveable = tPlaceableSpace;

        ArrayList<Space> tSpaceAfterMove = new ArrayList<>();
        //移動可能な座標リストから、移動後のSpaceのリストを生成
        for (int[] tNewPosition : tMoveable) {
            Space tNewSpace = aCurrentSpace.cloneSpace();
            tNewSpace.setBlockCloneSpace(tMovingBlockID, tNewPosition[0], tNewPosition[1]);
            tSpaceAfterMove.add(tNewSpace);
        }

        //評価関数の戻り値を返却
        return evaluateSpace(tSpaceAfterMove, tMovingBlockID, aSubTargetBlockID);
    }

    /**
     * 始点と終点の間を通り抜けられるかどうかと返す
     *
     * @param aStart
     * @param aGoal
     * @param aSpace
     * @return
     */

    private boolean canSlideThrough(int[] aStart, int[] aGoal, Space aSpace) {
        int tStartX = aStart[0];
        int tGoalX = aGoal[0];

        for (int x = tStartX; x < tGoalX; x++) {
            int tY = aSpace.getTopY(x);
            if (tY > aStart[1]) {
                return false;
            }

            if (tY == aSpace.getTopY(x)) {
                int tSameY = aStart[1];
                String tID = aSpace.getBlockID(x, tSameY - 1);

                if (Objects.equals(tID, null)) {
                    continue;
                }

                Block tBlock = mBlocks.get(tID);
                if (tBlock.canBeOn()) {
                    continue;
                }
                return false;
            }


            String tID = aSpace.getTopBlockID(x);
            if (Objects.equals(tID, null)) {
                continue;
            }
            Block tBlock = mBlocks.get(tID);
            if (tBlock.canBeOn()) {
                continue;
            }

            return false;

        }
        return true;
    }


    /**
     * これまでの移動系列、副目標、現在の座標を用いて、移動するBlockを決定する
     * 返却されるBlockは、上にBlockが乗っていないBlock
     *
     * @return 移動できるBlockがない場合はnullを返す
     * 返却値はObjects.equals(o1, o2):booleanでcheckする
     */
    abstract public String choiceBlock(Space aCurrentSpace, ArrayList<String> aSeries, String aSubTargetBlock);
//    {
//        //目標地点を受け取る
//        int[] tTargetPosition = mTargetSpace.getPosition(aRootBlockID);
//
//        //目標地点にブロックがあるなら、そのブロックかその上にあるブロック
//        int tHeight = aCurrentSpace.getTopY(tTargetPosition[0]);
//        if (tTargetPosition[1] < tHeight) {
//            String tBelowBlock = aCurrentSpace.getBlockID(tTargetPosition[0], tHeight - 1);//目標地点の最も上にあるブロック
//            if (tBelowBlock != null) {
//                return tBelowBlock;
//            }
//        }
//
//        //副目標に設定されているブロックの上にブロックがあるならそのブロック
//        int[] tNowPosition = aCurrentSpace.getPosition(aRootBlockID);//副目標に設定されているブロックの座標
//        tHeight = aCurrentSpace.getTopY(tNowPosition[0]);
//        if (tNowPosition[1] < tHeight - 1) {
//            return aCurrentSpace.getBlockID(tNowPosition[0], tHeight - 1);
//        }
//
//        //副目標に設定されているブロック
//        return aRootBlockID;
//    }

    /**
     * 評価関数
     *
     * @param tSpace       評価するSpaceのリスト
     * @param aChosenBlockID 移動したブロックのID
     * @param aRootBlockID 副目標として設定されているブロック
     * @return
     */
    abstract protected Space[] evaluateSpace(ArrayList<Space> aSpace, String aChosenBlockID, String aSubTargetBlockID);

}
