package pl.itiner.grave;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;

public final class ProgressFragment extends SherlockDialogFragment {

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setIndeterminate(true);
		dialog.setTitle(R.string.downloading_data);
		dialog.setCancelable(true);
		return dialog;
	}
}