package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RandomOperator extends Operator {

    RandomOperator(int aMinX, int aMaxX, int aMinY, int aMaxY,
                   HashMap<String, Block> aBlocks) {
        super(aMinX, aMaxX, aMinY, aMaxY, aBlocks);
    }
    
    /**
     * 移動先の候補の座標の優先順位をランダムに決定する
     * @param tSpace 移動先候補の座標のリスト
     * @param aSubTargetBlockID 副目標に設定されているBlockのID
     */
    protected Space[] evaluateSpace(ArrayList<Space> tSpace, String aSubTargetBlockID) {
        int tSize = tSpace.size();
        Space[] tReturned = new Space[tSize];

        for (int i = 0; i < tSize; i++) {
            tReturned[i] = tSpace.remove((int) (Math.random() * (tSize - i)));
        }
        return tReturned;
    }
    
    /**
     * 次に移動させるBlockを乱数を含む評価関数を用いて決定する
     * @param aCurrentSpace
     * @param aSeries
     * @param aSubTargetBlock
     */
    public String choiceBlock(Space aCurrentSpace, ArrayList<String> aSeries, String aSubTargetBlock){
    	HashMap<String,Integer> tEvaluate=new HashMap<>();

    	//評価を行い結果をMapに記録
    	int[] tXSize=mSpaceSize.get("x");
    	for(int x=tXSize[0];x<=tXSize[1];x++){
    		String tBlockID=aCurrentSpace.getTopBlockID(x);
    		if(Objects.equals(tBlockID,null)){
    			continue;
    		}
    		if(tBlockID.equals(aSeries.get(0))){//移動系列との比較（aSeriesで保持されているブロックの順番について要確認）
    			continue;
    		}
    		
    		//評価値を計算
    		Integer tValue=evaluationFunction(aCurrentSpace,aSubTargetBlock);

    		tEvaluate.put(tBlockID, tValue);
    	}
    	

    	//評価値が最も高いBlockを選択
    	Integer tMaxValue=null;
    	String tChosenBlock=null;;
    	for(HashMap.Entry<String,Integer> tEvaluation:tEvaluate.entrySet()){
    		if(Objects.equals(tMaxValue, null)){//初回は必ず更新
    			tMaxValue=tEvaluation.getValue();
    			tChosenBlock=tEvaluation.getKey();
    		}
    		if(tEvaluation.getValue() < tMaxValue){
    			tMaxValue=tEvaluation.getValue();
    			tChosenBlock=tEvaluation.getKey();
    		}
    	}

    	return tChosenBlock;
    }
    
    /**
     * 移動させるブロックを決定するための評価関数
     * @param aCurrentSpace
     * @param aSubTargetBlock
     * @return
     */
    public int evaluationFunction(Space aCurrentSpace,String aSubTargetBlock){
    	int tValue=0;
    	
    	//評価を行う
    	
    	
    	return tValue;
    }
}
