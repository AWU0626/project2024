import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

public class DataManager_attemptLogin_Test {

	@Test
	public void testBadStatus() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"bad\",\"data\":{\"_id\":\"abcde\", \"name\":\"new org\",\"description\":\"this is the new org\", \"funds\": []}}";

			}

		});

		Organization org = dm.attemptLogin("hello", "world");

		assertNull(org);

	}

	@Test(expected = IllegalStateException.class)
	public void testHandleException() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}

		});

		dm.attemptLogin("hello", "world");
	}

	@Test
	public void testEmptyFundsEmptyDonations() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"abcde\", \"name\":\"new org\",\"description\":\"this is the new org\", \"funds\": []}}";

			}

		});

		Organization org = dm.attemptLogin("hello", "world");

		assertNotNull(org);
		assertEquals("abcde", org.getId());
		assertEquals("new org", org.getName());
		assertEquals("this is the new org", org.getDescription());
		assertNotNull(org.getFunds());
		assertEquals(0, org.getFunds().size());

	}

	@Test
	public void testNonEmptyFundsEmptyDonations() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"abcde\", \"name\":\"new org\",\"description\":\"this is the new org\", \"funds\": [ {\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,\"org\":\"5678\",\"donations\":[],\"__v\":0}]}}";

			}

		});

		Organization org = dm.attemptLogin("hello", "world");

		assertNotNull(org);
		assertEquals("abcde", org.getId());
		assertEquals("new org", org.getName());
		assertEquals("this is the new org", org.getDescription());
		assertNotNull(org.getFunds());
		assertEquals(1, org.getFunds().size());
		Fund f = org.getFunds().get(0);
		assertNotNull(f);
		assertEquals("this is the new fund", f.getDescription());
		assertEquals("12345", f.getId());
		assertEquals("new fund", f.getName());
		assertEquals(10000, f.getTarget());

		assertNotNull(f.getDonations());
		assertEquals(0, f.getDonations().size());
	}

	@Test
	public void testNonEmptyFundsNonEmptyDonations() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"abcde\", \"name\":\"new org\",\"description\":\"this is the new org\", \"funds\": [ {\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,\"org\":\"5678\",\"donations\":[{\"contributor\": \"hello\", \"amount\" : 1000, \"date\" : \"June 11 2024\"}],\"__v\":0}]}}";

			}

		}) {
			@Override
			public String getContributorName(String contributorId) {
				return "hello";

			}
		};

		Organization org = dm.attemptLogin("hello", "world");

		assertNotNull(org);
		assertEquals("abcde", org.getId());
		assertEquals("new org", org.getName());
		assertEquals("this is the new org", org.getDescription());

		assertNotNull(org.getFunds());
		assertEquals(1, org.getFunds().size());

		Fund f = org.getFunds().get(0);
		assertNotNull(f);
		assertEquals("this is the new fund", f.getDescription());
		assertEquals("12345", f.getId());
		assertEquals("new fund", f.getName());
		assertEquals(10000, f.getTarget());

		assertNotNull(f.getDonations());
		assertEquals(1, f.getDonations().size());

		Donation d = f.getDonations().get(0);
		assertNotNull(d);

		assertEquals("12345", d.getFundId());
		assertEquals("hello", d.getContributorName());
		assertEquals(1000, d.getAmount());
		assertEquals("June 11 2024", d.getDate());

	}
}
