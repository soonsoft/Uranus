package com.soonsoft.uranus.core.common.attribute;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.soonsoft.uranus.core.common.attribute.DynamicEntityDefinition.Address;
import com.soonsoft.uranus.core.common.attribute.DynamicEntityDefinition.Person;
import com.soonsoft.uranus.core.common.attribute.access.ArrayDataAccessor;
import com.soonsoft.uranus.core.common.attribute.access.DefaultAttributeJsonTemplate;
import com.soonsoft.uranus.core.common.attribute.access.StructDataAccessor;

public class AttributeJsonTemplateTest {
    
    private AttributeBagFactory bagFactory = new AttributeBagFactory();

    @Test
    public void test_SampleJson() {
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor person = bag.getEntityOrNew("Person");
        
        person.setValue("Jack", Person.Name);

        ArrayDataAccessor array = person.newArray("CellPhoneNumber");
        array.addValue("138-0920-8909", Person.CellPhoneNumber);
        array.addValue("186-7689-1234", Person.CellPhoneNumber);

        StructDataAccessor bothAddress = person.newStruct("Address", "BothAddress");
        bothAddress.setValue("江苏省", Address.Province);
        bothAddress.setValue("南京市", Address.City);
        bothAddress.setValue("江宁区", Address.District);
        bothAddress.setValue("将军大道100号", Address.Detail);
    
        IAttributeJsonTemplate jsonTemplate = bag.toJSON();
        String expectedJSON = "{\"Person\":{\"Name\":\"Jack\",\"CellPhoneNumber\":[\"138-0920-8909\",\"186-7689-1234\"],\"BothAddress\":{\"Province\":\"江苏省\",\"City\":\"南京市\",\"District\":\"江宁区\",\"Detail\":\"将军大道100号\"}}}";
        Assert.assertTrue(expectedJSON.equals(jsonTemplate.getJSON()));

    }

    @Test
    public void test_Jackson() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule myModule = new SimpleModule("AttributeBagJsonModule");
        myModule.addSerializer(IAttributeBag.class, new JsonSerializer<IAttributeBag>() {
            @Override
            public void serialize(IAttributeBag value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                IAttributeJsonTemplate jsonTemplate = value.toJSON();
                if(jsonTemplate instanceof DefaultAttributeJsonTemplate defaultTemplate) {
                    // 先完成局部Json，然后合并到全局中
                    gen.writeRawValue(defaultTemplate.getJSON());
                }
            }
        });
        mapper.registerModule(myModule);

        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor person = bag.getEntityOrNew("Person");

        person.setValue("中文姓名", Person.Name);

        ArrayDataAccessor array = person.newArray("CellPhoneNumber");
        array.addValue("138-0920-8909", Person.CellPhoneNumber);
        array.addValue("186-7689-1234", Person.CellPhoneNumber);

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("code", "0");
        map.put("message", "请求成功");
        map.put("description", "AttributeBag Jackson Test");
        map.put("myAttributeBag", bag);

        try {
            String json = mapper.writeValueAsString(map);
            System.out.println(json);
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
}
