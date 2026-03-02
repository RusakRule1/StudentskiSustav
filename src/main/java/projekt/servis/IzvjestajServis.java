package projekt.servis;

import com.google.gson.Gson;
import projekt.izvjestaj.IzvjestajPodaci;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class IzvjestajServis {

    public static int generirajIzvjestaj(IzvjestajPodaci podaci, String pdfPutanja) {
        File tempDatoteka = null;
        try {
            tempDatoteka = File.createTempFile("izvjestaj_", ".json");
            tempDatoteka.deleteOnExit();

            try (FileWriter writer = new FileWriter(tempDatoteka)) {
                new Gson().toJson(podaci, writer);
            }

            String javaPutanja = ProcessHandle.current().info().command().orElse(System.getProperty("java.home") + "/bin/java");

            String classpath = System.getProperty("java.class.path");

            ProcessBuilder pb = new ProcessBuilder(
                    javaPutanja,
                    "-cp", classpath,
                    "projekt.izvjestaj.GeneratorPDFIzvjestaja",
                    tempDatoteka.getAbsolutePath(),
                    pdfPutanja
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();

            boolean zavrsen = process.waitFor(30, TimeUnit.SECONDS);
            if (!zavrsen) {
                process.destroyForcibly();
                return IzvjestajPodaci.GRESKA_GENERIRANJE;
            }

            return process.exitValue();

        } catch (IOException e) {
            return IzvjestajPodaci.GRESKA_GENERIRANJE;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return IzvjestajPodaci.GRESKA_GENERIRANJE;
        }
    }
}
