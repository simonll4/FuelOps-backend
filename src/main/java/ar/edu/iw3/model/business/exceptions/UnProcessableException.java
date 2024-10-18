package ar.edu.iw3.model.business.exceptions;

import lombok.Builder;
import java.io.Serial;

//https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/422
public class UnProcessableException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    @Builder
    public UnProcessableException(String message, Throwable ex) {
        super(message, ex);
    }
    @Builder
    public UnProcessableException(String message) {
        super(message);
    }
    @Builder
    public UnProcessableException(Throwable ex) {
        super(ex.getMessage(), ex);
    }
}

