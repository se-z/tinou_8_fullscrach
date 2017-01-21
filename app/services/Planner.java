package services;

/**
 * Created by seijihagawa on 2017/01/12.
 */

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 現在の状態が目標状態になっているかを確かめ、プランを適用する
 */

public class Planner {
    private Goals mGoals;
    private final HashMap<String, SubGoals> mSubGoalsList;
    private Space[] mTargetOptions;
    private final Space mInitialSpace;   //GUIで設定されている最初の空間
    private final HashMap<String, Block> mBlocks; // このクラスはこの書き方でいいのか？
    private final String[] mIDs;   //再代入不可の修飾子 final
    private final int k_SUB_TRACK_TIME = 10000;
    private final int k_GOALS_TRACK_TIME = 30;
    private Operator mOperator;


    /**
     * @param aTargetOptions
     * @param aBlocks
     * @param aIDs           データ的には重複するけど、IDはこの上の層で処理するのが良い adapterクラス作る
     *                       <p> 各IDに対応する、SubGoalsを生成する
     */
    Planner(Space[] aTargetOptions, HashMap<String, Block> aBlocks, String[] aIDs, Space aInitialSpace, int[] aX, int[] aY) {
        mTargetOptions = aTargetOptions;
        mBlocks = aBlocks;
        mIDs = aIDs;
        mGoals = new Goals(mIDs);
        mSubGoalsList = new HashMap<>();
        mInitialSpace = aInitialSpace;
        mOperator = new RandomOperator(aX[0], aX[1], aY[0], aY[1], aBlocks);
    }

    /**
     * Goalsの系列に対して、その系列をOperationに投げる
     * <p>
     * ここの実装がきたなすぎるので、書き直したい....
     */
    public OperationSeries[] STRIPS() {

        for (Space tTargetSpace : mTargetOptions) {

            //初期化処理 Goalsの初期化
            mGoals.randomSet();

            //subGoalsの初期化
            for (String tID : mIDs) {
                SubGoals tSubGoals = new SubGoals();
                mSubGoalsList.remove(tID);
                mSubGoalsList.put(tID, tSubGoals);
            }


            //推論開始
            //大域的な木構造のバックトラックに関する関数
            for (int i_goals = 0; i_goals < k_GOALS_TRACK_TIME; i_goals++) {


                ArrayList<Space> tSpace = new ArrayList<>();
                tSpace.add(mInitialSpace);
                SubGoals tSub = mSubGoalsList.get(mGoals.getCurrentTarget());
                tSub.putSpaces(tSpace);
                String tTargetBlockID = mGoals.getCurrentTarget();

                for (int k_subGoals = 0; k_subGoals < k_SUB_TRACK_TIME; k_subGoals++) {

                    Space tCurrentSpace = tSub.getCurrentSpace();
                    //subGoalが達成されているならば、-> break このタイミング
                    ArrayList<String> tSeries = tSub.getBlockSeries();

                    try {
                        // ここの例外処理は正しく書く
                        Space[] tNewSpaces = mOperator.findPositions(tCurrentSpace, tSeries, tTargetBlockID);

                        //backtrack
                        if (tNewSpaces.length == 0) {
                            if (!tSub.backTrack()) {
                                continue;
                            }
                            break; // 内側のsubGoalsのfor文を抜けてるつもり
                        }

                        //track
                        ArrayList<Space> tSpaceList = convertFrom(tNewSpaces);
                        tSub.putSpaces(tSpaceList);

                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }

                //もし、すべての副目標が達成されているならば、
                //系列をすべて取り出して、返却をする
            }
        }

        //失敗したので、空の系列を返す
        return  null;
    }


    private ArrayList<Space> convertFrom(Space[] aSpace) {
        ArrayList<Space> tSpaceList = new ArrayList<>();
        for (int l = 0; l < aSpace.length; l++) {
            tSpaceList.add(aSpace[l]);
        }
        return tSpaceList;
    }


}
