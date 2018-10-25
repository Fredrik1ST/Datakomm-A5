package Main;

import org.json.JSONObject;

/**
 *
 * @author Fredrik
 */
public class Main {

    // User-defined fields:
    String email = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"; // student email
    String phone = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"; // student phone #
    
    // Import the class that handles parsing & requests
    JSONToolbox Toolbox = new JSONToolbox();

    // Variables and parameters from the tasks that we want to keep around
    String sessionId = "";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.task1(); // Success

    }

    /*
    * Task 1
    * Send a JSON object as HTTP POST to "log into" the server.
     */
    private void task1() {
        String url = "http://104.248.47.74/dkrest/auth";
        JSONObject jsonTask1 = new JSONObject();
        jsonTask1.put("email", email);
        jsonTask1.put("phone", phone);

        // Send post, and filter out anything useful from the server's response
        String response = Toolbox.sendPost(url, jsonTask1);
        JSONObject jsonTask1Response = new JSONObject(response);
        sessionId = jsonTask1Response.get("sessionId").toString();

    }

    /*
    * Task 2
    * Uses the session ID from task 1 to request the next task.
     */
    private void task2() {

    }

}
