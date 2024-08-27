package ar.edu.iw3.util;

import org.springframework.http.HttpStatus;

public interface IStandartResponseBusiness {
	public StandartResponse build(HttpStatus httpStatus, Throwable ex, String message);

}
