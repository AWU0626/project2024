package edu.upenn.cis573.project;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONArray;

public class DataManager {

    private WebClient client;
    private HashMap<String, String> cacheFundName = new HashMap<>();

    public DataManager(WebClient client) {
        if(client == null){
            throw new IllegalStateException("WebClien is null in constructor");
        }
        this.client = client;
    }


    /**
     * Attempt to log in to the Contributor account using the specified login and password.
     * This method uses the /findContributorByLoginAndPassword endpoint in the API
     * @return the Contributor object if successfully logged in, null otherwise
     */
    public Contributor attemptLogin(String login, String password) {
        if(login == null){
            throw new IllegalArgumentException("attemptLogin: login is null");
        }
        if(password == null){
            throw new IllegalArgumentException("attemptLogin: password is null");
        }
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("login", login);
            map.put("password", password);
            String response = client.makeRequest("/findContributorByLoginAndPassword", map);
            if(response == null){
                throw new IllegalStateException("attemptLogin: Cannot connect to server / response is null");
            }
            JSONObject json;
            try{
                json = new JSONObject(response);
            }catch (Exception e){
                throw new IllegalStateException("attemptLogin: Malformed JSON");
            }
            String status = (String)json.get("status");

            if(status.equals("error")){
                throw new IllegalStateException("attemptLogin: Server returned error");
            }
            if(status.equals("Login failed")){
                return null;
            }
            else if (status.equals("success")) {
                JSONObject data = (JSONObject)json.get("data");
                String id = (String)data.get("_id"),
                        name = (String)data.get("name"),
                        email = (String)data.get("email"),
                        creditCardNumber = (String)data.get("creditCardNumber"),
                        creditCardCVV = (String)data.get("creditCardCVV"),
                        creditCardExpiryMonth = (
                                (Integer)data.get("creditCardExpiryMonth")).toString(),
                        creditCardExpiryYear = (
                                (Integer)data.get("creditCardExpiryYear")).toString(),
                        creditCardPostCode = (String)data.get("creditCardPostCode");
                Contributor contributor = new Contributor(id, name, email, creditCardNumber, creditCardCVV, creditCardExpiryMonth, creditCardExpiryYear, creditCardPostCode);
                List<Donation> donationList = new LinkedList<>();
                JSONArray donations = (JSONArray)data.get("donations");
                for (int i = 0; i < donations.length(); i++) {
                    JSONObject jsonDonation = donations.getJSONObject(i);
                    String fId = (String)jsonDonation.get("fund"),
                            fund = cacheFundName.getOrDefault(fId, null),
                            date = (String)jsonDonation.get("date");
                    if (fund == null) {
                        fund = getFundName(fId);
                        cacheFundName.put(fId, fund);
                    }
                    long amount = (Integer)jsonDonation.get("amount");
                    Donation donation = new Donation(fund, name, amount, date);
                    donationList.add(donation);
                }
                contributor.setDonations(donationList);
                return contributor;
            }
            return null;
        }
        catch (IllegalStateException e){
            throw new IllegalStateException(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the name of the fund with the specified ID using the /findFundNameById endpoint
     * @return the name of the fund if found, "Unknown fund" if not found, null if an error occurs
     */
    public String getFundName(String id) {
        if(id == null){
            throw new IllegalArgumentException("getFundName: id is null");
        }
        if (cacheFundName.containsKey(id)) {
            return cacheFundName.get(id);
        }
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            String response = client.makeRequest("/findFundNameById", map);
            if(response == null){
                throw new IllegalStateException("getFundName: Error in communicating with server / response is null");
            }
            JSONObject json;
            try{
                json = new JSONObject(response);
            }catch (Exception e){
                throw new IllegalStateException("getFundName: Malformed JSON");
            }
            String status = (String)json.get("status");
            if(status.equals("error")){
                throw new IllegalStateException("getFundName: Server returned error");
            }
            if (status.equals("success")) {
                String name = (String)json.get("data").toString();
                cacheFundName.put(id, name);
                return name;
            }
            else {
                return "Unknown Fund";
            }
        }
        catch (IllegalStateException e){
            throw new IllegalStateException(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get information about all of the organizations and their funds.
     * This method uses the /allOrgs endpoint in the API
     * @return a List of Organization objects if successful, null otherwise
     */
    public List<Organization> getAllOrganizations() {
        try {
            Map<String, Object> map = new HashMap<>();
            String response = client.makeRequest("/allOrgs", map);
            if(response == null){
                throw new IllegalStateException("getAllOrganizations: Error in communicating with server / response is null");
            }
            JSONObject json;
            try{
                json = new JSONObject(response);
            }catch (Exception e){
                throw new IllegalStateException("getAllOrganizations: Malformed JSON");
            }
            String status = (String)json.get("status");
            if(status.equals("error")){
                throw new IllegalStateException("getAllOrganizations: Server returned error");
            }
            if (status.equals("success")) {

                List<Organization> organizations = new LinkedList<>();

                JSONArray data = (JSONArray)json.get("data");

                for (int i = 0; i < data.length(); i++) {

                    JSONObject obj = data.getJSONObject(i);

                    String id = (String)obj.get("_id");
                    String name = (String)obj.get("name");

                    Organization org = new Organization(id, name);

                    List<Fund> fundList = new LinkedList<>();

                    JSONArray array = (JSONArray)obj.get("funds");

                    for (int j = 0; j < array.length(); j++) {

                        JSONObject fundObj = array.getJSONObject(j);

                        id = (String)fundObj.get("_id");
                        name = (String)fundObj.get("name");
                        long target = (Integer)fundObj.get("target");
                        long totalDonations = (Integer)fundObj.get("totalDonations");

                        Fund fund = new Fund(id, name, target, totalDonations);

                        fundList.add(fund);

                    }

                    org.setFunds(fundList);

                    organizations.add(org);

                }

                return organizations;

            }

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

    /**
     * Make a donation to the specified fund for the specified amount.
     * This method uses the /makeDonation endpoint in the API
     * @return true if successful, false otherwise
     */
    public boolean makeDonation(String contributorId, String fundId, String amount) {
        if(contributorId == null){
            throw new IllegalArgumentException("makeDonation: contributorId is null");
        }
        if(fundId == null){
            throw new IllegalArgumentException("makeDonation: fundId is null");
        }
        if(amount == null){
            throw new IllegalArgumentException("makeDonation: amount is null");
        }
        for(char c: amount.toCharArray()){
            if(!Character.isDigit(c) && c != '.'){
                throw new IllegalArgumentException("makeDonation: amount is non numeric");
            }
        }
        try {

            Map<String, Object> map = new HashMap<>();
            map.put("contributor", contributorId);
            map.put("fund", fundId);
            map.put("amount", amount);
            String response = client.makeRequest("/makeDonation", map);

            if(response == null){
                throw new IllegalStateException("makeDonation: Error in communicating with server / response is null");
            }
            JSONObject json;
            try{
                json = new JSONObject(response);
            }catch (Exception e){
                throw new IllegalStateException("makeDonation: Malformed JSON");
            }
            String status = (String)json.get("status");
            if(status.equals("error")){
                throw new IllegalStateException("makeDonation: Server returned error");
            }
            return status.equals("success");

        }
        catch (IllegalStateException e){
            throw new IllegalStateException(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
