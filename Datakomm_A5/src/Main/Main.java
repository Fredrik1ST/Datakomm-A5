package Main;

import org.json.JSONObject;

/**
 * This class carries out the various exercises in Datakomm 2018 Assignment A5.
 *
 * @author Fredrik
 */
public class Main {

    // User-defined fields:
    String email = "XXXXXXXXXXXXXXXXXXX"; // student email
    String phone = "XXXXXXXXXXXXXXXXXXX"; // student phone #

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
                    System.out.println("EXERCISE 2 ERROR: Invalid task number!");
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

}
