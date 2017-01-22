package services.naturalLanguage;

/**
 * Created by seijihagawa on 2017/01/22.
 */
public class block_nl {

    private String mID;
    private String mShape;
    private String mColor;

    public block_nl(String aID, String aShape, String aColor) {
        mID = aID;
        mShape = aShape;
        mColor = aColor;
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
        return(mID);
    }

    public String getShape() {
        return(mShape);
    }

    public String getColor() {
        return(mColor);
    }
}
