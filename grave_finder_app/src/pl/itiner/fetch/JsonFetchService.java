package pl.itiner.fetch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.itiner.db.GraveFinderProvider;
import pl.itiner.grave.ResultList;
import pl.itiner.model.Departed;
import pl.itiner.model.DepartedFactory;
import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.google.gson.JsonParseException;

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
//			final List<? extends Departed> results = new PoznanGeoJSONHandler(
//					params, getApplicationContext()).executeQuery();
			final List<? extends Departed> results = new WroclawDataHandler(
					params).executeQuery();
			ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(
					results.size() + 1);
			final ContentProviderOperation deleteOp = ContentProviderOperation
					.newDelete(GraveFinderProvider.createUri(params)).build();
			operations.add(deleteOp);
			for (Departed d : results) {
				final ContentProviderOperation insertOp = ContentProviderOperation
						.newInsert(GraveFinderProvider.CONTENT_URI)
						.withValues(DepartedFactory.asContentValues(d)).build();
				operations.add(insertOp);
			}
			try {
				getContentResolver().applyBatch(
						GraveFinderProvider.SIMPLE_AUTHORITY, operations);
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			} catch (OperationApplicationException e) {
				throw new RuntimeException(e);
			}
			if (results.size() == 0) {
				sendMsg(intent, ResultList.SearchHandler.NO_ONLINE_RESULTS);
			}
		} catch (JsonParseException e) {
			sendMsg(intent, ResultList.SearchHandler.UNEXPECTED_SEVER_ANSWER);
		} catch (IOException e) {
			sendMsg(intent, ResultList.SearchHandler.DOWNLOAD_FAILED);
		}
	}

	private void sendMsg(Intent intent, int what) {
		Messenger messenger = intent.getParcelableExtra(MESSENGER_BUNDLE);
		if (null != messenger) {
			Message msg = Message.obtain();
			msg.what = what;
			try {
				messenger.send(msg);
			} catch (RemoteException e1) {
				// Skip
			}
		}
	}

}
