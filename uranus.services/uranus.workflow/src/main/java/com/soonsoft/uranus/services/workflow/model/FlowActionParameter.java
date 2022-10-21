package com.soonsoft.uranus.services.workflow.model;

import java.util.Date;

public class FlowActionParameter {
    
     /** 操作人 */
     private String operator;
     /** 操作人姓名 */
     private String operatorName;
     /** 操作时间 */
     private Date operateTime;
 
     public String getOperator() {
         return operator;
     }
     public void setOperator(String operator) {
         this.operator = operator;
     }
 
     public String getOperatorName() {
         return operatorName;
     }
     public void setOperatorName(String operatorName) {
         this.operatorName = operatorName;
     }
 
     public Date getOperateTime() {
         return operateTime;
     }
     public void setOperateTime(Date operateTime) {
         this.operateTime = operateTime;
     }

}
