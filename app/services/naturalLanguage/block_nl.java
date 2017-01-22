package services.naturalLanguage;

/**
 * Created by seijihagawa on 2017/01/22.
 */

import services.JSON.block_req;

public class block_nl {

    private String mID;
    private String mShape;
    private String mColor;

    public block_nl(String aID, String aShape, String aColor) {
        mID = aID;
        mShape = aShape;
        mColor = aColor;
    }

    public block_nl(block_req aBlock) {
        mID = aBlock.getId();
        mShape = aBlock.getShape();
        mColor = aBlock.getShape();
    }

    public void setId(String aID) {
        mID = aID;
    }

    public void setShape(String aShape) {
        mShape = aShape;
    }

    public void setCoordinate(String aColor) {
        mColor = aColor;
    }

    public String getId() {
        return (mID);
    }

    public String getShape() {
        return (mShape);
    }

    public String getColor() {
        return (mColor);
    }
}
