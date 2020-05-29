package piociek.suppliesapp.async;

public interface AsyncCompletionAware {

    void onSuccess();

    void onFailure();

}
