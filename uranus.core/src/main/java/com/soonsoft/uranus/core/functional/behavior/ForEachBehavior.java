package com.soonsoft.uranus.core.functional.behavior;

public class ForEachBehavior {

    private BehaviorValue value = BehaviorValue.None;

    public void reset() {
        value = BehaviorValue.None;
    }

    public void setContinue() {
        value = BehaviorValue.Continue;
    }

    public void setBreak() {
        value = BehaviorValue.Break;
    }

    public boolean isContinue() {
        return value == BehaviorValue.Continue;
    }

    public boolean isBreak() {
        return value == BehaviorValue.Break;
    }

    public static enum BehaviorValue {
        None,
        Continue,
        Break,
        ;
    }
    
}
