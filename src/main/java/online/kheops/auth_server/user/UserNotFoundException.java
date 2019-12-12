package online.kheops.auth_server.user;

import online.kheops.auth_server.util.ErrorResponse;
import online.kheops.auth_server.util.KheopsException;

public class UserNotFoundException extends Exception implements KheopsException {

    private ErrorResponse errorResponse;

    public UserNotFoundException(ErrorResponse errorResponse) {
        super();
        this.errorResponse = errorResponse;
    }

    public UserNotFoundException() {
        super();
        this.errorResponse = new ErrorResponse.ErrorResponseBuilder()
                .message("User not found")
                .detail("The user is unknown by Kheops")
                .build();
    }

    public UserNotFoundException(Throwable e) {
        super(e);
        this.errorResponse = new ErrorResponse.ErrorResponseBuilder()
                .message("User not found")
                .detail("The user is unknown by Kheops")
                .build();
    }

    public UserNotFoundException(ErrorResponse errorResponse, Throwable e) {
        super(e);
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() { return errorResponse; }
}
