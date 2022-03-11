package com.soonsoft.uranus.web.json.serializer;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.func.Func1;

public class FormattingSerializer extends JsonSerializer<String> {

    private final List<Func1<String, String>> formatterList;

    public FormattingSerializer(List<Func1<String, String>> formatterList) {
        this.formatterList = formatterList;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String newValue = null;

        if(!StringUtils.isEmpty(value) && formatterList != null && !formatterList.isEmpty()) {
            newValue = value;
            for(Func1<String, String> formatter : formatterList) {
                newValue = formatter.call(newValue);
            }
        }

        if(newValue == null) {
            newValue = value;
        }

        gen.writeString(newValue);
        
    }
    
}
