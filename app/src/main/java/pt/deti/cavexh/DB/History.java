package pt.deti.cavexh.DB;

import java.io.Serializable;

/**
 * Created by tiago on 24/03/16.
 */
public class History implements Serializable{

    private static final long serialVersionUID = 7863262235394607247L;

    /**
     * Image associated with the history page.
     */
    private String imageUrl;

    /**
     * Text presented in the history page.
     */
    private String text;

    /**
     * Empty constructor (needed for retrofit).
     */
    public History() {

    }

    /**
     * ImageUrl getter.
     * @return the image URL.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Text getter.
     * @return the history text.
     */
    public String getText() {
        return text;
    }
}
