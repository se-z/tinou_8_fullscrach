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
 */

public class SubGoals {
    private Node mRootNode;
    private Node mCurrentNode;  //このNodeの挙動は何度か見直しが必要 scopeが広いでのミスが起きそう

    /**
     * 初期化の手順はSTRIPSの実装で決める
     */
    SubGoals() {
        mRootNode = new Node();
    }

    /**
     * 今着目しているNodeに記録されているSpaceを取り出す
     *
     * @return
     */
    public Space getCurrentSpace() {
        return mCurrentNode.getSpace();
    }

    /**
     * @param aSpaces 一番目から優先順位の高いデータが入っているとする
     */
    public void putSpaces(ArrayList<Space> aSpaces) {

        ArrayList<Node> tChildren = new ArrayList<>();

        for (int i = 0; i < aSpaces.size(); i++) {
            Space tSpace = aSpaces.get(i);
            Node tChild = new Node(mCurrentNode, tSpace, i);
            tChildren.add(tChild);
        }
        mCurrentNode.setChildrenNodes(tChildren);
    }

    /**
     * すべての枝でbackTrackが失敗した場合、falseを返却する
     * whileの最初のif文でエラーがでないか心配
     * Spaceの
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
     *
     * @return 関数の終了後も、木構造におけるmCurrentNodeの位置に変化はない
     */
    public ArrayList<Space> getSubSeries() {
        ArrayList<Space> tSeries = new ArrayList<>();
        Node tMoveNode = mCurrentNode;

        tSeries.add(tMoveNode.getSpace());
        while (true) {

            if (Objects.equals(tMoveNode.getParentNode(), null)) {
                break;
            }
            tMoveNode = tMoveNode.getParentNode();
            tSeries.add(0, tMoveNode.getSpace()); //先頭に要素を追加
        }
        return tSeries;
    }


    /**
     * 要実装 sub木構造で今まで連続して動かしてきたBlockの名前の系列が渡される
     *
     * @return
     */
    public ArrayList<String> getBlockSeries() {
        return null;

    }

}
