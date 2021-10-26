package capacitor.plugin.docline.sdk;

import android.util.Log;

public class DoclineSDK {

    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }
}
