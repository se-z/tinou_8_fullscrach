package services;

/**
 * Created by seijihagawa on 2017/01/12.
 */

import java.util.ArrayList;
import java.util.HashMap;

import services.JSON.*;
import services.JSON.OperationSeries;

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
        mOperator = new EvaluationOperator(aX[0], aX[1], aY[0], aY[1], aBlocks);
    }

    /**
     * Goalsの系列に対して、その系列をOperationに投げる
     * <p>
     * <p>
     * Goalsの系列でバックトラックをしない実装にしてみた
     * 未テスト
     */
    public ArrayList<OperationSeries> STRIPS() {

        outside:
        for (Space tTargetSpace : mTargetOptions) {
            System.out.println("#####################################################################");
            System.out.println("全体での目標状態");
            System.out.println(tTargetSpace);


            mGoals.randomSetSeries();
            System.out.println("副目標の達成順序は");
            System.out.println(mGoals.getCurrentList());


            mOperator.setTargetSpace(tTargetSpace);
            Space tInitial = mInitialSpace;


            for (int i_goals = 0; i_goals < k_GOALS_TRACK_TIME; i_goals++) {

                System.out.println("副目標の初期化");
                SubGoals tSubGoals = new SubGoals(tInitial);
                String tSubTargetID = mGoals.getCurrentTarget();
                mSubGoalsList.put(tSubTargetID, tSubGoals);
                System.out.println("服目標を展開するListのサイズ");
                System.out.println(mSubGoalsList.size());


                for (int k_subs = 0; k_subs < k_SUB_TRACK_TIME; k_subs++) {

                    //副目標が達成されてた場合
                    if (isSubGoal(tSubGoals, tTargetSpace)) {
                        //ここでbreakするならば、副目標とのズレがあってはいけない
                        if (mGoals.isLast()) {
                            System.out.println("最後の副目標が終了する場合は、服目標とGoalsのIDは一致していないといけない");
                            System.out.println(mGoals.getCurrentTarget());
                            System.out.println(mSubGoalsList.get(mGoals.getCurrentTarget()).getSeriesIDs());
                            break outside;
                        }

                        System.out.println("すべての副目標は達成されていないが、副目標が達成されたので、Goalsを更新する");
                        tSubGoals.fixSubTarget(); //副目標を固定する
                        tInitial = tSubGoals.getCurrentSpace().cloneSpace();
                        mGoals.setNextTarget();
                        break;

                    }

                    //この関数はまだ確認しきれていない
                    Space[] tTrack = mOperator.findPositions(tSubGoals.getCurrentSpace(),
                            tSubGoals.getSeriesIDs(), tSubTargetID);

                    //backTrack
                    if (tTrack.length == 0) {
                        if (tSubGoals.backTrack()) {
                            continue;
                        }

                        //副目標の達成順序を初期化
                        mGoals.randomSetSeries();
                        mSubGoalsList.clear();
                        mSubGoalsList.put(mGoals.getCurrentTarget(), new SubGoals(mInitialSpace));
                        tInitial = mInitialSpace;
                        break;
                    }

                    //Track
                    tSubGoals.track(tTrack);
                }


            }
        }

        //移動系列の取得 返却
        ArrayList<OperationSeries> tOperation = new ArrayList<>();
        String[] tList = mGoals.getCurrentList();
        for (int i = 0; i < mSubGoalsList.size(); i++) {
            SubGoals tSub = mSubGoalsList.get(tList[i]);
            ArrayList<Space> tSubSpaces = tSub.getSubSeries();
            for (Space tSpace : tSubSpaces) {
                String tID = tSpace.getMovingID();
                int[] tXY = tSpace.getPosition(tID);
                System.out.println(tID);
                System.out.println(tXY);
                OperationSeries tOpe = new OperationSeries();
                tOpe.setId(tID);
                tOpe.setnewPosition(tXY[0], tXY[1]);
                tOperation.add(tOpe);
            }
        }

        return tOperation;
    }

    /**
     * @param aSub
     * @param aTarget
     * @return 全体を通して、フィールドmGoalsのスコープが広すぎる。
     * 内部を何度も更新されるのに、スコープが広すぎる
     */
    private boolean isSubGoal(SubGoals aSub, Space aTarget) {
        String tSubTarget = mGoals.getCurrentTarget();
        //目標座標
        int tSubX = aSub.getCurrentSpace().getPosition(tSubTarget)[0];
        int tSubY = aSub.getCurrentSpace().getPosition(tSubTarget)[1];

        //実際の座標
        int tX = aTarget.getPosition(tSubTarget)[0];
        int tY = aTarget.getPosition(tSubTarget)[1];


        if (tSubX == tX && tSubY == tY) {
            return true;
        }
        return false;
    }


}