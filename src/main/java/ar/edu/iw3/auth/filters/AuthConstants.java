package ar.edu.iw3.auth.filters;

public final class AuthConstants {
	public static final long EXPIRATION_TIME = (12 * 60 * 60 * 1000); //  12 hs
	public static final String SECRET = "MyVerySecretKey";
	
	public static final String AUTH_HEADER_NAME = "Authorization";
	public static final String AUTH_PARAM_NAME = "authtoken";
	public static final String TOKEN_PREFIX = "Bearer ";
}

