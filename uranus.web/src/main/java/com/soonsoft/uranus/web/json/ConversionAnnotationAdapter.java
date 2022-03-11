package com.soonsoft.uranus.web.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.util.conversion.formatting.FormatType;
import com.soonsoft.uranus.util.conversion.formatting.Formatting;
import com.soonsoft.uranus.util.conversion.masking.Masking;
import com.soonsoft.uranus.web.json.serializer.FormattingSerializer;
import com.soonsoft.uranus.web.json.serializer.MaskingSerializer;

public class ConversionAnnotationAdapter extends JacksonAnnotationIntrospector {

    public ConversionAnnotationAdapter() {

    }

    @Override
    public Object findSerializer(Annotated annotated) {

        Masking maskingAnnotation = annotated.getAnnotation(Masking.class);
        if(maskingAnnotation != null && maskingAnnotation.value() != null) {
            return new MaskingSerializer(maskingAnnotation.value().get());
        }

        Formatting formattingAnnotation = annotated.getAnnotation(Formatting.class);
        if(formattingAnnotation != null) {
            FormatType[] formatTypes = formattingAnnotation.values();
            if(formatTypes != null && formatTypes.length > 0) {
                List<Func1<String, String>> formatterList = new ArrayList<>(formatTypes.length);
                for(int i = 0; i < formatTypes.length; i++) {
                    formatterList.add(formatTypes[i].get());
                }
                return new FormattingSerializer(formatterList);
            }
        }

        return super.findSerializer(annotated);
    }
    
}
