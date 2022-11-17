package chatApp.Entities;

import javax.persistence.*;

@Entity
@Table(name = "guest")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(unique = true)
    private String nickName;
    private boolean muted;

    public Guest(String nickName) {
        this.nickName = nickName;
        this.muted = false;
    }

    public String getNickName() {
        return nickName;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "id=" + id +
                ", nickName='" + nickName + '\'' +
                ", muted=" + muted +
                '}';
    }
}
