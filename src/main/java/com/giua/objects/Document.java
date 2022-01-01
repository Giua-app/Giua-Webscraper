/*
 * Giua Webscraper library
 * A webscraper of the online school workbook giua@school
 * Copyright (C) 2021 - 2022 Hiem, Franck1421 and contributors
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

package com.giua.objects;

public class Document {
    public final String status;
    public final String classroom;
    public final String subject;
    public final String institute;
    public final String downloadUrl;

    public Document(String status, String classroom, String subject, String institute, String downloadUrl) {
        this.status = status;
        this.classroom = classroom;
        this.subject = subject;
        this.institute = institute;
        this.downloadUrl = downloadUrl;
    }

    public String toString() {
        return status + "; " + classroom + "; " + subject + "; " + institute + "; " + downloadUrl;
    }
}
