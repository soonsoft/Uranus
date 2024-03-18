package com.soonsoft.uranus.core.common.attribute;
import org.junit.Assert;
import org.junit.Test;

import com.soonsoft.uranus.core.common.attribute.DynamicEntityDefinition.Address;
import com.soonsoft.uranus.core.common.attribute.DynamicEntityDefinition.Person;
import com.soonsoft.uranus.core.common.attribute.access.ArrayDataAccessor;
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
    
}
