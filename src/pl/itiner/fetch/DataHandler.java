package pl.itiner.fetch;

import android.content.Context;

public interface DataHandler<T> extends Runnable {
	void setResponseHandler(ResponseHandler<T> handler);
	DataHandler<T> clone(QueryParams params, Context ctx);
}
