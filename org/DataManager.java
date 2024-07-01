import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class DataManager {

	private final WebClient client;
	private Map<String, String> contributorCache;

	public DataManager(WebClient client) {
		if(client == null){
			throw new IllegalStateException("Client is null");
		}
		this.client = client;
		this.contributorCache = new HashMap<>();
	}

	/**
	 * Attempt to log the user into an Organization account using the login and
	 * password.
	 * This method uses the /findOrgByLoginAndPassword endpoint in the API
	 * 
	 * @return an Organization object if successful; null if unsuccessful
	 */
	public Organization attemptLogin(String login, String password) {
		if(login == null){
			throw new IllegalArgumentException("Login null");
		}
		if(password == null){
			throw new IllegalArgumentException("Password null");
		}
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("login", login);
			map.put("password", password);
			String response = client.makeRequest("/findOrgByLoginAndPassword", map);
			if(response == null){
				throw new IllegalStateException("Response null");
			}


			JSONParser parser = new JSONParser();
			JSONObject json;
			try{
				json = (JSONObject) parser.parse(response);
			}catch(Exception e){
				throw new IllegalStateException("Somethign went wrong parsing JSON");
			}
			String status = (String)json.get("status");

			if(status.equals("error")){
				throw new IllegalStateException("Cannot connect to the server");
			}

			if (status.equals("success")) {
				JSONObject data = (JSONObject) json.get("data");
				String orgId = (String) data.get("_id");
				String orgName = (String) data.get("name");
				String orgDescription = (String) data.get("description");

				Organization org = new Organization(orgId, orgName, orgDescription);
				String fundId;
				String fundName;
				String fundDescription;
				JSONArray funds = (JSONArray) data.get("funds");

				Iterator it = funds.iterator();
				while (it.hasNext()) {
					JSONObject fund = (JSONObject) it.next();
					fundId = (String) fund.get("_id");
					fundName = (String) fund.get("name");
					fundDescription = (String) fund.get("description");
					long target = (Long) fund.get("target");

					Fund newFund = new Fund(fundId, fundName, fundDescription, target);

					JSONArray donations = (JSONArray) fund.get("donations");
					List<Donation> donationList = new LinkedList<>();
					Iterator it2 = donations.iterator();
					while (it2.hasNext()) {
						JSONObject donation = (JSONObject) it2.next();
						String contributorId = (String) donation.get("contributor");
						String contributorName = this.getContributorName(contributorId);
						long amount = (Long) donation.get("amount");
						String date = (String) donation.get("date");
						donationList.add(new Donation(fundId, contributorName, amount, date));
					}

					newFund.setDonations(donationList);

					org.addFund(newFund);

				}

				return org;
			}
			else return null;
		}
		catch(IllegalStateException e){
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Organization newOrganization(String login, String password, String name, String description) {
		if(login == null){
			throw new IllegalArgumentException("Login null");
		}
		if(password == null){
			throw new IllegalArgumentException("Password null");
		}
		if(name == null){
			throw new IllegalArgumentException("Password null");
		}
		if(description == null){
			throw new IllegalArgumentException("Password null");
		}
		try {
			if (attemptLogin(login, password) != null) {
				throw new IllegalStateException();
			}
			Map<String, Object> map = new HashMap<>();
			map.put("login", login);
			map.put("password", password);
			map.put("name", name);
			map.put("description", password);
			String response = client.makeRequest("/createOrg", map);
			if(response == null){
				throw new IllegalStateException("Response null");
			}


			JSONParser parser = new JSONParser();
			JSONObject json;
			try{
				json = (JSONObject) parser.parse(response);
			}catch(Exception e){
				throw new IllegalStateException("Something went wrong parsing JSON");
			}
			String status = (String)json.get("status");

			if(status.equals("error")){
				throw new IllegalStateException("Cannot connect to the server");
			}

			if (status.equals("success")) {
				Organization org = attemptLogin(login, password);
				return org;
			}
			else return null;
		}
		catch(IllegalStateException e){
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Look up the name of the contributor with the specified ID.
	 * This method uses the /findContributorNameById endpoint in the API.
	 * 
	 * @return the name of the contributor on success; null if no contributor is
	 *         found
	 */
	public String getContributorName(String id) {

		if(id == null){
			throw new IllegalArgumentException("id is null");
		}
		if (contributorCache.containsKey(id)) {
			return contributorCache.get(id);
		} else {
      		try {

				Map<String, Object> map = new HashMap<>();
				map.put("id", id);
				String response = client.makeRequest("/findContributorNameById", map);
				if(response == null){
					throw new IllegalStateException("Error connecting to server");
				}
				JSONParser parser = new JSONParser();
				JSONObject json;
				try{
					json = (JSONObject) parser.parse(response);
				}catch (Exception e){
					throw new IllegalStateException("Failed JSON Parse");
				}
				String status = (String)json.get("status");

				if (status.equals("success")) {
					String name = (String)json.get("data");
					return name;
				}
				if(status.equals("error")){
					throw new IllegalStateException("Status is error");
				}
				else return null;


			}
			catch (IllegalStateException e){
				e.printStackTrace();
				throw new IllegalStateException(e.getMessage());
			}
			catch (Exception e) {
				return null;
			}	
		}
	}


	/**
	 * This method creates a new fund in the database using the /createFund endpoint
	 * in the API
	 * 
	 * @return a new Fund object if successful; null if unsuccessful
	 */
	public Fund createFund(String orgId, String name, String description, long target) {
		if(orgId == null) {
			throw new IllegalArgumentException("origId is null");
		}
		if(name == null){
			throw new IllegalArgumentException("name is null");
		}
		if(description == null){
			throw new IllegalArgumentException("description is null");
		}
		try {

			Map<String, Object> map = new HashMap<>();
			map.put("orgId", orgId);
			map.put("name", name);
			map.put("description", description);
			map.put("target", target);
			String response = client.makeRequest("/createFund", map);
			if(response == null){
				throw new IllegalStateException("Cannot connect to client / response is null");
			}

			JSONParser parser = new JSONParser();
			JSONObject json;
			try{
				json = (JSONObject) parser.parse(response);
			}catch(Exception e){
				throw new IllegalStateException("Error parsing JSON");
			}
			String status = (String)json.get("status");
			if(status.equals("error")){
				throw new IllegalStateException("WebClient returned error");
			}
			if (status.equals("success")) {
				JSONObject fund = (JSONObject) json.get("data");
				return new Fund(orgId, name, description, target);
			} else
				return null;
		}
		catch(IllegalStateException e){
			throw new IllegalStateException(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
