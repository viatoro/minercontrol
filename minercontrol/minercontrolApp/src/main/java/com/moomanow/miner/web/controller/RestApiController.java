package com.moomanow.miner.web.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.moomanow.miner.api.pool.IPoolApi;
import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.bean.RevenueBean;
import com.moomanow.miner.dao.MinerControlDao;
import com.moomanow.miner.web.util.CustomErrorType;

//import com.websystique.springboot.model.User;
//import com.websystique.springboot.service.UserService;
//import com.websystique.springboot.util.CustomErrorType;

@RestController
@RequestMapping("/api")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);
	
	@Autowired
	private MinerControlDao minerControlDao;
	
	@RequestMapping(value = "/pool/", method = RequestMethod.GET)
	public ResponseEntity<Map<String, IPoolApi>> listAllPools() {
		Map<String, IPoolApi> pools = minerControlDao.getAllPools();
		if (pools.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			// You many decide to return HttpStatus.NOT_FOUND
		}
		return new ResponseEntity<Map<String, IPoolApi>>(pools, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/pool/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getPool(@PathVariable("id") String id) {
		logger.info("Fetching User with id {}", id);
		Map<String, IPoolApi> pools = minerControlDao.getAllPools();
		IPoolApi pool = pools.get(id);
		if (pool == null) {
			logger.error("User with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("User with id " + id 
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<IPoolApi>(pool, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/miner/", method = RequestMethod.GET)
	public ResponseEntity<Map<String, IAppMiner>> listAllMiners() {
		Map<String, IAppMiner> miners = minerControlDao.getAllMiners();
		if (miners.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			// You many decide to return HttpStatus.NOT_FOUND
		}
		return new ResponseEntity<Map<String, IAppMiner>>(miners, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/miner/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMiner(@PathVariable("id") String id) {
		logger.info("Fetching User with id {}", id);
		Map<String, IAppMiner> miners = minerControlDao.getAllMiners();
		IAppMiner miner = miners.get(id);
		if (miner == null) {
			logger.error("User with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("User with id " + id 
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<IAppMiner>(miner, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/runing/", method = RequestMethod.GET)
	public ResponseEntity<Map<RevenueBean, IAppMiner>> listRunings() {
		Map<RevenueBean, IAppMiner> miners = minerControlDao.getRuning();
		if (miners.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			// You many decide to return HttpStatus.NOT_FOUND
		}
		return new ResponseEntity<Map<RevenueBean, IAppMiner>>(miners, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/runingRevenue/", method = RequestMethod.GET)
	public ResponseEntity<Set<RevenueBean>> listRuningRevenues() {
		Set<RevenueBean> miners = minerControlDao.getRuning().keySet();
		if (miners.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			// You many decide to return HttpStatus.NOT_FOUND
		}
		return new ResponseEntity<Set<RevenueBean>>(miners, HttpStatus.OK);
	}
	
//	@RequestMapping(value = "/runing/{id}", method = RequestMethod.GET)
//	public ResponseEntity<?> getRuning(@PathVariable("id") String id) {
//		logger.info("Fetching User with id {}", id);
//		Map<String, IAppMiner> miners = minerControlDao.getAllMiners();
//		IAppMiner miner = miners.get(id);
//		if (miner == null) {
//			logger.error("User with id {} not found.", id);
//			return new ResponseEntity(new CustomErrorType("User with id " + id 
//					+ " not found"), HttpStatus.NOT_FOUND);
//		}
//		return new ResponseEntity<IAppMiner>(miner, HttpStatus.OK);
//	}
//	processMining

//	@Autowired
//	UserService userService; //Service which will do all data retrieval/manipulation work
//
//	// -------------------Retrieve All Users---------------------------------------------
//
//	@RequestMapping(value = "/user/", method = RequestMethod.GET)
//	public ResponseEntity<List<User>> listAllUsers() {
//		List<User> users = userService.findAllUsers();
//		if (users.isEmpty()) {
//			return new ResponseEntity(HttpStatus.NO_CONTENT);
//			// You many decide to return HttpStatus.NOT_FOUND
//		}
//		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
//	}
//
//	// -------------------Retrieve Single User------------------------------------------
//
//	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
//	public ResponseEntity<?> getUser(@PathVariable("id") long id) {
//		logger.info("Fetching User with id {}", id);
//		User user = userService.findById(id);
//		if (user == null) {
//			logger.error("User with id {} not found.", id);
//			return new ResponseEntity(new CustomErrorType("User with id " + id 
//					+ " not found"), HttpStatus.NOT_FOUND);
//		}
//		return new ResponseEntity<User>(user, HttpStatus.OK);
//	}
//
//	// -------------------Create a User-------------------------------------------
//
//	@RequestMapping(value = "/user/", method = RequestMethod.POST)
//	public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
//		logger.info("Creating User : {}", user);
//
//		if (userService.isUserExist(user)) {
//			logger.error("Unable to create. A User with name {} already exist", user.getName());
//			return new ResponseEntity(new CustomErrorType("Unable to create. A User with name " + 
//			user.getName() + " already exist."),HttpStatus.CONFLICT);
//		}
//		userService.saveUser(user);
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(user.getId()).toUri());
//		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
//	}
//
//	// ------------------- Update a User ------------------------------------------------
//
//	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
//	public ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody User user) {
//		logger.info("Updating User with id {}", id);
//
//		User currentUser = userService.findById(id);
//
//		if (currentUser == null) {
//			logger.error("Unable to update. User with id {} not found.", id);
//			return new ResponseEntity(new CustomErrorType("Unable to upate. User with id " + id + " not found."),
//					HttpStatus.NOT_FOUND);
//		}
//
//		currentUser.setName(user.getName());
//		currentUser.setAge(user.getAge());
//		currentUser.setSalary(user.getSalary());
//
//		userService.updateUser(currentUser);
//		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
//	}
//
//	// ------------------- Delete a User-----------------------------------------
//
//	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
//	public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
//		logger.info("Fetching & Deleting User with id {}", id);
//
//		User user = userService.findById(id);
//		if (user == null) {
//			logger.error("Unable to delete. User with id {} not found.", id);
//			return new ResponseEntity(new CustomErrorType("Unable to delete. User with id " + id + " not found."),
//					HttpStatus.NOT_FOUND);
//		}
//		userService.deleteUserById(id);
//		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
//	}
//
//	// ------------------- Delete All Users-----------------------------
//
//	@RequestMapping(value = "/user/", method = RequestMethod.DELETE)
//	public ResponseEntity<User> deleteAllUsers() {
//		logger.info("Deleting All Users");
//
//		userService.deleteAllUsers();
//		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
//	}

}