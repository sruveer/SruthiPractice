package com.sruthi.demoRestApi;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.sruthi.bean.UsersBean;
import com.sruthi.implementation.UserImpl;
import com.sruthi.util.ErrorType;

@RestController
@RequestMapping("/api")
public class RestAPIController {
	
	public static final Logger logger = LoggerFactory.getLogger(RestAPIController.class);
	
	@Autowired
	UserImpl userService; 
	
	@RequestMapping(value="/user/", method= RequestMethod.GET)
	public ResponseEntity<?> displayAllUsers(){
		List<UsersBean> users= userService.getAllusers();
		if(users.isEmpty())
		{
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<UsersBean>>(users,HttpStatus.OK);
	}
	
	@RequestMapping(value="/user/{id}", method=RequestMethod.GET)
	public ResponseEntity<?> displayUserByID (@PathVariable("id") int id){
		logger.info("Fetching User with id {}", id);
		UsersBean user=userService.getByID(id);
		if(user==null) {
			logger.error("User with id {} not found.", id);
			return new ResponseEntity(new ErrorType("User with id "+id+"not found"),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<UsersBean>(user,HttpStatus.OK);
	}
	
	@RequestMapping(value="/user/", method=RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody UsersBean user, UriComponentsBuilder uriBuilder){
		logger.info("Creating a user");
		
		if(userService.isUsersExists(user)) {
			logger.error("Unable to create user {} as user with this name already exists", user.getName());
			
			return new ResponseEntity(new ErrorType("User with name"+user.getName()+"already exists"),HttpStatus.CONFLICT);
		}
		userService.saveUser(user);
		
		HttpHeaders header= new HttpHeaders();
		header.setLocation(uriBuilder.path("/api/user/{id}").buildAndExpand(user.getId()).toUri());
		return new ResponseEntity<String>(header, HttpStatus.CREATED);
	}
	
	@RequestMapping(value="/user/{id}", method=RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable("id") int id, @RequestBody UsersBean user){
		logger.info("Updating the User with id {}", id);
		
		UsersBean currentUser= userService.getByID(id);
		if(currentUser==null)
		{
			logger.error("User with this id {} does not exist",id);
			return new ResponseEntity(new ErrorType("No user with with this ID "+id),HttpStatus.NOT_FOUND);
		}
		if(userService.noDetailsModified(currentUser)){
			logger.error("User details not modified ",id);
			return new ResponseEntity(new ErrorType("User details not modified "+id),HttpStatus.NOT_FOUND);
		}
		currentUser.setName(user.getName());
		currentUser.setAge(user.getAge());
		currentUser.setSalary(user.getSalary());
		
		userService.update(currentUser);
		
		return new ResponseEntity<UsersBean>(currentUser, HttpStatus.OK);
	}
	
	@RequestMapping(value="/user/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteUserByID(@PathVariable("id") int id)
	{
		logger.info("Deleting the user of id {}",id);
		
		UsersBean user= userService.getByID(id);
		if(user== null)
		{
			logger.error("Unable to find user with id {}",id);
			return new ResponseEntity(new ErrorType("Unable to find user with id"+id),HttpStatus.NOT_FOUND);
		}
		userService.deleteById(id);
		
		return new ResponseEntity<UsersBean>(HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(value="/user", method= RequestMethod.DELETE)
	public ResponseEntity<?> deleteAllUsers()
	{
		logger.info("Deleting all Users");
		
		userService.deleteAllUsers();
		return new ResponseEntity<UsersBean>(HttpStatus.NO_CONTENT);
	}
	
}
