package services;

import java.util.ArrayList;

public class RandomEvaluation {

    /**
     * 移動先の候補の座標の優先順位をランダムに決定する
     * @param tSpace 移動先候補の座標のリスト
     * @param aSubTargetBlockID 副目標に設定されているBlockのID
     */
    protected static Space[] evaluateSpace(ArrayList<Space> tSpace, String aSubTargetBlockID) {
        int tSize = tSpace.size();
        Space[] tReturned = new Space[tSize];

        for (int i = 0; i < tSize; i++) {
            tReturned[i] = tSpace.remove((int) (Math.random() * (tSize - i)));
        }
        return tReturned;
    }
}
