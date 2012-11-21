package pl.itiner.fetch;

import java.io.IOException;
import java.util.List;

import pl.itiner.db.GraveFinderProvider;
import pl.itiner.grave.SearchActivity;
import pl.itiner.model.Departed;
import pl.itiner.model.DepartedFactory;
import android.app.IntentService;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public final class JsonFetchService extends IntentService {

	public static final String QUERY_PARAMS_BUNDLE = "QueryParamsBundle";
	public static final String MESSENGER_BUNDLE = "MESSENGER_BUNDLE";

	public JsonFetchService() {
		super("JsonFetchService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		QueryParams params = (QueryParams) intent.getExtras().get(
				QUERY_PARAMS_BUNDLE);
		try {
			final List<? extends Departed> results = new PoznanGeoJSONHandler(
					params, getApplicationContext()).executeQuery();
			getApplicationContext().getContentResolver().delete(
					GraveFinderProvider.createUri(params), null, null);
			for (Departed d : results) {
				getApplicationContext().getContentResolver().insert(
						GraveFinderProvider.CONTENT_URI,
						DepartedFactory.asContentValues(d));
			}
		} catch (IOException e) {
			Messenger messenger = intent.getParcelableExtra(MESSENGER_BUNDLE);
			if (null != messenger) {
				Message msg = Message.obtain();
				msg.what = SearchActivity.SearchActivityHandler.DOWNLOAD_FAILED;
				try {
					messenger.send(msg);
				} catch (RemoteException e1) {
					//Skip
				}
			}
		}
	}

}
