package Controllers;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SMSService {

    // Twilio Account SID and Auth Token
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";

    // Twilio phone number (from your Twilio console)
    public static final String TWILIO_PHONE_NUMBER = "+18382311934";

    // Admin phone number to receive the SMS
    public static final String ADMIN_PHONE_NUMBER = "+21620432066";

    // Initialize Twilio
    public static void initializeTwilio() {
        // Initialize Twilio with your account SID and auth token
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    // Method to send SMS with a static message
    public static void sendSMS() {
        // Static message
        String messageBody = "There is a new feedback, check it!";

        Message message = Message.creator(
                new PhoneNumber(ADMIN_PHONE_NUMBER), // Admin's phone number
                new PhoneNumber(TWILIO_PHONE_NUMBER), // Your Twilio phone number
                messageBody // The message body content
        ).create();

        System.out.println("Message sent with SID: " + message.getSid());
    }
}
