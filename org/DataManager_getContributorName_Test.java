import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_getContributorName_Test {
	@Test
	public void testBadStatus() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"bad\"}";

			}

		});

		String name = dm.getContributorName("12345");

		assertNull(name);

	}

	@Test(expected = IllegalStateException.class)
	public void testHandleException() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}

		});

		dm.getContributorName("12345");
	}

	@Test
	public void testSuccesfulRetrieval() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":\"hello\"}";
			}

		});

		String name = dm.getContributorName("12345");

		assertEquals("hello", name);

	}

}
