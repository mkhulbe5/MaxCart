package com.myStartup.services.userService;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.myStartup.dto.UserDto;
import com.myStartup.exceptions.AlreadyExistsException;
import com.myStartup.exceptions.ResourceNotFoundException;
import com.myStartup.model.User;
import com.myStartup.repository.UserRepository;
import com.myStartup.requests.CreateUserRequest;
import com.myStartup.requests.UserUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	@Override
	public User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
	}

	@Override
	public User createUser(CreateUserRequest request) {
		return Optional.of(request).filter(user -> !userRepository.existsByEmail(request.getEmail())).map(req -> {
			User user = new User();
			user.setEmail(request.getEmail());
			user.setPassword(request.getPassword());
			user.setFirstName(request.getFirstName());
			user.setLastName(request.getLastName());
			return userRepository.save(user);
		}).orElseThrow(() -> new AlreadyExistsException("Oops!" + request.getEmail() + " already exists!"));
	}

	@Override
	public User updateUser(UserUpdateRequest request, Long userId) {
		return userRepository.findById(userId).map(existingUser -> {
			existingUser.setFirstName(request.getFirstName());
			existingUser.setLastName(request.getLastName());
			return userRepository.save(existingUser);
		}).orElseThrow(() -> new ResourceNotFoundException("User not found!"));

	}

	@Override
	public void deleteUser(Long userId) {
		userRepository.findById(userId).ifPresentOrElse(userRepository::delete, () -> {
			throw new ResourceNotFoundException("User not found!");
		});
	}

	@Override
	public UserDto convertUserToDto(User user) {
		return modelMapper.map(user, UserDto.class);
	}
}