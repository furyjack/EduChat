package com.example.admin.educhat.utils;

/**
 * Created by Ankit on 20/08/2016.
 */
public class Urls {
    public static final String BASE_URL = "http://api.edunuts.com/";
    public static final String LOGIN_URL = BASE_URL + "partners/login";
    public static final String LOGOUT_URL = BASE_URL + "partners/logout";
    public static final String SIGN_UP_URL = BASE_URL + "partners/signup";
    public static final String INSTITUTE_URL = BASE_URL + "partners/search/institutes?q=";
    public static final String OTP_REQUEST = BASE_URL + "partners/otp/request";
    public static final String OTP_VERIFY = BASE_URL + "partners/otp/verify";
    public static final String LEAD_URL = BASE_URL + "partners/leads";
    public static final String FORGOT_PASSWORD = BASE_URL + "partners/forgot-password";
    public static final String RESET_PASSWORD = BASE_URL + "partners/reset-password";
    public static final String BUY_CREDITS = BASE_URL + "partners/buy-credits";
    public static final String SEARCH_LEADS = BASE_URL + "partners/leads?q=";
    public static final String CONTACTED_URL = BASE_URL + "partners/leads/contacted";
    public static final String CONVERTED_URL = BASE_URL + "partners/leads/converted";
}
