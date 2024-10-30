package ar.edu.iw3;

public final class Constants {
	public static final String URL_API = "/api";
	public static final String URL_API_VERSION = "/v1";
	public static final String URL_BASE = URL_API + URL_API_VERSION;
	public static final String URL_PRODUCTS = URL_BASE + "/products";
	public static final String URL_ORDERS = URL_BASE + "/orders";
	public static final String URL_SUPPLIERS = URL_BASE + "/suppliers";

	public static final String URL_INTEGRATION = URL_BASE + "/integration";
	public static final String URL_INTEGRATION_CLI1 = URL_INTEGRATION + "/cli1";
	public static final String URL_INTEGRATION_CLI2 = URL_INTEGRATION + "/cli2";
	public static final String URL_INTEGRATION_CLI3 = URL_INTEGRATION + "/cli3";

	public static final String URL_LOGIN = URL_BASE + "/login";
	public static final String URL_AUTHORIZATION = URL_BASE + "/authtest";

	public static final String URL_MAIL = URL_BASE + "/mail";
}
