package com.soonsoft.uranus.security.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.soonsoft.uranus.core.Guard;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.expression.WebExpressionVoter;

/**
 * Spring Security共有三种投票器
 * AffirmativeBased：只要有通过票就通过，如果没有通过，但是有两个以上反对，则拒绝
 * UnanimousBased： 只要有反对票就拒绝，全部弃权则通过
 * ConsensusBased：通过票多就通过，反对票多就拒绝，如果票数相同就看allowIfEqualGrantedDeniedDecisions的配置，默认为true
 * 
 * 我们的投票器其实最接近UnanimousBased的逻辑，但是UnanimousBased把configAttributes中的元素单独判定了
 * 而在角色场景，用户往往可以配置多个角色，往往只要匹配到一个角色就可以放行，所以需要重写。
 */
@Deprecated
public class WebAccessDecisionManager extends UnanimousBased {

    private List<AccessDecisionVoter<? extends Object>> decisionVoters;

    public WebAccessDecisionManager(List<AccessDecisionVoter<? extends Object>> decisionVoters) {
        super(decisionVoters);
        this.decisionVoters = decisionVoters;
    }

    public void addVoter(AccessDecisionVoter<? extends Object> voter) {
        Guard.notNull(voter, "the voter is required.");
        decisionVoters.add(voter);
    }

    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    @Override
    public void decide(Authentication authentication, Object object,
			Collection<ConfigAttribute> attributes) throws AccessDeniedException {

		int grant = 0;

        for (AccessDecisionVoter voter : getDecisionVoters()) {
            int result = voter.vote(authentication, object, attributes);

            if (logger.isDebugEnabled()) {
                logger.debug("Voter: " + voter + ", returned: " + result);
            }

            switch (result) {
                case AccessDecisionVoter.ACCESS_GRANTED:
                    grant++;
                    break;

                case AccessDecisionVoter.ACCESS_DENIED:
                    throw new AccessDeniedException(
                        messages.getMessage(
                            "AbstractAccessDecisionManager.accessDenied", "Access is denied"));
                default:
                    break;
            }
        }

		// To get this far, there were no deny votes
		if (grant > 0) {
			return;
		}

		// To get this far, every AccessDecisionVoter abstained
		checkAllowIfAllAbstainDecisions();
	}

    public static WebAccessDecisionManager create() {
        List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
        // 默认的身份验证投票器，根据config的表达式进行验证
        decisionVoters.add(new WebExpressionVoter());
        // 默认角色投票器，角色以ROLE_开头
        decisionVoters.add(new RoleVoter());
        return new WebAccessDecisionManager(decisionVoters);
    }
    
}