package com.giua.webscraper;
/*
 * Raccolta degli errori custom
 */
public class GiuaScraperExceptions {
    public static class SessionCookieEmpty extends RuntimeException {
        public SessionCookieEmpty(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class UnableToLogin extends RuntimeException {
        public UnableToLogin(String errorMessage) {
            super(errorMessage);
        }
    }
}
