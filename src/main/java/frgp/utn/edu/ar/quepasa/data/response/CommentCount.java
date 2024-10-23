package frgp.utn.edu.ar.quepasa.data.response;

import java.sql.Timestamp;

public class CommentCount {

    private int count;
    private Timestamp lastUpdated;

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
