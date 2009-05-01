import java.io.*;
import java.net.*;
import com.sun.ejte.ccl.reporter.*;

/*
 * Unit test for 6393940 ("precompilation of jsp with jsp fragments"):
 *
 * Make sure compilation errors in JSP segments are ignored during JSP
 * precompilation (during deployment) as long as jsp-config property
 * 'ignoreJspFragmentErrors' has been set to TRUE in sun-web.xml.
 *
 * Otherwise, precompilation of this webapp's JSPs would have failed, 
 * since the 'counter' variable is not declared in any of the included
 * JSP segments.
 */
public class WebTest {

    private static final String TEST_NAME =
        "jsp-precompile-ignore-fragment-errors";

    private static final String EXPECTED_RESPONSE = "counter=2";

    private static SimpleReporterAdapter stat
        = new SimpleReporterAdapter("appserv-tests");

    private String host;
    private String port;
    private String contextRoot;

    public WebTest(String[] args) {
        host = args[0];
        port = args[1];
        contextRoot = args[2];
    }
    
    public static void main(String[] args) {

        stat.addDescription("Unit test for 6393940");
        WebTest webTest = new WebTest(args);

        try {
            webTest.doTest();
        } catch (Exception ex) {
            ex.printStackTrace();
            stat.addStatus(TEST_NAME, stat.FAIL);
        }

	stat.printSummary();
    }

    public void doTest() throws Exception {

        InputStream is = null;
        BufferedReader input = null;
        try {
            URL url = new URL("http://" + host  + ":" + port
                              + contextRoot + "/jsp/test.jsp");
            System.out.println("Connecting to: " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.err.println("Wrong response code. Expected: 200"
                                   + ", received: " + responseCode);
                stat.addStatus(TEST_NAME, stat.FAIL);
            } else {
                is = conn.getInputStream();
                input = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = input.readLine()) != null) {
                    if (EXPECTED_RESPONSE.equals(line)) {
                        break;
                    }
                }

                if (EXPECTED_RESPONSE.equals(line)) {
                    stat.addStatus(TEST_NAME, stat.PASS);
                } else {
                    System.err.println("Wrong response body. Expected: "
                                       + EXPECTED_RESPONSE + ", received: "
                                       + line);
                    stat.addStatus(TEST_NAME, stat.FAIL);
                }
            }
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ex) {}
            try {
                if (input != null) input.close();
            } catch (IOException ex) {}
        }
    }
}
