package ar.edu.iw3.model.business.exceptions;

import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor
public class BusinessException extends Exception {
	@Serial
	private static final long serialVersionUID = 1L;

	@Builder
	public BusinessException(String message, Throwable ex) {
		super(message, ex);
	}
	@Builder
	public BusinessException(String message) {
		super(message);
	}
	@Builder
	public BusinessException(Throwable ex) {
		super(ex.getMessage(), ex);
	}
}
