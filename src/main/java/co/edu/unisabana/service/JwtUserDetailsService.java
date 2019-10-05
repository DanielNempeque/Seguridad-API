package co.edu.unisabana.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unisabana.dao.UserDao;
import co.edu.unisabana.model.DAOUser;
import co.edu.unisabana.model.UserDTO;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	/**
	 * Search a user by username in the database, and returns all the data of it
	 * @return UserDetails
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		DAOUser user = userDao.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}
	/**
	 * Saves the user in the database creating an instance of DAOUser class 
	 * @param user
	 * @return DAOUser
	 */
	public DAOUser save(UserDTO user) {
		DAOUser newUser = new DAOUser();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return userDao.save(newUser);
	}
}
