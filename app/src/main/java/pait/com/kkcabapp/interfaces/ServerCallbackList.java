package pait.com.kkcabapp.interfaces;

public interface ServerCallbackList<T> {
    public void onSuccess(T result);
    public void onFailure(T result);
}

