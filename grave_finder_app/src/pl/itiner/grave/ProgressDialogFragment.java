package pl.itiner.grave;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.KeyEvent;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class ProgressDialogFragment extends SherlockDialogFragment {
	private SearchActivity activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof SearchActivity) {
			this.activity = (SearchActivity) activity;
		} else {
			throw new IllegalArgumentException("Activity is not instance of "
					+ SearchActivity.class.getSimpleName());
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new android.app.ProgressDialog(getActivity());
		dialog.setIndeterminate(true);
		dialog.setOnKeyListener(listener);
		dialog.setTitle(R.string.searching);
		return dialog;
	}

	private OnKeyListener listener = new OnKeyListener() {

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				activity.cancelSearch();
				dismiss();
			}
			return false;
		}
	};
}
