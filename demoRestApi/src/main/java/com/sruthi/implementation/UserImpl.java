package com.sruthi.implementation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.sruthi.bean.UsersBean;

@Service("userService")
public class UserImpl {
	private static final AtomicInteger counter=new AtomicInteger();
	private static List<UsersBean> users;
	static {
		users=populateUsers();
	}
	public List<UsersBean> getAllusers(){
		return users;
	}
	
	public UsersBean getByID(int id){
		for(UsersBean user: users) {
			if(user.getId() == id)
			{
				return user;
			}
		}
		return null;
	}
	
	public UsersBean getByName(String name){
		for(UsersBean user: users) {
			if(user.getName().equalsIgnoreCase(name))
			{
				return user;
			}
		}
		return null;
	}
	
	public boolean isUsersExists(UsersBean user) {
		return getByName(user.getName())!=null;
	}
	
	public void saveUser(UsersBean user) {
		user.setId(counter.incrementAndGet());
		users.add(user);
	}
	
	public void update(UsersBean user) {
		int index=users.indexOf(user);
		users.set(index, user);
	}
	
	public void deleteById(int id) {
		for(Iterator<UsersBean> iterator=users.iterator(); iterator.hasNext(); ) {
			UsersBean user= iterator.next();
			if(user.getId()==id)
			{
				iterator.remove();
			}
		}
	}
	
	public void deleteAllUsers() {
		users.clear();
	}
	
	private static List<UsersBean> populateUsers(){
		List<UsersBean> users= new ArrayList<UsersBean>();
		users.add(new UsersBean(counter.incrementAndGet(),"User1",26,150000));
		users.add(new UsersBean(counter.incrementAndGet(),"User2",27.0,180000));
		users.add(new UsersBean(counter.incrementAndGet(),"User3",25.2,100000));
		return users;
	}
}
