package services;

/**
 * Created by seijihagawa on 2017/01/12.
 */

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 現在の状態が目標状態になっているかを確かめ、プランを適用する
 *
 */

public class Planner {
    private Goals mGoals;
    private HashMap<String, SubGoals> mSubGoals;
    private Space mTargetSpace; //これいらないかも
    private Space[] mTargetOptions;
    private Space mCurrentSpace;
    private String mTargetBlockID;   //これグローバル変数として持つ必要ないかもしれない
    private static  Block[] mBlocks; // このクラスはこの書き方でいいのか？

    Planner(Space[] aTargetOptions, Block[] aBlocks){
        mTargetOptions = aTargetOptions;
        mBlocks = aBlocks;
        mGoals = new Goals();
        mSubGoals = new HashMap<>();
        mTargetSpace = new Space();
        mCurrentSpace = new Space();
        mTargetBlockID = new String();

    }


    /**
     *
     * Goalsの系列に対して、その系列をOperationに投げる
     */
    public OperationSeries[] STRIPS(){


    }



}
