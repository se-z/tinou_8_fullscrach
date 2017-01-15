package controllers;
import play.mvc.Controller;
import play.mvc.Result;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by seijihagawa on 2016/12/06.
 */
public class HomeController extends Controller {


    //
    //private final String kFilePath = "/Users/seijihagawa/java/3rdClass/kadai8_play_fullScrach/app/views/index.html";
    private final String kFilePath = "C:/play/activator-dist-1.3.12/bin/tinou_8_fullscrach/app/views/index.html";
    /**
     * アプリケーションが起動されて、localHostに対するアクセスに対するレスポンスを返す関数.
     * @return
     * @throws IOException
     * 最初に発火される関数
     * 名前の変更はできない
     */
    public Result index() throws IOException {

        try {
            FileInputStream tFile = new FileInputStream(kFilePath);
            return ok(tFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


}
