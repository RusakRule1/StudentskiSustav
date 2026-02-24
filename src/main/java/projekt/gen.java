package projekt;

import projekt.util.AES;
import projekt.util.Hash;
import projekt.util.RSA;

public class gen {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("GENERIRANJE SQL ZA BAZU SA ŠIFRIRANJEM");
        System.out.println("=========================================\n");

        // Studenti - 30 studenata ukupno (10 originalnih + 20 novih)
        String[] studenti = {
                // Originalnih 10
                "ivan.horvat@student.unizg.hr",
                "ana.kovacevic@student.unizg.hr",
                "marko.babic@student.unizg.hr",
                "petra.novak@student.unizg.hr",
                "luka.juric@student.unizg.hr",
                "maja.pavic@student.unizg.hr",
                "tomislav.maric@student.unizg.hr",
                "sara.knezevic@student.unizg.hr",
                "david.bozic@student.unizg.hr",
                "karla.varga@student.unizg.hr",
                // Novih 20 studenata
                "mateo.zupan@student.unizg.hr",
                "lena.kralj@student.unizg.hr",
                "lovro.simic@student.unizg.hr",
                "ella.milakovic@student.unizg.hr",
                "leo.tomic@student.unizg.hr",
                "nina.grgic@student.unizg.hr",
                "jakov.vasic@student.unizg.hr",
                "tea.saric@student.unizg.hr",
                "noa.peric@student.unizg.hr",
                "emma.antonic@student.unizg.hr",
                "frane.marjanovic@student.unizg.hr",
                "lana.kos@student.unizg.hr",
                "viktor.miletic@student.unizg.hr",
                "lucia.stankovic@student.unizg.hr",
                "bruno.matic@student.unizg.hr",
                "dorotea.petrovic@student.unizg.hr",
                "alex.loncar@student.unizg.hr",
                "klara.barisic@student.unizg.hr",
                "niko.popovic@student.unizg.hr",
                "mila.simunovic@student.unizg.hr"
        };

        // Profesori - 10 profesora ukupno (5 originalnih + 5 novih)
        String[] profesori = {
                // Originalnih 5
                "miljenko.kovac@prof.unizg.hr",
                "dragan.petrovic@prof.unizg.hr",
                "ivana.matic@prof.unizg.hr",
                "ante.horvat@prof.unizg.hr",
                "marina.kolar@prof.unizg.hr",
                // Novih 5 profesora
                "zlatan.ivkovic@prof.unizg.hr",
                "sanja.milutinovic@prof.unizg.hr",
                "boris.novak@prof.unizg.hr",
                "dunja.simic@prof.unizg.hr",
                "igor.mandic@prof.unizg.hr"
        };

        // Administrator
        String admin = "admin.sustav@unizg.hr";

        System.out.println("KOMPLETAN SQL INSERT KOD SA ŠIFRIRANJEM:");
        System.out.println("=========================================\n");

        System.out.println("-- Prvo isključimo provjeru stranih ključeva");
        System.out.println("SET FOREIGN_KEY_CHECKS = 0;\n");

        System.out.println("-- 1. Umetanje korisnika (ime i prezime šifrirano)");
        System.out.println("INSERT INTO korisnik (email, lozinka_hash, sifrirano_ime, sifrirano_prezime, uloga) VALUES");

        // Studenti INSERT - 30 studenata
        for (int i = 0; i < studenti.length; i++) {
            String email = studenti[i];
            String lozinka = "student" + (i + 1);
            String hash = Hash.hashirajLozinku(lozinka, email);
            String ime = getImeStudenta(i);
            String prezime = getPrezimeStudenta(i);

            // Šifriraj ime i prezime
            String sifriranoIme = AES.sifriraj(ime);
            String sifriranoPrezime = RSA.sifriraj(prezime);

            System.out.print("  ('" + email + "', '" + hash + "', '" +
                    escapeSQL(sifriranoIme) + "', '" +
                    escapeSQL(sifriranoPrezime) + "', 'STUDENT')");
            if (i < studenti.length - 1 || profesori.length > 0) {
                System.out.println(",");
            }
        }

        // Profesori INSERT - 10 profesora
        for (int i = 0; i < profesori.length; i++) {
            String email = profesori[i];
            String lozinka = "profesor" + (i + 1);
            String hash = Hash.hashirajLozinku(lozinka, email);
            String ime = getImeProfesora(i);
            String prezime = getPrezimeProfesora(i);

            // Šifriraj ime i prezime
            String sifriranoIme = AES.sifriraj(ime);
            String sifriranoPrezime = RSA.sifriraj(prezime);

            System.out.print("  ('" + email + "', '" + hash + "', '" +
                    escapeSQL(sifriranoIme) + "', '" +
                    escapeSQL(sifriranoPrezime) + "', 'PROFESOR')");
            if (i < profesori.length - 1 || admin != null) {
                System.out.println(",");
            }
        }

        // Admin INSERT
        String adminIme = "Administrator";
        String adminPrezime = "Sustav";
        String adminLozinka = "admin1";
        String adminHash = Hash.hashirajLozinku(adminLozinka, admin);
        String sifriranoAdminIme = AES.sifriraj(adminIme);
        String sifriranoAdminPrezime = RSA.sifriraj(adminPrezime);

        System.out.println("  ('" + admin + "', '" + adminHash + "', '" +
                escapeSQL(sifriranoAdminIme) + "', '" +
                escapeSQL(sifriranoAdminPrezime) + "', 'ADMIN');\n");

        System.out.println("-- 2. Umetanje studenata (30 studenata)");
        System.out.println("INSERT INTO student (korisnik_id, jmbag) VALUES");
        for (int i = 1; i <= 30; i++) {
            String jmbag = String.format("0036%06d", 1000 + i);
            System.out.print("  (" + i + ", '" + jmbag + "')");
            if (i < 30) {
                System.out.println(",");
            } else {
                System.out.println(";\n");
            }
        }

        System.out.println("-- 3. Umetanje profesora (10 profesora)");
        System.out.println("INSERT INTO profesor (korisnik_id, titula) VALUES");
        String[] titule = {
                // Originalne 5
                "Redoviti profesor",
                "Docent",
                "Izvorni profesor",
                "Profesor dr. sc.",
                "Docent dr. sc.",
                // Nove 5
                "Profesor",
                "Redoviti profesor dr. sc.",
                "Profesor",
                "Docent",
                "Profesor emeritus"
        };
        for (int i = 0; i < 10; i++) {
            System.out.print("  (" + (31 + i) + ", '" + escapeSQL(titule[i]) + "')");
            if (i < 9) {
                System.out.println(",");
            } else {
                System.out.println(";\n");
            }
        }

        System.out.println("-- 4. Umetanje administratora");
        System.out.println("INSERT INTO admin (korisnik_id, ovlasti) VALUES");
        System.out.println("  (41, 'FULL_ACCESS: Korisnici, Studenti, Profesori, Predmeti, Ocjene, Financije');\n");

        System.out.println("-- Vraćamo provjeru stranih ključeva");
        System.out.println("SET FOREIGN_KEY_CHECKS = 1;\n");

        // Prikaži RSA javni ključ za pohranu
        System.out.println("=========================================");
        System.out.println("RSA JAVNI KLJUČ (spremite u datoteku):");
        System.out.println("=========================================");
        System.out.println();

        System.out.println("=========================================");
        System.out.println("POPIS LOZINKA ZA TESTIRANJE:");
        System.out.println("=========================================");
        System.out.println("Studenti koriste: student1 do student30");
        System.out.println("Profesori koriste: profesor1 do profesor10");
        System.out.println("Admin koristi: admin1");
        System.out.println("=========================================");
    }

    private static String getImeStudenta(int index) {
        String[] imena = {
                // Originalnih 10
                "Ivan", "Ana", "Marko", "Petra", "Luka", "Maja", "Tomislav", "Sara", "David", "Karla",
                // Novih 20
                "Mateo", "Lena", "Lovro", "Ella", "Leo", "Nina", "Jakov", "Tea", "Noa", "Emma",
                "Frane", "Lana", "Viktor", "Lucia", "Bruno", "Dorotea", "Alex", "Klara", "Niko", "Mila"
        };
        return imena[index];
    }

    private static String getPrezimeStudenta(int index) {
        String[] prezimena = {
                // Originalnih 10
                "Horvat", "Kovačević", "Babić", "Novak", "Jurić", "Pavić", "Marić", "Knežević", "Božić", "Varga",
                // Novih 20
                "Zupan", "Kralj", "Simić", "Milaković", "Tomić", "Grgić", "Vasić", "Sarić", "Perić", "Antonić",
                "Marjanović", "Kos", "Miletić", "Stanković", "Matić", "Petrović", "Lončar", "Barišić", "Popović", "Simunović"
        };
        return prezimena[index];
    }

    private static String getImeProfesora(int index) {
        String[] imena = {
                // Originalnih 5
                "Miljenko", "Dragan", "Ivana", "Ante", "Marina",
                // Novih 5
                "Zlatan", "Sanja", "Boris", "Dunja", "Igor"
        };
        return imena[index];
    }

    private static String getPrezimeProfesora(int index) {
        String[] prezimena = {
                // Originalnih 5
                "Kovač", "Petrović", "Matić", "Horvat", "Kolar",
                // Novih 5
                "Ivković", "Milutinović", "Novak", "Simić", "Mandić"
        };
        return prezimena[index];
    }

    private static String escapeSQL(String text) {
        if (text == null) return "";
        return text.replace("'", "''");
    }
}