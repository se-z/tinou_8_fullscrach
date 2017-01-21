package services;

import java.util.ArrayList;
import java.util.HashMap;

public class EvaluationOperator extends Operator {
    public EvaluationOperator(int aMinX, int aMaxX, int aMinY, int aMaxY,
                              HashMap<String, Block> aBlocks) {
        super(aMinX, aMaxX, aMinY, aMaxY, aBlocks);
    }

    @Override
    public String choiceBlock(Space aCurrentSpace, ArrayList<String> aSeries, String aSubTargetBlock) {
        return AdditionalChoice.choiceBlock(aCurrentSpace, mTargetSpace, mBlocks, aSeries, aSubTargetBlock, mSpaceSize.get("x")[0], mSpaceSize.get("x")[1]);
    }

    @Override
    protected Space[] evaluateSpace(ArrayList<Space> aSpace, String aChosenBlockID, String aSubTargetBlockID) {
        return AdditionalEvaluation.evaluateSpace(aSpace, mTargetSpace, mBlocks, aChosenBlockID, aSubTargetBlockID);
    }
}