import static org.junit.Assert.*;
import java.util.Map;
import org.junit.Test;

public class DataManager_changePassword_Test {
    private DataManager dm;

    @Test(expected=IllegalStateException.class)
    public void testNullClient() {
        dm = new DataManager(null);
        dm.changePassword("orgId", "password");

    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullId() {
        dm = new DataManager(new WebClient("localhost", 3001));
        dm.changePassword(null, "password");

    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullName() {
        dm = new DataManager(new WebClient("localhost", 3001));
        dm.changePassword("orgId", null);
    }

    @Test(expected=IllegalStateException.class)
    public void testUpdatePasswordErrorOne() {
        dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"error\"}";
            }
        });
        dm.changePassword("orgId", "password");
    }

    @Test(expected=IllegalStateException.class)
    public void testUpdatePasswordErrorTwo() {
        dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return  "error";
            }
        });
        dm.changePassword("orgId", "password");
    }

    @Test
    public void testUpdatePasswordSuccess() {
        dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\"," +
                        "\"password\":\"password\"," +
                        "\"data\":{" +
                            "\"_id\":\"orgId\"," +
                            "\"name\":\"new org\"," +
                            "\"description\":\"new description\"," +
                            "\"funds\":[]}}";
            }
        });

        String newPassword = dm.changePassword("orgId", "newPassword");
        assertEquals(newPassword, "newPassword");
    }
}