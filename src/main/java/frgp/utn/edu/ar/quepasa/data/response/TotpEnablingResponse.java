package frgp.utn.edu.ar.quepasa.data.response;

public class TotpEnablingResponse {

    private byte[] qr;
    private String url;

    public TotpEnablingResponse() {}
    public TotpEnablingResponse(byte[] qr, String url) {
        this.qr = qr;
        this.url = url;
    }

    public byte[] getQr() { return this.qr; }
    public void setQr(byte[] qr) { this.qr = qr; }

    public String getUrl() { return this.url; }
    public void setUrl(String url) { this.url = url; }

}
