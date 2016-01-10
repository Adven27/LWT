package com.example.android.navigationdrawerexample.util;

/**
 * Created by adven on 26.05.14.
 */
public class StubHelper {

    public static String getLongText(int paragraphCount) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 1; i <= paragraphCount; i++) {
            sb.append(//"“You’re not stuck with the brain you have.” " +
                    "Larry welcomes a panel of experts to discuss the power we have over our " +
                            "brain health - and the roles nutrition, mental games, exercise & meditation" +
                            " play in optimizing our noggins and staving off dementia.\n" +
                            "From Manischewitz to iPhones: Two of the world’s most famous octogenarians " +
                            "chat about the trials of aging, the wonders of the Internet, and whether " +
                            "aliens exist. Plus, an impromptu wine tasting! And bagels!\n"
            );
        }
        return sb.toString();
    }

}
