package Main;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;
import java.lang.Math;
import java.util.Arrays;

/**
 * This class carries out the various exercises in Datakomm 2018 Assignment A5.
 *
 * @author Fredrik
 */
public class Main {

    // User-defined fields:
    String email = "fredrtak@stud.ntnu.no"; // student email
    String phone = "90908033"; // student phone #

    // Import the class that handles parsing & requests
    JSONToolbox toolbox = new JSONToolbox();

    // Variables and parameters from the exercises that we want to keep around
    String task1;
    String task2;
    String task3;
    String task4;
    Double secretTaskNr;
    String secretTaskString;

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
        /*
        main.getTask("1");
        main.task1();
        main.getTask("2");
        main.task2();
        main.getTask("3");
        main.task3();
        main.getTask("4");
        main.task4();
        main.getTask("2016"); // This is the secret task number
        main.doSecretTask();
         */

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
    private void getTask(String taskNr) {
        String url = "http://104.248.47.74/dkrest/gettask/"
                + taskNr + "?sessionId=" + sessionId;

        if (!sessionId.equals("")) {
            switch (taskNr) {
                case "1":
                    task1 = toolbox.sendGet(url);
                    break;
                case "2":
                    task2 = toolbox.sendGet(url);
                    break;
                case "3":
                    task3 = toolbox.sendGet(url);
                    break;
                case "4":
                    task4 = toolbox.sendGet(url);
                    break;
                default:
                    System.out.println("ERROR: Abnormal task number");
                    secretTaskString = toolbox.sendGet(url);
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
     * Solve the riddle to find the secret task number.
     * Seems to always be the square root of a certain number.
     */
    private void findSecretTask() {
        getTask("5");
        JSONObject secretTask = new JSONObject(secretTaskString);
        String secretString = secretTask.get("comment").toString();
        secretString = secretString.replaceAll("[^0-9]", "");
        int secretInt = Integer.parseInt(secretString);
        secretTaskNr = Math.sqrt(secretInt);
    }

    /**
     * Do the secret task.
     */
    private void doSecretTask() {
        JSONObject secretTask = new JSONObject(secretTaskString);
        JSONArray arguments = secretTask.getJSONArray("arguments");
        String networkIp = arguments.getString(0);
        String subnetMask = arguments.getString(1);
        System.out.println("IP: " + networkIp + "\nSubnet mask: " + subnetMask + "\n");

        String[] networkIpArray = networkIp.split("\\.");
        String[] subnetMaskArray = subnetMask.split("\\.");

        System.out.println("Array values:");
        for (String s : networkIpArray) {
            System.out.print(s + " ");
        }
        System.out.print("\n");
        for (String s : subnetMaskArray) {
            System.out.print(s + " ");
        }

        // Attempt 1: Create a valid IP address
        // GOT A LUCKY GUESS! When subnet is below 255, the host addresses
        // start at 254 and go downwards from there.
        for (int i = 0; i < networkIpArray.length; i++) {
            if (!subnetMaskArray[i].equals("255")) {
                //int newInt = Integer.parseInt(networkIpArray[i]) + 1;
                networkIpArray[i] = "128";
            }
        }

        /* Attempt 2: Create a valid IP address
        boolean done = false;
        int tries = 0;
        while (!done & tries<networkIpArray.length) {
            if (subnetMaskArray[tries].equals("0")) {
                int newInt = Integer.parseInt(networkIpArray[tries]) + 1;
                networkIpArray[tries] = Integer.toString(newInt);
                done = true;
            }
            tries++;
        } */
        // Format the new IP address properly (XXX.XXX.XXX.XXX)
        String newNetworkIp = "";
        newNetworkIp = String.format("%03d", Integer.parseInt(networkIpArray[0]));
        for (int i = 1; i < networkIpArray.length; i++) {
            newNetworkIp = newNetworkIp + ("." + String.format("%03d", Integer.parseInt(networkIpArray[i])));
        }
        System.out.println("\n\nNew IP: " + newNetworkIp + "\n");

        JSONObject json = new JSONObject();
        json.put("sessionId", sessionId);
        json.put("ip", newNetworkIp);
        toolbox.sendPost("http://104.248.47.74/dkrest/solve", json);
    }

    /**
     * Request our score for this assignment
     */
    private void getResults() {
        toolbox.sendGet("http://104.248.47.74/dkrest/results/" + sessionId);
    }

}
