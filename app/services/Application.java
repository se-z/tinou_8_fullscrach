package services;


/**
 * Created by seijihagawa on 2017/01/12.
 */

import services.JSON.OperationSeries;
import services.JSON.Request;
import services.JSON.block_req;
import services.naturalLanguage.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Application {

    private Request mRequest;
    private HashMap<String, Block> mBlocks;
    private String[] mIDs;
    private Space mInitialSpace;
    private int[] mX = new int[2];
    private int[] mY = new int[2];


    public Application(Request aRequest) {
        mRequest = aRequest;
        mX[0] = mRequest.getX().get(0);
        mX[1] = mRequest.getX().get(1);
        mY[0] = mRequest.getY().get(0);
        mY[1] = mRequest.getY().get(1);

        ArrayList<block_req> tBlocks = aRequest.getBlocks();
        mIDs = new String[tBlocks.size()];

        mInitialSpace = new Space(mRequest.getX().get(0), mRequest.getX().get(1),
                mRequest.getY().get(0), mRequest.getY().get(1));

        for (int i = 0; i < tBlocks.size(); i++) {
            block_req tb = tBlocks.get(i);
            Block tB = new Block(tb.getId(), tb.getShape(), tb.getHeavy());
            String tID = tb.getId();
            mIDs[i] = tID;
            int tX = tb.getCoordinate().get(0);
            int tY = tb.getCoordinate().get(1);
            mInitialSpace.addBlock(tID, tX, tY);
        }
    }

    public ArrayList<OperationSeries> run() {
        FieldData tFiled = new FieldData(mRequest);
        Target tTarget = new Target();
        Space[] tTargets = tTarget.getTargetList2(tFiled);
        Planner tPlanner = new Planner(tTargets, mBlocks, mIDs, mInitialSpace, mX, mY);
        ArrayList<OperationSeries> tOperationSeries = tPlanner.STRIPS();
        return tOperationSeries;
    }


}

