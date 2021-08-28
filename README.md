# Giua-Webscraper

<p align='center'>
  <a href='https://github.com/Giua-app/Giua-Webscraper/blob/master/LICENSE'><img src='https://img.shields.io/github/license/Giua-app/Giua-Webscraper'/></a>
  <img src='https://img.shields.io/github/v/tag/Giua-app/Giua-Webscraper?label=version&include_prereleases&color=success'/>
  <a href='https://github.com/Giua-app/Giua-Webscraper/actions/workflows/maven.yml'><img src='https://github.com/Giua-app/Giua-Webscraper/actions/workflows/maven.yml/badge.svg'/></a>
</p>


## Che cos'è?
Giua-webscraper è una libreria per ottenere informazioni dai registri elettronici [giua@school](https://github.com/trinko/giuaschool) 

Può raccogliere informazioni su:
- Verifiche
- Voti
- Circolari
- Avvisi
- Compiti
- Assenze
- Pagelle
- Lezioni
- e molto altro ancora

## Installazione (come dipendenza Android)
Aggiungi JitPack al tuo top-level `build.gradle`
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Inserisci la dipendenza nel `build.gradle` module (subproject)
```gradle
dependencies {
    // Giua scraper
    implementation 'com.github.Giua-app:Giua-Webscraper:VERSION'
}
```

Sostituisci "VERSION" con il numero scritto qui (senza la v iniziale) ![](https://img.shields.io/github/v/release/Giua-app/Giua-Webscraper?color=success&include_prereleases&label=%E2%80%8B&logo=github)


## Come posso contribuire?
Contatta HiemSword o Franck1421
