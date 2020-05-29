package piociek.suppliesapp.async;

import android.os.AsyncTask;

import java.util.List;

import piociek.suppliesapp.constants.Properties;
import piociek.suppliesapp.domain.Item;
import piociek.suppliesapp.repository.ItemRepository;
import piociek.suppliesapp.repository.SuppliesBEServiceRepository;
import piociek.suppliesapp.util.AppPropertiesProvider;

public class GetAllItemsAsync extends AsyncTask<Void, Void, Boolean> {

    private AsyncCompletionAware asyncCompletionAware;
    private ItemRepository itemRepository;
    private int maxRetryCount;

    public GetAllItemsAsync(AsyncCompletionAware asyncCompletionAware, ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        this.asyncCompletionAware = asyncCompletionAware;
        this.maxRetryCount = AppPropertiesProvider.getPropertyAsInt(itemRepository.getContext(), Properties.SERVICE_RETRY_COUNT);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        int retryCount = 0;
        List<Item> items;
        do {
            items = SuppliesBEServiceRepository
                    .getInstance(itemRepository.getContext())
                    .getAllItems(itemRepository);
            if (items != null) return true;
        } while (retryCount++ < maxRetryCount);
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            asyncCompletionAware.onSuccess();
        } else {
            asyncCompletionAware.onFailure();
        }
    }
}
