package projekt;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class GeneratorKljuceva {

    private static final Path PUTANJA = Paths.get(
            System.getProperty("user.home"),
            ".studentski-sustav",
            "podaci"
    );

    public static void main(String[] args) throws Exception {
        Files.createDirectories(PUTANJA);

        generirajAESKljuc();
        generirajRSAKljuceve();

        System.out.println("Ključevi uspješno generirani u: " + PUTANJA);
    }

    private static void generirajAESKljuc() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        SecretKey kljuc = generator.generateKey();
        String kljucBase64 = Base64.getEncoder().encodeToString(kljuc.getEncoded());
        Files.writeString(PUTANJA.resolve("aes_kljuc.key"), kljucBase64);
        System.out.println("AES ključ generiran.");
    }

    private static void generirajRSAKljuceve() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair par = generator.generateKeyPair();

        String javniBase64 = Base64.getEncoder().encodeToString(par.getPublic().getEncoded());
        String privatniBase64 = Base64.getEncoder().encodeToString(par.getPrivate().getEncoded());

        Files.writeString(PUTANJA.resolve("rsa_javni.key"), javniBase64);
        Files.writeString(PUTANJA.resolve("rsa_privatni.key"), privatniBase64);
        System.out.println("RSA ključevi generirani.");
    }
}
