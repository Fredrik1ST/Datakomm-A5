package Main;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * This class carries out the various exercises in Datakomm 2018 Assignment A5.
 *
 * @author Fredrik
 */
public class Main {

    // User-defined fields:
    String email = "XXXXXXXXXXXXXXXXXXXXXXX"; // student email
    String phone = "XXXXXXXXXXXXXXXXXXXXXXX"; // student phone #

    // Import the class that handles parsing & requests
    JSONToolbox toolbox = new JSONToolbox();

    // Variables and parameters from the exercises that we want to keep around
    String task1;
    String task2;
    String task3;
    String task4;
    String sessionId = "";

    /**
     * The main function runs through the exercises in the correct order.
     * High coupling going on, but it doesn't matter here.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.authorize(); // Success
        main.getTask(1);
        main.task1();
        main.getTask(2);
        main.task2();
        main.getTask(3);
        main.task3();
        main.getTask(4);
        main.task4();
        main.getTask(Integer.parseInt(main.sessionId));
        main.getResults();
    }

    /*
    * Exercise 1 - Authorize the user.
    * Send a JSON object as HTTP POST to "log into" the server.
     */
    private void authorize() {
        String url = "http://104.248.47.74/dkrest/auth";
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("phone", phone);

        // Send post, and filter out anything useful from the server's response
        String response = toolbox.sendPost(url, json);
        JSONObject jsonTask1Response = new JSONObject(response);
        sessionId = jsonTask1Response.get("sessionId").toString();

    }

    /*
    * Exercise 2 - Get a task from the server.
    * Uses the session ID from exercise 1 to request the next task.
     */
    private void getTask(int taskNr) {
        String url = "http://104.248.47.74/dkrest/gettask/"
                + taskNr + "?sessionId=" + sessionId;

        if (!sessionId.equals("")) {
            switch (taskNr) {
                case 1:
                    task1 = toolbox.sendGet(url);
                    break;
                case 2:
                    task2 = toolbox.sendGet(url);
                    break;
                case 3:
                    task3 = toolbox.sendGet(url);
                    break;
                case 4:
                    task4 = toolbox.sendGet(url);
                    break;
                default:
                    System.out.println("ERROR: Abnormal task number");
                    toolbox.sendGet(url);
                    break;
            }
        } else {
            System.out.println("EXERCISE 2 ERROR: No session ID!");
        }
    }

    /**
     * Exercise 3 - parse the String received from a task to a JSON object
     *
     * @param task The task string to be parsed into JSON format
     * @return The task, as a JSON object
     */
    private JSONObject parseTask(String task) {
        JSONObject jsonTask = new JSONObject(task);
        return jsonTask;
    }

    /**
     * Exercise 4 : Task 1 - Send a "hello" to the server.
     */
    private void task1() {
        JSONObject json = new JSONObject();
        json.put("sessionId", sessionId);
        json.put("msg", "Hello");
        toolbox.sendPost("http://104.248.47.74/dkrest/solve", json);
    }

    /**
     * Exercise 4 : Task 2 - send the response to the request back as an echo.
     */
    private void task2() {
        // Extract the message using JSON parsing
        String msg = parseTask(task2).get("arguments").toString();
        // Remove all brackets and quotation marks
        msg = msg.replaceAll("[\\[\\]\"]", "");
        System.out.println("Task 2: " + msg + "\n");

        JSONObject json = new JSONObject();
        json.put("sessionId", sessionId);
        json.put("msg", msg);
        toolbox.sendPost("http://104.248.47.74/dkrest/solve", json);

    }

    /**
     * Exercise 4 : Task 3 - multiply the numbers in the response
     * then send the result as a HTTP post.
     */
    private void task3() {
        // Extract the message using JSON parsing
        String numberString = parseTask(task3).get("arguments").toString();
        // Remove all brackets and quotation marks, separate elements
        numberString = numberString.replaceAll("[\\[\\]\"]", "");
        numberString = numberString.replaceAll(",", " ");
        System.out.println("Task 3 elements: " + numberString);

        // Convert elements to integers
        String[] stringArray = numberString.split(" ");
        int[] numberArray = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            numberArray[i] = Integer.parseInt(stringArray[i]);
        }
        // Calculate product
        int product = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            product = product * numberArray[i];
        }
        System.out.println("Task 3 result: " + product + "\n");
        // Create and send result
        JSONObject json = new JSONObject();
        json.put("sessionId", sessionId);
        json.put("result", product);
        toolbox.sendPost("http://104.248.47.74/dkrest/solve", json);
    }

    /**
     * Excercise 4 : Task 4 - crack a MD5 hash of a PIN code, return the code.
     */
    private void task4() {
        boolean crackingInProgress = true;
        int pin = 0;
        String pinString;
        String crackHash;
        String result = "NO RESULT";

        // Extract the hash
        String hash = parseTask(task4).get("arguments").toString();
        hash = hash.replaceAll("[\\[\\]\"]", "");
        System.out.println("\nFound hash: " + hash);

        // Try cracking every possible combination using brute force
        // We know that the combinations range from 0-9999
        while (crackingInProgress) {
            try {
                // Format string to four digits
                pinString = String.format("%04d", pin);
                crackHash = toolbox.stringToMD5Hash(pinString);
                if (crackHash.equals(hash)) {
                    result = pinString;
                    System.out.println("Hash cracked! The correct PIN is "
                            + pinString + "!\n");
                    crackingInProgress = false;
                } else if (pin > 10000) {

                }
                pin++;
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Create and send result
        JSONObject json = new JSONObject();
        json.put("sessionId", sessionId);
        json.put("pin", result);
        toolbox.sendPost("http://104.248.47.74/dkrest/solve", json);
    }

    /**
     * Spam the server to find tasks. Does this count as a DDOS?
     */
    private void huntDownTasks() {
        for (int i=0; i<10000; i++) {
            getTask(i);
        }
    }
    
    /**
     * Request our score for this assignment
     */
    private void getResults() {
        toolbox.sendGet("http://104.248.47.74/dkrest/results/" + sessionId);
    }

}
