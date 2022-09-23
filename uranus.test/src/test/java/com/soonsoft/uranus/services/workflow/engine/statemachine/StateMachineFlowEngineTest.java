package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;

public class StateMachineFlowEngineTest {

    private StateMachineFlowFactory<StateMachineFlowDataQuery> factory;

    @Before
    public void initial() {
        StateMachineFlowRepository repository = new StateMachineFlowRepository();
        DataSubQuery query = new DataSubQuery();
        factory = new StateMachineFlowFactory<StateMachineFlowDataQuery>(repository, query);
    }

    @Test
    public void test_query() {
        Object parameter = null;
        StateMachineFlowDefinition definition = factory.loadDefinition(parameter);
        StateMachineFLowEngine<StateMachineFlowDataQuery> engine = factory.createEngine(definition);
        List<String> codes = engine.query().queryAllCodes();
        Assert.assertTrue(codes.size() == 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_extensionQuery() {
        Object factoryObj = factory;
        StateMachineFlowFactory<DataSubQuery> factory2 = (StateMachineFlowFactory<DataSubQuery>) factoryObj;
        
        Object parameter = null;
        StateMachineFLowEngine<DataSubQuery> engine = factory2.createEngine(factory2.loadDefinition(parameter));
        Object data = engine.query().getData("type", 1);
        assert data.toString() == "{}"; 
    }
    
}
