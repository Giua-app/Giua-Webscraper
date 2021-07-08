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

    public static class InternetProblems extends RuntimeException {
        public InternetProblems(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class SiteConnectionProblems extends RuntimeException {
        public SiteConnectionProblems(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class UnableToGetUserType extends RuntimeException {
        public UnableToGetUserType(String errorMessage, Exception e) {
            super(errorMessage);
            e.printStackTrace();
        }
    }

    public static class UnableToGetMaintenanceInfo extends RuntimeException {
        public UnableToGetMaintenanceInfo(String errorMessage, Exception e) {
            super(errorMessage);
            e.printStackTrace();
        }
    }

    public static class MaintenanceIsActiveException extends RuntimeException {
        public MaintenanceIsActiveException(String errorMessage) {
            super(errorMessage);
            //e.printStackTrace();
        }
    }

}
