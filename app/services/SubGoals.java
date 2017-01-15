package services;

/**
 * Created by seijihagawa on 2017/01/12.
 */

import java.util.ArrayList;
import java.util.Objects;

/**
 * SubGoals
 * targetになっているブロックの目標状態を達成する。系列をバックトラックのために管理。
 * 木構造で管理しているが、別に木構造でなくても良い。このクラスはGoalsのノード数の回数だけ生成される
 * <p>
 * ・getSeries(aNodeName String):Space[]
 * (親ノードをたどって動作の系列を取得, このbackTrace時に穴の情報も追加する？)
 * ・backTrack(aNodeName String):Space
 */

public class SubGoals {
    private Node mRootNode;
    private Node mCurrentNode;
    private String[] mMovedBlocks;  //targetのブロックではなく、今動かしているブロック


    /**
     * 初期化の手順はSTRIPSの実装で決める
     */
    SubGoals() {
        mRootNode = new Node();
    }

    /**
     * @return
     */
    public Space getCurrentSpace() {
        return mCurrentNode.getSpace();
    }


    /**
     * すべての枝でbackTrackが失敗した場合、falseを返却する
     * whileの最初のif文でエラーがでないか心配
     */
    public boolean backTrack() {
        while (true) {

            if (Objects.equals(mCurrentNode.getParentNode(), null)) {
                return false;
            }

            Node tParentNode = mCurrentNode.getParentNode();
            if (tParentNode.HaveNotFalse()) {
                tParentNode.putFalseChild(mCurrentNode.getThisIndex());
                mCurrentNode = tParentNode;
                continue;
            }

            mCurrentNode = tParentNode;
            mCurrentNode.getNotFalseChild();
            break;

        }
        return true;
    }


    /**
     * 現在のノードから、backTrackで通過した系列をListとして返却
     * @return
     * 関数の終了後も、木構造におけるmCurrentNodeの位置に変化はない
     */
    public ArrayList<Space> getSeries() {
        ArrayList<Space> tSeries = new ArrayList<>();
        Node tMoveNode = mCurrentNode;

        tSeries.add(tMoveNode.getSpace());
        while(true){

            if (Objects.equals(tMoveNode.getParentNode(), null)) {
                break;
            }
            tMoveNode = tMoveNode.getParentNode();
            tSeries.add(tMoveNode.getSpace());
        }
        return tSeries;
    }

}
