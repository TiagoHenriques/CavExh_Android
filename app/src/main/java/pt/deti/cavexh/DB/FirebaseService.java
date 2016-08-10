package pt.deti.cavexh.DB;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by tiago on 17/03/16.
 */
public interface FirebaseService {

    public static final String BASE_URL = "https://radiant-inferno-748.firebaseio.com";

    @GET ("/images_new.json")
    Call<Paintings> getPaintings();

    @GET ("/authors_new.json")
    Call<AuthorsList> getAuthorsList();

    @GET ("/history.json")
    Call<History> getHistory();

    @GET ("/images_pt_new.json")
    Call<Paintings> getPaintingsPortuguese();

    @GET ("/authors_pt_new.json")
    Call<AuthorsList> getAuthorsListPortuguese();

    @GET ("/history_pt.json")
    Call<History> getHistoryPortuguese();

}
