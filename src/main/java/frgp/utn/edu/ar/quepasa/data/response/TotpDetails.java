package frgp.utn.edu.ar.quepasa.data.response;

public class TotpDetails {

    private boolean enabled;
    private byte[] qr;
    private String url;

    public TotpDetails() {}
    public TotpDetails(boolean enabled, byte[] qr, String url) {
        this.enabled = enabled;
        this.qr = qr;
        this.url = url;
    }

    public boolean isEnabled() { return this.enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public byte[] getQr() { return this.qr; }
    public void setQr(byte[] qr) { this.qr = qr; }

    public String getUrl() { return this.url; }
    public void setUrl(String url) { this.url = url; }

}
