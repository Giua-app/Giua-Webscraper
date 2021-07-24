/*
 * Giua Webscraper library
 * A webscraper of the online school workbook giua@school
 * Copyright (C) 2021 - 2021 Hiem, Franck1421 and contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package com.giua.webscraper;

import java.io.Serializable;

/*
 * Raccolta degli errori custom
 */
public class GiuaScraperExceptions implements Serializable {

    public static class UnableToLogin extends RuntimeException {
        public UnableToLogin(String errorMessage, Throwable err) {
            super("Unable to login: " + errorMessage,err);
        }
        public UnableToLogin(String errorMessage) {
            super("Unable to login: " + errorMessage);
        }
    }

    public static class SessionCookieEmpty extends RuntimeException {
        public SessionCookieEmpty(String errorMessage) {
            super(errorMessage);
        }

        public SessionCookieEmpty(String errorMessage, Exception e) {
            super(errorMessage);
            e.printStackTrace();
        }
    }

    public static class UnsupportedAccount extends RuntimeException {
        public UnsupportedAccount(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class NotLoggedIn extends RuntimeException {
        public NotLoggedIn(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class YourConnectionProblems extends RuntimeException {
        public YourConnectionProblems(String errorMessage) {
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
