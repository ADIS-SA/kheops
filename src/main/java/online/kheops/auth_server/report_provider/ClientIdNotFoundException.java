package online.kheops.auth_server.report_provider;

import online.kheops.auth_server.util.ErrorResponse;
import online.kheops.auth_server.util.KheopsException;

public class ClientIdNotFoundException extends Exception implements KheopsException {

    private ErrorResponse errorResponse;

    public ClientIdNotFoundException(ErrorResponse errorResponse) {

        super();
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
