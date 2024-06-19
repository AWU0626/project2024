package edu.upenn.cis573.project;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.fail;

public class AndroidDataManagerRobustnessTest {

    private DataManager dm;

    /*
    Tests for attemptLogin
     */

    @Test(expected=IllegalStateException.class)
    public void testAttemptLogin_WebClientIsNull() {

        dm = new DataManager(null);
        dm.attemptLogin("login", "password");
        fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient is null");

    }

    @Test(expected=IllegalArgumentException.class)
    public void testAttemptLogin_LoginIsNull() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001));
        dm.attemptLogin(null, "password");
        fail("DataManager.attemptLogin does not throw IllegalArgumentxception when login is null");

    }

    @Test(expected=IllegalArgumentException.class)
    public void testAttemptLogin_PasswordIsNull() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001));
        dm.attemptLogin("login", null);
        fail("DataManager.attemptLogin does not throw IllegalArgumentxception when password is null");

    }

    @Test(expected=IllegalStateException.class)
    public void testAttemptLogin_WebClientCannotConnectToServer() {

        // this assumes no server is running on port 3002
        dm = new DataManager(new WebClient("10.0.2.2", 3002));
        dm.attemptLogin("login", "password");
        fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient cannot connect to server");

    }

    @Test(expected=IllegalStateException.class)
    public void testAttemptLogin_WebClientReturnsNull() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.attemptLogin("login", "password");
        fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient returns null");

    }

    @Test(expected=IllegalStateException.class)
    public void testAttemptLogin_WebClientReturnsError() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}";
            }
        });
        dm.attemptLogin("login", "password");
        fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient returns error");

    }

    @Test(expected=IllegalStateException.class)
    public void testAttemptLogin_WebClientReturnsMalformedJSON() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "I AM NOT JSON!";
            }
        });
        dm.attemptLogin("login", "password");
        fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient returns malformed JSON");

    }


    /*
     * Tests for getFundName
     */


    @Test(expected=IllegalStateException.class)
    public void testGetFundName_WebClientIsNull() {

        dm = new DataManager(null);
        dm.getFundName("id");
        fail("DataManager.getFundName does not throw IllegalStateException when WebClient is null");

    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetFundName_IdIsNull() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001));
        dm.getFundName(null);
        fail("DataManager.getFundName does not throw IllegalArgumentxception when id is null");

    }

    @Test(expected=IllegalStateException.class)
    public void testGetFundName_WebClientCannotConnectToServer() {

        // this assumes no server is running on port 3002
        dm = new DataManager(new WebClient("10.0.2.2", 3002));
        dm.getFundName("id");
        fail("DataManager.getFundName does not throw IllegalStateException when WebClient cannot connect to server");

    }

    @Test(expected=IllegalStateException.class)
    public void testGetFundName_WebClientReturnsNull() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.getFundName("id");
        fail("DataManager.getFundName does not throw IllegalStateException when WebClient returns null");

    }


    @Test(expected=IllegalStateException.class)
    public void testGetFundName_WebClientReturnsError() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}";
            }
        });
        dm.getFundName("id");
        fail("DataManager.getFundName does not throw IllegalStateException when WebClient returns error");

    }

    @Test(expected=IllegalStateException.class)
    public void testGetFundName_WebClientReturnsMalformedJSON() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "I AM NOT JSON!";
            }
        });
        dm.getFundName("id");
        fail("DataManager.getFundName does not throw IllegalStateException when WebClient returns malformed JSON");

    }



    /*
     * Tests for getAllOrganizations
     */


    @Test(expected=IllegalStateException.class)
    public void testGetAllOrganizations_WebClientIsNull() {

        dm = new DataManager(null);
        dm.getAllOrganizations();
        fail("DataManager.getAllOrganizations does not throw IllegalStateException when WebClient is null");

    }


    @Test(expected=IllegalStateException.class)
    public void testGetAllOrganizations_WebClientCannotConnectToServer() {

        // this assumes no server is running on port 3002
        dm = new DataManager(new WebClient("10.0.2.2", 3002));
        dm.getAllOrganizations();
        fail("DataManager.getAllOrganizations does not throw IllegalStateException when WebClient cannot connect to server");

    }

    @Test(expected=IllegalStateException.class)
    public void testGetAllOrganizations_WebClientReturnsNull() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.getAllOrganizations();
        fail("DataManager.getAllOrganizations does not throw IllegalStateException when WebClient returns null");

    }


    @Test(expected=IllegalStateException.class)
    public void testGetAllOrganizations_WebClientReturnsError() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}";
            }
        });
        dm.getAllOrganizations();
        fail("DataManager.getAllOrganizations does not throw IllegalStateException when WebClient returns error");

    }

    @Test(expected=IllegalStateException.class)
    public void testGetAllOrganizations_WebClientReturnsMalformedJSON() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "I AM NOT JSON!";
            }
        });
        dm.getAllOrganizations();
        fail("DataManager.getAllOrganizations does not throw IllegalStateException when WebClient returns malformed JSON");

    }



    /*
     * Tests for getMakeDonation
     */


    @Test(expected=IllegalStateException.class)
    public void testMakeDonation_WebClientIsNull() {

        dm = new DataManager(null);
        dm.makeDonation("contributorId", "fundId", "100");
        fail("DataManager.getMakeDonation does not throw IllegalStateException when WebClient is null");

    }

    @Test(expected=IllegalArgumentException.class)
    public void testMakeDonation_ContributorIdIsNull() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001));
        dm.makeDonation(null, "fundId", "100");
        fail("DataManager.getMakeDonation does not throw IllegalArgumentxception when contributorId is null");

    }

    @Test(expected=IllegalArgumentException.class)
    public void testMakeDonation_FundIdIsNull() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001));
        dm.makeDonation("contributorId", null, "100");
        fail("DataManager.getMakeDonation does not throw IllegalArgumentxception when fundId is null");

    }

    @Test(expected=IllegalArgumentException.class)
    public void testMakeDonation_AmountIsNull() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001));
        dm.makeDonation("contributorId", "fundId", null);
        fail("DataManager.getMakeDonation does not throw IllegalArgumentxception when amount is null");

    }

    @Test(expected=IllegalArgumentException.class)
    public void testMakeDonation_AmountIsNonNumeric() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001));
        dm.makeDonation("contributorId", "fundId", "amount");
        fail("DataManager.getMakeDonation does not throw IllegalArgumentxception when amount is non-numeric");

    }

    @Test(expected=IllegalStateException.class)
    public void testMakeDonation_WebClientCannotConnectToServer() {

        // this assumes no server is running on port 3002
        dm = new DataManager(new WebClient("10.0.2.2", 3002));
        dm.makeDonation("contributorId", "fundId", "100");
        fail("DataManager.getMakeDonation does not throw IllegalStateException when WebClient cannot connect to server");

    }

    @Test(expected=IllegalStateException.class)
    public void testMakeDonation_WebClientReturnsNull() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.makeDonation("contributorId", "fundId", "100");
        fail("DataManager.getMakeDonation does not throw IllegalStateException when WebClient returns null");

    }


    @Test(expected=IllegalStateException.class)
    public void testMakeDonation_WebClientReturnsError() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}";
            }
        });
        dm.makeDonation("contributorId", "fundId", "100");
        fail("DataManager.getMakeDonation does not throw IllegalStateException when WebClient returns error");

    }

    @Test(expected=IllegalStateException.class)
    public void testMakeDonation_WebClientReturnsMalformedJSON() {

        dm = new DataManager(new WebClient("10.0.2.2", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "I AM NOT JSON!";
            }
        });
        dm.makeDonation("contributorId", "fundId", "100");
        fail("DataManager.getMakeDonation does not throw IllegalStateException when WebClient returns malformed JSON");

    }
}
