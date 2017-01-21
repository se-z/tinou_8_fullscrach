package services;

/**
 * Created by seijihagawa on 2017/01/12.
 */

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Objects;

/**
 * SubGoalsで管理される木構造の一つのノードを表す
 */
public class Node {
    private Node mParentNode;
    private ArrayList<Node> mChildrenNodes;
    private Space mSpace;
    private ArrayList<Integer> mFalseChildren;
    private int mThisIndex; //同じ親を共有するノードの通し番号

    //rootNodeの実装用
    //初期状態はrootに持たせる
    public Node(Space aSpace) {
        mFalseChildren = new ArrayList<>();
        mParentNode = null;
        mSpace = aSpace;
    }

    /**
     * @param aParent
     * @param aSpace
     * @param aIndex  同じ親を共有するノードの通し番号で、同じ親、同じ高さのノードに対して０番からつける
     */
    public Node(Node aParent, Space aSpace, int aIndex) {
        mFalseChildren = new ArrayList<>();
        mParentNode = aParent;
        mSpace = aSpace;
        mThisIndex = aIndex;
    }


    //初期化処理で双方向リストの木構造を維持できている
    //ただし、導入の順序によってはバグが起きる可能性があるので注意が必要

    /**
     * @param aChildren 上のレイヤーで子ノードは親ノード、自分に登録する空間、自分のインデックスを登録しないといけない
     * @throws ConcurrentModificationException() 既にNodeが初期化されていた場合に返却
     */
    public void setChildrenNodes(ArrayList<Node> aChildren) {
        if (!Objects.equals(mChildrenNodes, null)) {
            throw new ConcurrentModificationException();
        }
        mChildrenNodes = new ArrayList<>(aChildren);
    }

    public Node getChildNode(int aNumber) {
        return mChildrenNodes.get(aNumber);
    }

    public Space getSpace() {
        return mSpace;
    }

    public Node getParentNode() {
        return mParentNode;
    }

    public int getThisIndex() {
        return mThisIndex;
    }

    /**
     * @return すべての子ノードで失敗している場合、nullを返却
     * 0番目からの順位優先度が高くなくてもこの関数は対応できる
     */
    public Node getNotFalseChild() {
        if (mChildrenNodes.size() == mFalseChildren.size()) {
            return null;
        }

        int tChildNum = 0;
        for (int i = 0; i < mChildrenNodes.size(); i++) {
            if (mFalseChildren.contains(i)) {
                continue;
            }

            tChildNum = i;
            break;
        }
        return mChildrenNodes.get(tChildNum);
    }

    public boolean HaveNotFalse() {
        if (mChildrenNodes.size() != mFalseChildren.size()) {
            return true;
        }
        return false;
    }

    /**
     * 失敗した子ノードをsetする
     *
     * @param aNumber 子ノードのインデックスは0番スタート
     */
    public void putFalseChild(int aNumber) {
        mFalseChildren.add(aNumber);
    }

}
