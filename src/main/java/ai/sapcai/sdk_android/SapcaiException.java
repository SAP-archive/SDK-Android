package ai.sapcai.sdk_android;

import java.util.HashMap;
import java.util.Map;

public class SapcaiException extends RuntimeException {
    private int statusCode;
    public SapcaiException(final String message) {
        super(message);
        this.statusCode = -1;
    }

    public SapcaiException(int statusCode) {
        this(getSapcaiErrorMessage(statusCode));
        this.statusCode = statusCode;
    }

    public SapcaiException(final String message, final Throwable cause) {
        super(message, cause);
    }

    private static String getSapcaiErrorMessage(int statusCode) {
        Map<Integer, String> errorMessages = new HashMap<>();
        errorMessages.put(400, "400: Bad request");
        errorMessages.put(415, "415: Unsupported media type");
        errorMessages.put(503, "503: Service unavailable");
        errorMessages.put(401, "401: Unauthorized");
        return errorMessages.get(statusCode);
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
