import static org.junit.Assert.*;
import java.util.Map;
import org.junit.Test;

public class DataManager_updateOrgInfo_Test {
    private DataManager dm;

    @Test(expected=IllegalStateException.class)
    public void testNullClient() {
        Organization org = new Organization("id", "name", "description");
        dm = new DataManager(null);
        dm.updateAccountInfo(org, "name", "description");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullOrg() {
        dm = new DataManager(new WebClient("localhost", 3001));
        dm.updateAccountInfo(null, "name", "description");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullId() {
        Organization org = new Organization(null, "name", "description");
        dm = new DataManager(new WebClient("localhost", 3001));
        dm.updateAccountInfo(org, "name", "description");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullName() {
        Organization org = new Organization("id", "name", "description");
        dm = new DataManager(new WebClient("localhost", 3001));
        dm.updateAccountInfo(org, null, "description");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullDescription() {
        Organization org = new Organization("id", "name", "description");
        dm = new DataManager(new WebClient("localhost", 3001));
        dm.updateAccountInfo(org, "name", null);
    }

    @Test(expected=IllegalStateException.class)
    public void testUpdateAccountInfoErrorOne() {
        Organization org = new Organization("id", "name", "description");
        dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"," +
                        "\"error\":\"error\"}";
            }
        });
        dm.updateAccountInfo(org, "name2", "description2");
    }

    @Test(expected=IllegalStateException.class)
    public void testUpdateAccountInfoErrorTwo() {
        Organization org = new Organization("id", "name", "description");
        dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "error";
            }
        });
        dm.updateAccountInfo(org, "newName", "newDescription");
    }

    @Test
    public void testUpdateAccountInfoSuccess() {
        Organization org = new Organization("id", "name", "description");
        dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\"," +
                        "\"data\":{" +
                            "\"_id\":\"12345\"," +
                            "\"name\":\"new org\"," +
                            "\"description\":\"new description\"," +
                            "\"funds\":[]}}";
            }
        });

        dm.updateAccountInfo(org, "newName", "newDescription");

        assertEquals(org.getName(), "newName");
        assertEquals(org.getDescription(), "newDescription");
    }
}