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
        public UnableToLogin(String errorMessage, Exception e) {
            super(errorMessage);
            e.printStackTrace();
        }
    }

    public static class NotLoggedIn extends RuntimeException {
        public NotLoggedIn(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class UnableToGetUserType extends RuntimeException {
        public UnableToGetUserType(String errorMessage, Exception e) {
            super(errorMessage);
            e.printStackTrace();
        }
    }
}
