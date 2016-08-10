package pt.deti.cavexh.DB;

/**
 * Created by tiago on 24/03/16.
 */
public class Author {

    /**
     * Author name.
     */
    private String name;

    /**
     * Author short biography.
     */
    private String bio;

    /**
     * Use in app.
     */
    private String useInApp;

    /**
     * Empty constructor (needed for retrofit).
     */
    public Author() {

    }

    /**
     * Author name getter.
     * @return the author name.
     */
    public String getName() {
        return name;
    }

    /**
     * Author bio getter.
     * @return the author biography.
     */
    public String getBio() {
        return bio;
    }

    public String getUseInApp() {
        return useInApp;
    }

    public void setUseInApp(String useInApp) {
        this.useInApp = useInApp;
    }
}
