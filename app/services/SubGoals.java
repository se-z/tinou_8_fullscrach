package services;

/**
 * Created by seijihagawa on 2017/01/12.
 */

import java.util.ArrayList;
import java.util.Objects;

/**
 * SubGoals spaceオブジェクトの系列を管理 トラック、バックトラックを行うインターフェースを提供
 * <p>
 * 木構造で管理しているが、別に木構造でなくても良い。このクラスはGoalsのノード数の回数だけ生成される
 * 全体的に、まだアルゴリズムの工夫の余地があると思う
 */
public class SubGoals {
    private Node mRootNode;
    private Node mCurrentNode;  //このNodeの挙動は何度か見直しが必要 scopeが広いでのミスが起きそう

    /**
     * mCurrentNodeは根ノードの参照で初期化する
     */
    SubGoals(Space aInitSpace) {
        mRootNode = new Node(aInitSpace);
        mCurrentNode = mRootNode;
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
     * spaceの登録と同時にtrackする
     *
     * @param aSpaces 0番目から優先順位の高いデータが入っているとする
     * @return void
     * <p>
     * 双方向の木構造なので、子ノードから親ノードの登録
     * 親ノードから、子ノードの登録の両方を行う
     */
    public void track(Space[] aSpaces) {

        ArrayList<Node> tChildren = new ArrayList<>();

        for (int i = 0; i < aSpaces.length; i++) {
            Space tSpace = aSpaces[i];
            Node tChild = new Node(mCurrentNode, tSpace, i);
            tChildren.add(tChild);
        }
        mCurrentNode.setChildrenNodes(tChildren);
        mCurrentNode = mCurrentNode.getChildNode(0);
    }


    /**
     * すべての枝でbackTrackが失敗した場合、falseを返却する
     * whileの最初のif文でエラーがでないか心配
     * <p>
     * この返却値にはまだ変更できる余地があるような気がする
     */
    public boolean backTrack() {
        while (true) {

            //mCurrentNode == 根ノード
            if (Objects.equals(mCurrentNode.getParentNode(), null)) {
                return false;
            }

            Node tParentNode = mCurrentNode.getParentNode();
            tParentNode.putFalseChild(mCurrentNode.getThisIndex());
            if (!tParentNode.HaveNotFalse()) {
                //メモリが無駄だから、失敗したらそもそも参照を消した方がいいのでは？
                tParentNode.putFalseChild(mCurrentNode.getThisIndex());
                mCurrentNode = tParentNode;
                continue;
            }

            mCurrentNode = tParentNode.getNotFalseChild();
            break;

        }
        return true;
    }


    /**
     * 現在のノードから、backTrackで通過した系列をListとして返却
     *
     * @return 関数の終了後も、木構造におけるmCurrentNodeの位置に変化はない
     * ここでは、自分のspaceを含むべき
     */
    public ArrayList<Space> getSubSeries() {
        ArrayList<Space> tSeries = new ArrayList<Space>();
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
     * @return Spaceに新たにTargetIDをもたせたので、バグがないか不安
     * ここでは、現在着目してるIDは含まないべき
     */
    public ArrayList<String> getSeriesIDs() {
        ArrayList<String> tIDs = new ArrayList<>();
        Node tMoveNode = mCurrentNode.getParentNode();

        //現在, root ノードにいる場合
        if (Objects.equals(tMoveNode, null)) {
            return tIDs;
        }

        tIDs.add(tMoveNode.getSpace().getMovingID());
        while (true) {

            if (Objects.equals(tMoveNode.getParentNode(), null)) {
                break;
            }
            tMoveNode = tMoveNode.getParentNode();
            tIDs.add(0, tMoveNode.getSpace().getMovingID()); //先頭に要素を追加
        }
        return tIDs;
    }

    public void fixSubTarget() {
        Space tSpace = mCurrentNode.getSpace();
        String tID = tSpace.getMovingID();
        mCurrentNode.getSpace().fix(tID);
    }

}
