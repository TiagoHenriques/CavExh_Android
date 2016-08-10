package pt.deti.cavexh.DB;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tiago on 18/02/16.
 */
public class ListItem implements Serializable{

    private static final long serialVersionUID = 7863262235394607247L;

    /**
     * Id of the item.
     */
    private String id;

    /**
     * Title of the item.
     */
    private String title;

    /**
     * Description of the item.
     */
    private String description;

    /**
     * Image URL of the item.
     */
    private String imageUrl;

    /**
     * Other Images of cavaquinho.
     */
    private List<Image> otherImages;

    /**
     *
     */
    private String useInApp;

    /**
     * Empty constructor (needed for retrofit).
     */
    public ListItem() {

    }

    /**
     * Title getter.
     * @return the title of the item.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Descritption  getter.
     * @return the description of the item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * ImageURL getter.
     * @return the image URL of the item.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Id getter.
     * @return the id of the item.
     */
    public String getId() {
        return id;
    }

    /**
     * OtherImages getter.
     * @return other images of that cavaquinho.
     */
    public List<Image> getOtherImages() {
        return otherImages;
    }

    /**
     * UseInApp getter.
     * @return useInApp.
     */
    public String getUseInApp() {
        return useInApp;
    }

    /**
     * useInApp setter.
     * @param useInApp useInApp.
     */
    public void setUseInApp(String useInApp) {
        this.useInApp = useInApp;
    }
}
