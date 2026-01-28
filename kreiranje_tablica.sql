DROP TABLE IF EXISTS korisnik CASCADE;
DROP TABLE IF EXISTS student CASCADE;
DROP TABLE IF EXISTS profesor CASCADE;
DROP TABLE IF EXISTS admin CASCADE;
DROP TABLE IF EXISTS predmet CASCADE;
DROP TABLE IF EXISTS upis CASCADE;
DROP TABLE IF EXISTS zadatak CASCADE;
DROP TABLE IF EXISTS predaja_zadatka CASCADE;
DROP TABLE IF EXISTS ocjena CASCADE;

CREATE TABLE IF NOT EXISTS korisnik (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    lozinka_hash VARCHAR(255) NOT NULL,
    ime VARCHAR(100) NOT NULL,
    prezime VARCHAR(100) NOT NULL,
    uloga VARCHAR(50) NOT NULL CHECK (uloga IN ('STUDENT', 'PROFESOR', 'ADMIN'))
    );

CREATE TABLE IF NOT EXISTS student (
    korisnik_id INT PRIMARY KEY,
    jmbag VARCHAR(10) UNIQUE NOT NULL,
    FOREIGN KEY (korisnik_id) REFERENCES korisnik(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS profesor (
    korisnik_id INT PRIMARY KEY,
    titula VARCHAR(50) NOT NULL,
    FOREIGN KEY (korisnik_id) REFERENCES korisnik(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS admin (
    korisnik_id INT PRIMARY KEY,
    ovlasti TEXT NOT NULL,
    FOREIGN KEY (korisnik_id) REFERENCES korisnik(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS predmet (
    id INT AUTO_INCREMENT PRIMARY KEY,
    naziv VARCHAR(255) NOT NULL,
    sifra VARCHAR(10) NOT NULL,
    ects_bodovi INT NOT NULL CHECK (ects_bodovi > 0 AND ects_bodovi <= 30),,
    profesor_id INT,
    semestar VARCHAR(20) NOT NULL CHECK (semestar IN ('ZIMSKI', 'LJETNI')),
    godina_izvodenja INT NOT NULL CHECK (godina_izvodenja BETWEEN 1 AND 5),
    FOREIGN KEY (profesor_id) REFERENCES profesor(korisnik_id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS upis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    predmet_id INT NOT NULL,
    datum_upisa TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    polozen BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT unique_student_predmet UNIQUE (student_id, predmet_id),
    FOREIGN KEY (student_id) REFERENCES student(korisnik_id) ON DELETE CASCADE,
    FOREIGN KEY (predmet_id) REFERENCES predmet(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS zadatak (
    id INT AUTO_INCREMENT PRIMARY KEY,
    naziv VARCHAR(255) NOT NULL,
    opis TEXT NOT NULL,
    predmet_id INT NOT NULL,
    rok_predaje TIMESTAMP NOT NULL,
    datum_objave TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (predmet_id) REFERENCES predmet(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS predaja_zadatka (
    id INT AUTO_INCREMENT PRIMARY KEY,
    zadatak_id INT NOT NULL,
    student_id INT NOT NULL,
    naziv_datoteke VARCHAR(255) NOT NULL,
    tip_datoteke VARCHAR(50) NOT NULL,
    velicina_datoteke BIGINT NOT NULL,
    predana_datoteka BLOB NOT NULL,
    datum_predaje TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'PREDANO' CHECK (status IN ('PREDANO', 'OCJENJENO')),
    CONSTRAINT unique_zadatak_student UNIQUE (zadatak_id, student_id),
    FOREIGN KEY (zadatak_id) REFERENCES zadatak(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(korisnik_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS ocjena (
    id INT AUTO_INCREMENT PRIMARY KEY,
    predaja_id INT NOT NULL UNIQUE,
    profesor_id INT NOT NULL,
    vrijednost INT NOT NULL CHECK (vrijednost BETWEEN 1 AND 5),
    komentar TEXT NOT NULL,
    datum_ocjenjivanja TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (predaja_id) REFERENCES predaja_zadatka(id) ON DELETE CASCADE,
    FOREIGN KEY (profesor_id) REFERENCES profesor(korisnik_id) ON DELETE CASCADE
    );