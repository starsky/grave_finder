package pl.itiner.grave;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class AlertDialogFragment extends SherlockDialogFragment {
	public static String TITLE_BUND = "TITLE_BUND";
	public static String MESSAGE_BUND = "MESSAGE_BUND";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		final Bundle bundle = getArguments();
		b.setTitle(bundle.getInt(TITLE_BUND));
		b.setMessage(bundle.getInt(MESSAGE_BUND));
		b.setPositiveButton(R.string.ok, null);
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
