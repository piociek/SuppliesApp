package piociek.suppliesapp.repository;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import piociek.suppliesapp.domain.Item;
import piociek.suppliesapp.domain.LocationDetails;
import piociek.suppliesapp.domain.SuppliesBEResponse;
import piociek.suppliesapp.util.AppPropertiesProvider;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static piociek.suppliesapp.constants.Properties.SERVICE_URL;

public class SuppliesBEServiceRepository {

    private static final String TAG = "ItemRepository";

    private static SuppliesBEServiceRepository instance;
    private final SuppliesBEService service;

    private SuppliesBEServiceRepository(String serviceUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serviceUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(SuppliesBEService.class);
    }

    public static SuppliesBEServiceRepository getInstance(final Context context) {
        if (instance == null) {
            instance = new SuppliesBEServiceRepository(
                    AppPropertiesProvider.getProperty(context, SERVICE_URL));
        }
        return instance;
    }

    public List<Item> getAllItems(ItemRepository itemRepository) {
        Call<SuppliesBEResponse> responseCall = service.getAllItems();
        Response<SuppliesBEResponse> response = processCall("getAllItems", responseCall);
        if (doesResponseContainItems(response)) {
            itemRepository.deleteAll();
            for (Item item : response.body().getItems()) {
                itemRepository.insert(item);
            }
            return response.body().getItems();
        }
        return null;
    }

    public void saveItem(ItemRepository itemRepository, Item item) {
        Call<SuppliesBEResponse> responseCall = service.saveItem(item);
        Response<SuppliesBEResponse> response = processCall("saveItem", responseCall);
        if (response.isSuccessful()) {
            itemRepository.insert(item);
        }
    }

    public void saveLocationDetails(ItemRepository itemRepository, String itemId, LocationDetails locationDetails) {
        Call<SuppliesBEResponse> responseCall = service.saveLocationDetails(itemId, locationDetails);
        Response<SuppliesBEResponse> response = processCall("saveLocationDetails", responseCall);
        if (doesResponseContainItem(response)) {
            itemRepository.insert(response.body().getItems().get(0));
        }
    }

    public void deleteLocationDetails(ItemRepository itemRepository, String itemId, String locationDetailsId) {
        Call<SuppliesBEResponse> responseCall = service.deleteLocationDetails(itemId, locationDetailsId);
        Response<SuppliesBEResponse> response = processCall("deleteLocationDetails", responseCall);
        if (doesResponseContainItem(response)) {
            itemRepository.insert(response.body().getItems().get(0));
        }
    }

    private Response<SuppliesBEResponse> processCall(String logId, Call<SuppliesBEResponse> responseCall) {
        try {
            Response<SuppliesBEResponse> response = responseCall.execute();
            if (response.isSuccessful()) {
                return response;
            } else {
                Log.d(TAG, String.format("%s: response not successful: %s", logId, response.code()));
            }
        } catch (IOException e) {
            Log.e(TAG, String.format("%s: error during call", logId), e);
        }
        return null;
    }

    private boolean doesResponseContainItems(Response<SuppliesBEResponse> response) {
        return response != null && response.body() != null && response.body().getItems() != null;
    }

    private boolean doesResponseContainItem(Response<SuppliesBEResponse> response) {
        return doesResponseContainItems(response) && response.body().getItems().size() == 1;
    }
}
