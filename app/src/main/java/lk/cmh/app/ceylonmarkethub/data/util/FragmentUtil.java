package lk.cmh.app.ceylonmarkethub.data.util;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import lk.cmh.app.ceylonmarkethub.R;

public class FragmentUtil {

    public static void loadFragment(Fragment fragment, @IdRes int containerViewId, FragmentActivity context) {
        context.getSupportFragmentManager().beginTransaction().replace(containerViewId, fragment).commit();
    }

}
