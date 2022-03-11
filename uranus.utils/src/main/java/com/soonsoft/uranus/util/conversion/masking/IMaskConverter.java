package com.soonsoft.uranus.util.conversion.masking;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.util.conversion.IConverter;

public interface IMaskConverter extends IConverter<String, String> {

    Func1<String, String> get();

    static String getStars(int count) {
        if(count <= 0) {
            return StringUtils.Empty;
        }

        switch(count) {
            case 1 : return "*";
            case 2 : return "**";
            case 3 : return "***";
            case 4 : return "****";
            case 5 : return "*****";
            case 6 : return "******";
            case 7 : return "*******";
            case 8 : return "********";
            case 9 : return "*********";
            default : {
                String[] stars = new String[count];
                for(int i = 0; i < stars.length; i++) {
                    stars[i] = "*";
                }
                return StringUtils.join(StringUtils.Empty, stars);
            }
        }
    }
    
}
