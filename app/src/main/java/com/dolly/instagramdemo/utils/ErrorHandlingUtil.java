package com.dolly.instagramdemo.utils;

import android.content.Context;
import android.widget.Toast;

import com.dolly.instagramdemo.model.BaseInstagramResponse;

public class ErrorHandlingUtil {
    // Show error to the user showing the message.
    public static void showErrorToUser(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // It is worth noting that the method "isCorrectInstagramResponse" takes in "BaseInstagramResponse" object.
    // All Instagram responses contain the "meta" field. The code value of 200 in the meta indicates the success.
    // Anything else indicates some sort of error. The two classes: "FeedInstagramResponse" and
    // "LikedByInstagramResponse" extend this class. The benefit of keeping meta field in a base class is that
    // the error handling of the response can be done in a clean manner, irrespective of the type of response.

    // https://api.instagram.com/v1/users/self/media/recent/?access_token=2200473475.1a35374.e139e3935a98413e9d527561e5ed8a70

    public static boolean isCorrectInstagramResponse(Context context, BaseInstagramResponse responseBody) {
        // First handle the error cases.
        // Case 1: Response body is null or empty, or if meta is null.
        if (responseBody == null || responseBody.getMeta() == null) {
            showErrorToUser(context, "Unexpected error: Empty response received from Instagram API. Please try again later.");
            return false;
        }

        // Case 2: Meta is not null, but the code is not 200.
        if (responseBody.getMeta().getCode() != 200) {
            String errorType = responseBody.getMeta().getErrorType();
            if (errorType.contains("OAuthAccessToken")) {
                // If the error type contains OAuth, then either the access token is invalid or expired.
                // Ask the user to authenticate again.
                // TODO: Ideally, we should redirect the user to reauthenticate. However,
                // auth tokens are never expired in instagram. So, not doing that.
                showErrorToUser(context, "Access token is invalid or expired. Please log in again.");
            } else {
                showErrorToUser(context, "Something went wrong. Instagram API returned: " + responseBody);
            }
            return false;
        }

        return true;
    }
}
