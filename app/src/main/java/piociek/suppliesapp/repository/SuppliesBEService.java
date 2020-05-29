package piociek.suppliesapp.repository;

import piociek.suppliesapp.domain.Item;
import piociek.suppliesapp.domain.LocationDetails;
import piociek.suppliesapp.domain.SuppliesBEResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SuppliesBEService {

    @GET("getAllItems")
    Call<SuppliesBEResponse> getAllItems();

    @POST("saveItem")
    Call<SuppliesBEResponse> saveItem(@Body Item item);

    @POST("saveLocationDetails")
    Call<SuppliesBEResponse> saveLocationDetails(@Query(value = "itemId") String itemId,
                                                 @Body LocationDetails locationDetails);

    @DELETE("deleteLocationDetails")
    Call<SuppliesBEResponse> deleteLocationDetails(
            @Query(value = "itemId") String itemId,
            @Query(value = "locationDetailsId") String locationDetailsId);

}
