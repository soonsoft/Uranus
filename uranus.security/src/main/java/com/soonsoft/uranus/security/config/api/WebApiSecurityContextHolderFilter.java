package com.soonsoft.uranus.security.config.api;

import java.io.IOException;
import java.util.function.Supplier;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;

import com.auth0.jwt.exceptions.JWTDecodeException;

import com.soonsoft.uranus.security.config.api.jwt.JWTTokenProvider;
import com.soonsoft.uranus.security.config.api.jwt.token.JWTAuthenticationToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebApiSecurityContextHolderFilter extends SecurityContextHolderFilter {

	private SecurityContextRepository securityContextRepository;

    public WebApiSecurityContextHolderFilter(SecurityContextRepository repo) {
        super(repo);
		this.securityContextRepository = repo;
    }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request; 
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if(securityContextRepository instanceof WebApiHttpSessionSecurityContextRepository repository) {
			SecurityContext securityContext = repository.loadContext(httpRequest, httpResponse);
			try {
				SecurityContextHolder.setContext(securityContext);
				filterChain.doFilter(request, response);
			}
			finally {
				SecurityContextHolder.clearContext();
			}
		} else {
			super.doFilter(request, response, filterChain);
		}
	}

	public static class WebApiHttpSessionSecurityContextRepository extends HttpSessionSecurityContextRepository {

		private static final Logger LOGGER = LoggerFactory.getLogger(WebApiHttpSessionSecurityContextRepository.class);
	
		private ITokenProvider<?> tokenProvider;
	
		public WebApiHttpSessionSecurityContextRepository(ITokenProvider<?> tokenProvider) {
			this.tokenProvider = tokenProvider;
		}
	
		public SecurityContext loadContext(HttpServletRequest request, HttpServletResponse response) {
			if(ITokenProvider.SESSION_ID_TYPE.equals(tokenProvider.getTokenType())) {
				// API适配SessionId
				((ISessionIdStrategy) tokenProvider).updateSessionId(request, response);
			}
	
			Supplier<SecurityContext> contextSupplier = loadDeferredContext(request);
			return loadSecurityContext(request, contextSupplier);
		}
	
		private SecurityContext loadSecurityContext(HttpServletRequest request, Supplier<SecurityContext> contextSupplier) {
			SecurityContext securityContext = contextSupplier.get();
	
			// 如果Token无效，则清空登录信息
			if(!tokenProvider.checkToken(request)) {
				securityContext.setAuthentication(null);
				return securityContext;
			}
	
			if(ITokenProvider.JWT_TYPE.equals(tokenProvider.getTokenType())) {
				// API适配JWT-Token
				setJWTAuthentication(securityContext, request);
			}
	
			return securityContext;
		}
	
		protected void setJWTAuthentication(SecurityContext securityContext, HttpServletRequest request) {
			
			String accessToken = ((JWTTokenProvider) tokenProvider).getAccessToken(request);
			if(accessToken == null) {
				return;
			}
			
			try {
				JWTAuthenticationToken authenticationToken = JWTAuthenticationToken.parse(accessToken);
				securityContext.setAuthentication(authenticationToken);
			} catch(JWTDecodeException e) {
				LOGGER.warn("parse jwtToken: " + accessToken + " failed. ", e);
				securityContext.setAuthentication(null);
			}
		}
		
	}
    
}
