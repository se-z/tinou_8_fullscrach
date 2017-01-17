package services;

/**
 * Created by seijihagawa on 2017/01/12.
 */

import java.util.ArrayList;

/**
 * SubGoalsで管理される木構造の一つのノードを表す
 */
public class Node {
    private Node mParentNode;
    private ArrayList<Node> mChildrenNodes;
    private Space mSpace;
    private ArrayList<Integer> mFalseChildren;
    private int mThisIndex; //同じ親を共有する個ノードの通し番号

    //rootNodeの実装用
    public Node(){
        mParentNode = null;
    }

    public Node(Node aParent, Space aSpace, int aIndex) {
        mFalseChildren = new ArrayList<>();
        mParentNode = aParent;
        mSpace = aSpace;
        mThisIndex = aIndex;
    }

    public void setChildrenNodes(ArrayList<Node> aChildren) {
        mChildrenNodes = aChildren;
    }

    public Node getChildNode(int aNumber) {
        return mChildrenNodes.get(aNumber);
    }

    public Space getSpace(){
        return mSpace;
    }

    public Node getParentNode(){
        return mParentNode;
    }

    public int getThisIndex(){
        return mThisIndex;
    }

    /**
     * @return すべての子ノードで失敗している場合、nullを返却
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
            return false;
        }
        return true;
    }

    public void putFalseChild(int aNumber) {
        mFalseChildren.add(aNumber);
    }

}
