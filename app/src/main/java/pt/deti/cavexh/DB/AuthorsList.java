package pt.deti.cavexh.DB;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tiago on 24/03/16.
 */
public class AuthorsList implements Serializable {

    private static final long serialVersionUID = 7863262235394607247L;

    /**
     * String for struture purpose.
     */
    public String authors;

    /**
     * List of authors.
     */
    public List<Author> list;

    /**
     * Empty constructor (needed for retrofit).
     */
    public AuthorsList() {

    }
}
