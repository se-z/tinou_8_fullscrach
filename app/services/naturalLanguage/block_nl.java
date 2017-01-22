package services.naturalLanguage;

/**
 * Created by seijihagawa on 2017/01/22.
 */
public class block_nl {

    private String mID;
    private String mShape;
    private int[] mCoordinate = new int[2];

    public block_nl(String aID, String aShape, int[] aCoordinate) {
        mID = aID;
        mShape = aShape;
        mCoordinate = aCoordinate;
    }

    public void setId(String aID) {
        mID = aID;
    }

    public void setShape(String aShape) {
        mShape = aShape;
    }

    public void setCoordinate(int[] aCoordinate) {
        mCoordinate[0] = aCoordinate[0];
        mCoordinate[1] = aCoordinate[1];
    }


}
