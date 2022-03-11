package com.soonsoft.uranus.web.json.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.func.Func1;

public class MaskingSerializer extends JsonSerializer<String> {

    private final Func1<String, String> stringValueConverter;

    public MaskingSerializer(Func1<String, String> converter) {
        this.stringValueConverter = converter;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if(!StringUtils.isEmpty(value) && stringValueConverter != null) {
            gen.writeString(stringValueConverter.call(value));
        } else {
            gen.writeString(value);
        }
        
    }
    
}
