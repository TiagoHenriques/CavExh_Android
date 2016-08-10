package pt.deti.cavexh.DB;

import java.io.Serializable;

/**
 * Created by tiago on 29/03/16.
 */
public class Image implements Serializable {

    private static final long serialVersionUID = 7863262235394607247L;

    private String img;

    public Image() {

    }

    public String getImg() {
        return img;
    }
}
