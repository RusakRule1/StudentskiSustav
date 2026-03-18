# Studentski Sustav

Sveobuhvatna JavaFX desktop aplikacija za upravljanje akademskim poslovanjem sveučilišta. Sustav podržava upravljanje studentima, profesorima, predmetima, zadacima, predajama i ocjenama s višerazinskom kontrolom pristupa temeljenom na ulogama.

## Sadržaj

- [Opis projekta](#opis-projekta)
- [Tehnologije](#tehnologije)
- [Struktura projekta](#struktura-projekta)
- [Preduvjeti](#preduvjeti)
- [Pokretanje](#pokretanje)
- [Korisničke uloge](#korisničke-uloge)
- [Sigurnost](#sigurnost)

---

## Opis projekta

Aplikacija simulira informacijski sustav obrazovne institucije. Omogućuje:

- Prijavu korisnika s ulogama (Student, Profesor, Admin)
- Upravljanje predmetima, upisima i zadacima
- Predaju rješenja i ocjenjivanje
- Generiranje PDF izvještaja
- Revizijski dnevnik (audit log) svih administrativnih akcija
- Kriptiranu pohranu osobnih podataka

---

## Tehnologije

| Kategorija      | Tehnologija                           | Verzija     |
| --------------- | ------------------------------------- | ----------- |
| Jezik           | Java                                  | JDK 25      |
| UI Framework    | JavaFX                                | 25.0.2      |
| Build alat      | Maven                                 | —           |
| Baza podataka   | H2 Database                           | 2.4.240     |
| ORM             | Hibernate / Jakarta Persistence (JPA) | 7.2.0       |
| JSON            | Gson                                  | 2.13.2      |
| XML             | Jakarta XML Bind (JAXB)               | 4.0.4       |
| PDF generiranje | iText                                 | 7.2.5       |
| Konfiguracija   | ini4j                                 | 0.5.4       |
| Enkripcija      | AES-256, RSA-2048, SHA-256            | Java stdlib |

---

## Struktura projekta

    studentski-sustav/
    ├── src/
    │   ├── main/
    │   │   ├── java/projekt/
    │   │   │   ├── adapter/          # JNI adaptori
    │   │   │   ├── dao/              # Data Access Objects (CRUD)
    │   │   │   ├── izvjestaj/        # Generiranje PDF izvještaja
    │   │   │   ├── model/            # JPA entiteti (Student, Profesor, Predmet...)
    │   │   │   ├── pogled/           # JavaFX pogledi (ekrani)
    │   │   │   ├── repozitorij/      # Repository pattern
    │   │   │   ├── servis/           # Poslovna logika
    │   │   │   ├── upravitelj/       # Menadžeri (sesija, baza, navigacija)
    │   │   │   └── util/             # Pomoćne klase (enkripcija, validacija, UI)
    │   │   │       └── graditelj/    # Builder pattern za UI komponente
    │   │   └── resources/
    │   │       ├── META-INF/persistence.xml
    │   │       └── css/              # JavaFX stilovi
    │   └── test/
    ├── bazaPodataka/                 # H2 datoteka baze
    ├── lib/                          # Vanjske biblioteke
    ├── pom.xml
    ├── kreiranje_tablica.sql         # SQL shema i testni podaci
    ├── kreiraj_popuni_bazu.bat       # Skripta za inicijalizaciju baze
    └── pokreni_H2Server.bat          # Skripta za pokretanje H2 servera

---

## Preduvjeti

- **JDK 25** ili noviji
- **Maven** (konfiguriran u PATH-u)
- **H2 Database** (uključen, pokreće se lokalno)

---

## Pokretanje

### 1. Generiranje kriptografskih ključeva _(samo prvi put)_

Pokrenuti klasu `projekt.util.GeneratorKljuceva`. Ključevi se spremaju u:

    ~/.studentski-sustav/podaci/

### 2. Pokretanje H2 servera

    pokreni_H2Server.bat

Server se pokreće na portu **9092** i mora biti aktivan dok aplikacija radi.

### 3. Inicijalizacija baze podataka _(samo prvi put)_

    kreiraj_popuni_bazu.bat

Izvršava `kreiranje_tablica.sql` — kreira tablice i unosi inicijalne podatke.

### 4. Pokretanje aplikacije

    mvn clean compile javafx:run

Aplikacija se pokreće putem JavaFX Maven plugina. Entry point: `projekt.Main`.

---

## Korisničke uloge

| Uloga        | Mogućnosti                                                                    |
| ------------ | ----------------------------------------------------------------------------- |
| **Student**  | Pregled upisanih predmeta, predaja zadataka, pregled ocjena                   |
| **Profesor** | Kreiranje predmeta i zadataka, ocjenjivanje predanih rješenja, PDF izvještaji |
| **Admin**    | Upravljanje korisnicima, pregled revizijskog dnevnika                         |

---

## Sigurnost

- Lozinke se pohranjuju kao **SHA-256 hash sa salt i pepper vrijednostima**
- Osobni podaci (ime, prezime) enkriptirani su **AES-256 (CBC/PKCS5Padding)**
- Dodatna zaštita podataka korištenjem **RSA-2048 (OAEP padding)**
- Sva administrativna djelovanja bilježe se u **audit log** (`Zapis` entitet)

---

## Baza podataka

- **JDBC URL:** `jdbc:h2:tcp://localhost:9092/./studentski_sustav`
- **Korisnik:** `sa`
- **Lozinka:** _(prazna)_
- Hibernate automatski ažurira shemu (`hbm2ddl.auto=update`)
