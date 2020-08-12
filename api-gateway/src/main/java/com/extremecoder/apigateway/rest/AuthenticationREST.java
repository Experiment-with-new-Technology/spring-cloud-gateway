package com.extremecoder.apigateway.rest;

import com.extremecoder.apigateway.model.AuthRequest;
import com.extremecoder.apigateway.model.AuthResponse;
import com.extremecoder.apigateway.model.User;
import com.extremecoder.apigateway.service.UserService;
import com.extremecoder.apigateway.util.JWTUtil;
import com.extremecoder.apigateway.util.PBKDF2Encoder;
import com.extremecoder.apigateway.util.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
public class AuthenticationREST {

	@Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private PBKDF2Encoder passwordEncoder;

	@Autowired
	private UserService userService;

	@Autowired
	private UtilService utilService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest ar) {
		return userService.findByUsername(ar.getUsername()).map((userDetails) -> {
			if (passwordEncoder.encode(ar.getPassword()).equals(userDetails.getPassword())) {
				return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(userDetails)));
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		}).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public Mono<ResponseEntity<?>> createPerson(@RequestBody User user) {
		user.setEnabled(true);
		user.setRoles(List.of("ROLE_USER"));
		String message = utilService.validation(user);
		if (message.isEmpty()) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			return Mono.just(ResponseEntity.ok(userService.adduser(user)));
		} else {
			return Mono.just(ResponseEntity.badRequest().body(message));
		}
	}

}
