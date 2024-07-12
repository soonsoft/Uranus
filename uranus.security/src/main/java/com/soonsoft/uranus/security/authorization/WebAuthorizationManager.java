package com.soonsoft.uranus.security.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.Nullable;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.security.authorization.voter.IVoter;

public class WebAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final Func1<HttpServletRequest, Collection<ConfigAttribute>> resourceAttributesGetter;
    private List<IVoter> voterList;
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    public WebAuthorizationManager(Func1<HttpServletRequest, Collection<ConfigAttribute>> resourceAttributesGetter) {
        this.resourceAttributesGetter = resourceAttributesGetter;
    }

    @Override
    @Nullable
    public AuthorizationDecision check(Supplier<Authentication> authenticationGetter, RequestAuthorizationContext requestAuthorizationContext) {
        // 身份校验
        Authentication authentication = authenticationGetter.get();
        boolean isAuthenticated = 
            authentication != null && !this.trustResolver.isAnonymous(authentication) && authentication.isAuthenticated();
        if(!isAuthenticated) {
            return new AuthorizationDecision(false);
        }

        HttpServletRequest httpRequest = requestAuthorizationContext.getRequest();
        // 访问资源的权限信息
        Collection<ConfigAttribute> attributes = 
            resourceAttributesGetter != null ? resourceAttributesGetter.call(httpRequest) : null;
        // 权限校验
        boolean granted = attributes == null ? true : decision(authentication, httpRequest, attributes);

        return new AuthorizationDecision(granted);
    }

    protected boolean decision(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        List<IVoter> voters = getVoterList();
        if(voters == null || voters.isEmpty()) {
            return true;
        }

        for (IVoter voter : voters) {
            if(voter.vote(authentication, object, attributes)) {
                return true;
            }
        }

        return false;
    }

    //#region getter and setter

    public List<IVoter> getVoterList() {
        return voterList;
    }

    public void setVoterList(List<IVoter> voterList) {
        this.voterList = voterList;
    }

    public boolean addVoter(IVoter voter) {
        if(voter == null) {
            return false;
        }
        if(voterList == null) {
            voterList = new ArrayList<>();
        }

        if(voterList.contains(voter)) {
            return false;
        }

        return voterList.add(voter);
    }

    //#endregion
    
}
