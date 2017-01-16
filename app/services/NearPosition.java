package services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//目標状態と同じ位置にあるブロックが多いほど高評価
public class NearPosition extends Operator{
	
	NearPosition(int aMinX, int aMaxX, int aMinY, int aMaxY, Block[] aBlocks) {
		super(aMinX, aMaxX, aMinY, aMaxY, aBlocks);
		// TODO Auto-generated constructor stub
	}

	protected Space[] evaluateSpace(ArrayList<Space> aSpace,String aRootBlockID){
		Map<Space,Integer> tEvaluation=new HashMap<Space,Integer>();
		for(Space tSpace:aSpace){
			int tE=0;//評価値
			for(Block tBlock:mBlocks){
				String tName=tBlock.getID();
				int[] tTargetPosition=mTargetSpace.getPosition(tName);//目標状態の座標
				int[] tNextPosition=tSpace.getPosition(tName);//移動先候補の状態の座標
				if(tTargetPosition[0]==tNextPosition[0]&&tTargetPosition[1]==tNextPosition[1]){//目標状態と同じ座標
					tE++;
				}
			}
			tEvaluation.put(tSpace,tE);
		}
		
        // List 生成 (ソート用)
        List<Entry<Space, Integer>> tEntries =
              new ArrayList<Map.Entry<Space,Integer>>(tEvaluation.entrySet());
        Collections.sort(tEntries, new Comparator<Map.Entry<Space,Integer>>() {
 
            @Override
            public int compare(
                  Entry<Space,Integer> entry1, Entry<Space,Integer> entry2) {
                return ((Integer)entry2.getValue()).compareTo((Integer)entry1.getValue());
            }
        });
         
        //配列に入れる
        int tSize=aSpace.size();
        Space[] tReturned =new Space[tSize];
        int tNum=0;
        for(Entry<Space,Integer> tS : tEntries){
        	tReturned[tNum]=tS.getKey();
        	tNum++;
        }
        
        return tReturned;
	}
}
