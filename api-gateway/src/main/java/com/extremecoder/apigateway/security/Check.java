package com.extremecoder.apigateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class Check implements ReactiveAuthorizationManager<AuthorizationContext> {

	@Override
	public Mono<AuthorizationDecision> check(Mono<Authentication> authentication,
	                                         AuthorizationContext context) {

		/*ServerWebExchange exchange = context.getExchange();
		String originalUri = exchange.getRequest().getPath().pathWithinApplication().value();

		log.debug("check........................." + originalUri);
		// do logic and return result ...
        // hard code for demo

		return Mono.just(new AuthorizationDecision(true));*/

		String originalUri = context.getExchange().getRequest().getPath().pathWithinApplication().value();
		log.info("check........................." + originalUri);
		return authentication
				.map(auth -> {
					if (auth.getAuthorities()
							.stream().anyMatch(
									x -> x.getAuthority().equals("ROLE_USER"))
					) {
						return true;
					}

					return auth.getName().equals(context.getVariables().get("username"));
				})
				.map(AuthorizationDecision::new);
	}
}