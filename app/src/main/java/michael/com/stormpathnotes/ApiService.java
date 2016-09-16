package michael.com.stormpathnotes;

import java.util.List;

import michael.com.stormpathnotes.util.Constants;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Mikhail on 9/13/16.
 */
public class ApiService {

    public interface ApiCall {
        @GET("/contacts")
        Call<List<Contacts>>getContacts();

        @FormUrlEncoded
        @POST("/contacts")
        Call<Contacts> postContacts(
                @Field("firstName") String firstName,
                @Field("lastName") String lastName);

        @POST("/contacts")
        Call<Contacts> postObject(@Body Contacts body);
    }



    public static ApiCall create() {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.ApiCall.class);
    }
}
