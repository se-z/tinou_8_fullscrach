package services;

/**
 * Created by seijihagawa on 2017/01/12.
 */

import java.util.HashMap;

/**
 * 現在の状態が目標状態になっているかを確かめ、プランを適用する
 * <p>
 * Planner
 * 現在の状態が目標状態に
 * なっているかを確かめ、
 * プランを適用する
 * + method(type): type
 * ・STRIPS(): ここの内部実装が結構複雑そう
 * ・matchState():boolean
 * ・setTargetBlock()
 */

public class Planner {
    private Goals mGoals;
    private HashMap<String, SubGoals> mSubGoalsList;
    private Space[] mTargetOptions;
    private static Block[] mBlocks; // このクラスはこの書き方でいいのか？
    // private   オペレータが必要


    /**
     *
     * @param aTargetOptions
     * @param aBlocks
     * @param aIDs データ的には重複するけど、IDはこの上のそうで処理するのが良い adapterクラス作る
     * <p> 各IDに対応する、SubGoalsを生成する
     */
    Planner(Space[] aTargetOptions, Block[] aBlocks, String[] aIDs) {

        mTargetOptions = aTargetOptions;
        mBlocks = aBlocks;
        mGoals = new Goals(aIDs);
        mSubGoalsList = new HashMap<>();

    }

    /**
     * Goalsの系列に対して、その系列をOperationに投げる
     */
    public OperationSeries[] STRIPS() {
        String tTarget = mGoals.getCurrentTarget();
        // while (){
        // if(現状の空間が次の空間に繊維可能でなければ)
        // { backTrack または、目標状態の系列の着替えを行う}
        // 以下成功パターン
        // tTargetをOperatorに渡して、OperatorからSpaceの一覧をもらう
        // tSpaceをmSubGoalsListの一つのListに代入。
        // この状態を繰り返す
        // すべての状態が達成されたらbreak
        // }

        // for(mapのすべての値について){
        // getSeriresを行う
        // }
        // return OperationSeries

    }


}
