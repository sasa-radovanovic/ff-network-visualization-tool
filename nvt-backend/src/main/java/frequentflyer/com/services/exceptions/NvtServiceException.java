package frequentflyer.com.services.exceptions;

/**
 * Created by sasaradovanovic on 10/17/17.
 */
public class NvtServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NvtServiceException() {
        super();
    }

    public NvtServiceException(String message) {
        super(message);
    }

    public NvtServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NvtServiceException(Throwable cause) {
        super(cause);
    }

}
