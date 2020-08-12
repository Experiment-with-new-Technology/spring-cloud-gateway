package com.extremecoder.apigateway.service;

import com.extremecoder.apigateway.model.User;
import com.extremecoder.apigateway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

//	private Map<String, User> data;
	
	/*@PostConstruct
	public void init(){
		data = new HashMap<>();
		
		//username:passwowrd -> user:user
		data.put("user", new User("user", "cBrlgyL2GI2GINuLUUwgojITuIufFycpLG4490dhGtY=", true, Arrays.asList(Role.ROLE_USER)));

		//username:passwowrd -> admin:admin
		data.put("admin", new User("admin", "dQNjUIMorJb8Ubj2+wVGYp6eAeYkdekqAcnYp+aRq5w=", true, Arrays.asList(Role.ROLE_ADMIN)));
	}*/

	public Mono<User> findByUsername(String username) {
		Mono<User> optionalUser = userRepository.findByUsername(username);
		return optionalUser.switchIfEmpty(Mono.empty());
	}

	public Mono<User> adduser(User user) {
		return userRepository.save(user);
	}

}
