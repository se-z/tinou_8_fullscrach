package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AdditionalChoice {

    //評価関数で使用するパラメータ
    //コメントの最後に示す式の値を加算する(kは変数名の略称)
    //加点減点にかかわらず値は加算されるので、減点するときはパラメータを負の値にする
    //副目標のブロック,副目標のブロックの目標地点　 +(近いほうの距離×k)減点
    static final int kPointDistance = 5;
    //x座標が、副目標のブロックの現在座標または目標座標と等しいなら　+(k)加点
    static final int kPointOnTargetPosition = 5;
    //副目標のブロックより高い位置にあるなら　+(k)加点
    static final int kPointHighThanTarget = 5;
    //副目標のブロックより低い位置にあり、ブロックを乗せられるなら　+(k)減点
    static final int kPointCanBeOn = 5;
    //副目標のブロックより低い位置にあり、ブロックを乗せられないなら　+(k)加点
    static final int kPointCannotBeOn = 5;
    //重いブロックなら　+(k)減点
    static final int kPointIsHeavy = 5;
    //地中にあるなら +(穴の深さ×k)減点
    static final int kPointUnderground = 5;

    //評価値に加えられる乱数の最大値　+(0～k)
    static final int kPointRandom = 5;

    /**
     * 次に移動させるBlockを乱数を含む評価関数を用いて決定する
     *
     * @param aCurrentSpace
     * @param aTargetSpace
     * @param aBlocks
     * @param aSeries
     * @param aSubTargetBlock
     * @param aXMin           x座標の最小値
     * @param aXMax           x座標の最大値
     */
    public static String choiceBlock(Space aCurrentSpace, Space aTargetSpace, HashMap<String, Block> aBlocks, ArrayList<String> aSeries, String aSubTargetBlock, int aXMin, int aXMax) {
        HashMap<String, Integer> tEvaluate = new HashMap<>();

        //評価を行い結果をHashMapに記録
        for (int x = aXMin; x <= aXMax; x++) {
            String tBlockID = aCurrentSpace.getTopBlockID(x);
            if (Objects.equals(tBlockID, null)) {
                continue;
            }
            if (tBlockID.equals(aSeries.get(aSeries.size() - 1))) {//最後に移動させたブロックは選択しない
                continue;
            }

            //評価値を計算
            Integer tValue = evaluationFunction(tBlockID, aCurrentSpace, aTargetSpace, aBlocks, aSubTargetBlock);
            //評価値を記録
            tEvaluate.put(tBlockID, tValue);
        }


        //評価値が最も高いBlockを選択
        Integer tMaxValue = null;
        String tChosenBlock = null;
        ;
        for (HashMap.Entry<String, Integer> tEvaluation : tEvaluate.entrySet()) {
            if (Objects.equals(tMaxValue, null)) {//初回は必ず更新
                tMaxValue = tEvaluation.getValue();
                tChosenBlock = tEvaluation.getKey();
            }
            if (tEvaluation.getValue() < tMaxValue) {
                tMaxValue = tEvaluation.getValue();
                tChosenBlock = tEvaluation.getKey();
            }
        }

        return tChosenBlock;
    }

    /**
     * 移動させるブロックを決定するための評価関数
     *
     * @param aBlockID        評価するブロックのID
     * @param aCurrentSpace
     * @param aTargetSpace
     * @param aBlocks
     * @param aSubTargetBlock
     * @return
     */
    public static int evaluationFunction(String aBlockID, Space aCurrentSpace, Space aTargetSpace, HashMap<String, Block> aBlocks, String aSubTargetBlock) {
        int tValue = 0;

        //評価を行う
        int[] tChosenPosition = aCurrentSpace.getPosition(aBlockID);//評価するブロックの現在の座標
        int[] tTargetCurrentPosition = aCurrentSpace.getPosition(aSubTargetBlock);//副目標のブロックの現在の座標
        int[] tTargetGoalPosition = aTargetSpace.getPosition(aSubTargetBlock);//副目標のブロックの目標地点の座標

        //x座標の評価
        int tDistance = Math.abs(tTargetGoalPosition[0] - tChosenPosition[0]);//副目標のブロックの目標地点の座標とのx方向の距離
        if (tDistance > Math.abs(tTargetCurrentPosition[0] - tChosenPosition[0]))
            tDistance = Math.abs(tTargetCurrentPosition[0] - tChosenPosition[0]);//副目標のブロックの現在の座標とのx方向の距離
        if (tDistance == 0) tValue += kPointOnTargetPosition;//距離が0なら加点
        else tValue += tDistance * kPointDistance;//遠いほど減点

        //y座標の評価
        if (tChosenPosition[1] >= tTargetCurrentPosition[1]) {//副目標のブロックより高いor同じ高さ
            tValue += kPointHighThanTarget;
        } else {//副目標のブロックより低い
            if (aBlocks.get(aBlockID).canBeOn()) {//評価するブロックが上にブロックを乗せられるか
                tValue += kPointCanBeOn;
            } else {
                tValue += kPointCannotBeOn;
            }
        }

        //重さの評価
        if (aBlocks.get(aBlockID).isHeavy())
            tValue += kPointIsHeavy;

        //地中にあるか評価
        int tY = aCurrentSpace.getPosition(aBlockID)[1];
        if (tY < 0) {
            tValue += -tY * kPointUnderground;
        }

        //乱数を加算
        tValue += (int) (Math.random() * kPointRandom);

        return tValue;
    }
}
