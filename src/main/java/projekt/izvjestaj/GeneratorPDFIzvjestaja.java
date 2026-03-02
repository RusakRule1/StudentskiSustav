package projekt.izvjestaj;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GeneratorPDFIzvjestaja {

    static void main(String[] args) {
        if (args.length < 2) {
            System.exit(IzvjestajPodaci.GRESKA_ARGUMENTI);
        }

        String jsonPutanja = args[0];
        String pdfPutanja = args[1];

        String json;
        try {
            json = Files.readString(Path.of(jsonPutanja));
        } catch (IOException e) {
            System.exit(IzvjestajPodaci.GRESKA_CITANJE);
            return;
        }

        IzvjestajPodaci podaci;
        try {
            podaci = new Gson().fromJson(json, IzvjestajPodaci.class);
            if (podaci == null || podaci.getPredmet() == null
                    || podaci.getStudenti() == null || podaci.getLabele() == null) {
                System.exit(IzvjestajPodaci.GRESKA_PARSIRANJE);
                return;
            }
        } catch (JsonSyntaxException e) {
            System.exit(IzvjestajPodaci.GRESKA_PARSIRANJE);
            return;
        }

        try {
            generirajPDF(podaci, pdfPutanja);
            System.exit(IzvjestajPodaci.USPJEH);
        } catch (Exception e) {
            System.exit(IzvjestajPodaci.GRESKA_GENERIRANJE);
        }
    }

    private static void generirajPDF(IzvjestajPodaci podaci, String pdfPutanja) throws IOException {
        PdfWriter writer = new PdfWriter(pdfPutanja);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        PdfFont font = ucitajFont("C:/Windows/Fonts/arial.ttf");
        PdfFont boldFont = ucitajFont("C:/Windows/Fonts/arialbd.ttf");

        IzvjestajPodaci.PredmetPodaci predmet = podaci.getPredmet();
        IzvjestajPodaci.Labele l = podaci.getLabele();

        document.add(new Paragraph(l.getNaslov())
                .setFont(boldFont).setFontSize(18));

        document.add(new Paragraph(predmet.getNaziv())
                .setFont(boldFont).setFontSize(14));

        document.add(new Paragraph(l.getSifra() + ": " + predmet.getSifra() + "   |   " + l.getEcts() + ": " + predmet.getEctsBodovi())
                .setFont(font).setFontSize(11));

        document.add(new Paragraph(l.getSemestar() + ": " + predmet.getSemestar() + "   |   " + l.getGodinaIzvodenja() + ": " + predmet.getGodinaIzvodenja())
                .setFont(font).setFontSize(11));

        if (predmet.getProfesor() != null && !predmet.getProfesor().isBlank()) {
            document.add(new Paragraph(l.getProfesor() + ": " + predmet.getProfesor()).setFont(font).setFontSize(11));
        }

        document.add(new Paragraph("\n" + l.getUpisaniStudenti() + " (" + podaci.getStudenti().size() + "):")
                .setFont(boldFont).setFontSize(13));

        Table table = new Table(new float[]{3, 3, 2, 1.5f});
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(new Cell().add(new Paragraph(l.getKolonaIme()).setFont(boldFont)));
        table.addHeaderCell(new Cell().add(new Paragraph(l.getKolonaPrezime()).setFont(boldFont)));
        table.addHeaderCell(new Cell().add(new Paragraph(l.getKolonaJmbag()).setFont(boldFont)));
        table.addHeaderCell(new Cell().add(new Paragraph(l.getKolonaGodina()).setFont(boldFont)));

        for (IzvjestajPodaci.StudentPodaci student : podaci.getStudenti()) {
            table.addCell(new Cell().add(new Paragraph(student.getIme()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(student.getPrezime()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(student.getJmbag()).setFont(font)));
            table.addCell(new Cell().add(
                    new Paragraph(String.valueOf(student.getGodinaStudija())).setFont(font)));
        }

        document.add(table);
        document.close();
    }

    private static PdfFont ucitajFont(String putanja) {
        try {
            return PdfFontFactory.createFont(putanja, PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        } catch (IOException e) {
            try {
                return PdfFontFactory.createFont();
            } catch (IOException ex) {
                throw new RuntimeException("Ne mogu učitati font", ex);
            }
        }
    }
}
