package lk.cmh.app.ceylonmarkethub.data.util;

import com.google.gson.Gson;

public class GsonUtil {

    private static Gson gson;

    private GsonUtil() {

    }

    public static Gson getInstance() {
        if(gson == null) {
            gson = new Gson();
        }
        return gson;
    }

}
