package pl.itiner.fetch;

public interface ResponseHandler<T> {
	void handleResponse(T data);
}
