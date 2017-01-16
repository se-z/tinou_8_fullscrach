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
 * <p>
 * <p>
 * + field: type
 * ・mGoals:Goals
 * ・mSubGoals:
 * map(key:id, value:subGoals)
 * ・mTargetSpace:Space
 * ・mTargetOptions:Space[]
 * ・mBlocks:Block[]
 * ・mCurrentSpace:Space
 * ・mTargetBlockID:string
 * <p>
 * <p>
 * + method(type): type
 * ・STRIPS(): ここの内部実装が結構複雑そう
 * ・matchState():boolean
 * ・setTargetBlock()
 */

public class Planner {
    private Goals mGoals;
    private HashMap<String, SubGoals> mSubGoals;
    private Space mTargetSpace; //これいらないかも
    private Space[] mTargetOptions;
    private Space mCurrentSpace;
    private String mTargetBlockID;   //これグローバル変数として持つ必要ないかもしれない
    private static Block[] mBlocks; // このクラスはこの書き方でいいのか？

    Planner(Space[] aTargetOptions, Block[] aBlocks) {
        mTargetOptions = aTargetOptions;
        mBlocks = aBlocks;
        mGoals = new Goals();
        mSubGoals = new HashMap<>();
        mTargetSpace = new Space();
        mCurrentSpace = new Space();
        mTargetBlockID = new String();

    }


    /**
     * Goalsの系列に対して、その系列をOperationに投げる
     */
    public OperationSeries[] STRIPS() {


    }


}
