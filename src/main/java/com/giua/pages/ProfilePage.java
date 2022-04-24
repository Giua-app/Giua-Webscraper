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

package com.giua.pages;

import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ProfilePage implements IPage {
    private GiuaScraper gS;
    private Document doc;

    public ProfilePage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }


    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.PROFILE_PAGE);
    }

    /**
     * Ottieni le informazioni del profilo
     *
     * @return Un vettore di stringhe in cui:
     * <br>Indice 0: Utente
     * <br>Indice 1: Tipo account
     * <br>Indice 2: email
     * <br><br>Può restituire null in caso non abbia trovato gli elementi
     */
    public String[] getProfileInformation() {
        if (gS.isDemoMode())
            return new String[]{"Mario Rossi genitore di Marietto Rossi", "Genitore", "mariorossi@example.com"};

        Element a = doc.getElementsByTag("dl").get(0);

        if (a.childrenSize() < 6) return null;

        String utente = a.child(1).text();
        String ruolo = a.child(3).text();
        String email = a.child(5).text();

        return new String[]{utente, ruolo, email};
    }
}