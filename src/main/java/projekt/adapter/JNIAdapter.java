package projekt.adapter;

public class JNIAdapter {
    static {
        System.loadLibrary("StudentskiSustavLib");
    }

    public native boolean validirajEmail(String email);

    public native boolean validirajLozinku(String lozinka);

    public native boolean validirajJMBAG(String jmbag);

    public native String ucitajSvePrijevode();

    public native String otvoriDijalogOdabira();

    public native String otvoriDijalogSpremanja(String predlozenoIme);
}
