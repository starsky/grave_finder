package pl.itiner.grave;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class AlertDialogFragment extends SherlockDialogFragment {
	public static String TITLE_BUND = "TITLE_BUND";
	public static String MESSAGE_BUND = "MESSAGE_BUND";

	private SearchActivity activity;

	private OnClickListener okClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			activity.gotoSearchForm();
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof SearchActivity)) {
			throw new IllegalArgumentException(
					"Activity should be of type SearchActivity.");
		} else {
			this.activity = (SearchActivity) activity;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		this.activity = null;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		final Bundle bundle = getArguments();
		b.setTitle(bundle.getInt(TITLE_BUND));
		b.setMessage(bundle.getInt(MESSAGE_BUND));
		b.setPositiveButton(R.string.ok, okClickListener);
		return b.create();
	}

	public static AlertDialogFragment create(int titleId, int msgId) {
		Bundle bundle = new Bundle();
		bundle.putInt(TITLE_BUND, titleId);
		bundle.putInt(MESSAGE_BUND, msgId);
		AlertDialogFragment fragment = new AlertDialogFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
}
