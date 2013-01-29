package com.rhomobile.rhoconnect.restclient;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.HashMap;

public class RestClient4JTest {
	String token;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// TODO:  Make sure that redis and rhoconnect up and running 
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		// Get token
		String url = "http://localhost:9292/rc/v1/system/login";
		Map<String, String> credentials = new HashMap<String, String>();
		credentials.put("login", "rhoadmin");
		credentials.put("password", "");
		String content = JSONObject.toJSONString(credentials);

		Map<String, String> params = new HashMap<String, String>();
		params.put("content-type", "application/json");
		RestResponse response = RestClient4J.post(url, content, params);
		assertEquals("Response code", 200, response.code());

		assertEquals("Token", "my-rhoconnect-token", response.body());
		this.token = response.body();
	}

	@After
	public void tearDown() throws Exception {
		// TODO:
	}

	@Test
	public void testReset() throws MalformedURLException {
		// Flush DB
		String url = "http://localhost:9292/rc/v1/system/reset";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-RhoConnect-API-TOKEN", token);
		headers.put("content-type", "application/json");
		Map<String, String> data = new HashMap<String, String>();
		String content = JSONObject.toJSONString(data);
		
		RestResponse response = RestClient4J.post(url, content, headers);
		assertEquals("Response code", 200, response.code());
		assertEquals("Body", "DB reset", response.body());
	}
	
	@Test
	public void testGetEmpltyUsersList() throws MalformedURLException {
		// Get empty list of users
		String url = "http://localhost:9292/rc/v1/users";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-RhoConnect-API-TOKEN", token);
		RestResponse response = RestClient4J.get(url, headers);

		assertEquals("Response code", 200, response.code());
		String users = response.body();
		Object o = JSONValue.parse(users);
		JSONArray array = (JSONArray)o;
		assertEquals("List of users should be empty", 0, array.size());		
		// JSONObject.
	}

	@Test
	public void testCreateUser() throws MalformedURLException {
		// Create user 'alexb'
		Map<String, String> credentials = new HashMap<String, String>();
		credentials.put("login", "alexb");
		credentials.put("password", "hello");
		Map<String, Map<String, String>> payload = new HashMap<String, Map<String, String>>();
		payload.put("attributes", credentials);
		String content = JSONObject.toJSONString(payload);		

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("content-type", "application/json");	
		headers.put("X-RhoConnect-API-TOKEN", token);
		
		String url = "http://localhost:9292/rc/v1/users";
		RestResponse response = RestClient4J.post(url, content, headers);
		assertEquals("Response code", 200, response.code());
		assertEquals("Body", "User created", response.body());		
		//fail("Not yet implemented");
	}

	@Test
	public void testGetNotEmpltyUsersList() throws MalformedURLException {
		// Get not empty list of users
		String url = "http://localhost:9292/rc/v1/users";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-RhoConnect-API-TOKEN", token);
		RestResponse response = RestClient4J.get(url, headers);

		assertEquals("Response code", 200, response.code());
		String users = response.body();
		Object o = JSONValue.parse(users);
		JSONArray array = (JSONArray)o;
		assertEquals("List of users should not be empty", 1, array.size());
		//System.out.println(array.get(0));
		assertEquals("User name should be 'alexb'", "alexb", array.get(0));		
	}
	
	@Test
	public void testDeleteUser() throws MalformedURLException {
		String url = "http://localhost:9292/rc/v1/users/alexb";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-RhoConnect-API-TOKEN", token);

		RestResponse response = RestClient4J.delete(url, headers);
		assertEquals("Response code", 200, response.code());
		assertEquals("Body", "User deleted", response.body());		
		// fail("Not yet implemented");
	}

	// @Test
	// public void testPut() {
	// fail("Not yet implemented");
	// }
	
}
