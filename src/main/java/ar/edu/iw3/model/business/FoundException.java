package ar.edu.iw3.model.business;

import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor
public class FoundException extends Exception {
	@Serial
	private static final long serialVersionUID = 1L;

	@Builder
	public FoundException(String message, Throwable ex) {
		super(message, ex);
	}
	@Builder
	public FoundException(String message) {
		super(message);
	}
	@Builder
	public FoundException(Throwable ex) {
		super(ex.getMessage(), ex);
	}
}
