/*
 * This file is part of the Lokalizator grobów project.
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation v3; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */

package pl.itiner.grave;

import pl.itiner.db.GraveFinderProvider;
import pl.itiner.db.NameHintProvider;
import pl.itiner.fetch.QueryParams;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SearchActivity extends SherlockFragmentActivity {

	private FragmentManager fragmentMgr;
	private ResultList listFragment;
	private GFormFragment formFragment;

	private boolean dualFragments = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		dualFragments = isDualFragments();
		fragmentMgr = getSupportFragmentManager();
		listFragment = (ResultList) fragmentMgr
				.findFragmentById(R.id.result_list_fragment);
		formFragment = (GFormFragment) fragmentMgr
				.findFragmentById(R.id.search_form_fragment);
		if (!dualFragments) {
			hideFragment(savedInstanceState);
		}
	}

	private void hideFragment(Bundle savedInstanceState) {
		int hideFragmentId = R.id.result_list_fragment;
		if (null != savedInstanceState) {
			hideFragmentId = savedInstanceState.getInt("HIDDEN_FRAGMENT_ID",
					hideFragmentId);
		}
		FragmentTransaction transaction = fragmentMgr.beginTransaction();
		transaction.hide(fragmentMgr.findFragmentById(hideFragmentId));
		transaction.commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(!dualFragments) {
			outState.putInt("HIDDEN_FRAGMENT_ID",
					formFragment.isHidden() ? R.id.search_form_fragment
							: R.id.result_list_fragment);
		}
		super.onSaveInstanceState(outState);
	}

	private boolean isDualFragments() {
		return ((LinearLayout) findViewById(R.id.content_fragment_placeholder))
				.getOrientation() == LinearLayout.HORIZONTAL;
	}

	public void search(QueryParams params) {
		if (!listFragment.isVisible()) {
			FragmentTransaction transaction = fragmentMgr.beginTransaction();
			transaction.hide(formFragment);
			transaction.show(listFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		listFragment.search(params);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			Intent intentAboutView = new Intent(this.getApplicationContext(),
					About.class);
			startActivity(intentAboutView);
			return true;
		case R.id.menu_clear_cache:
			getContentResolver().delete(GraveFinderProvider.CONTENT_URI, null,
					null);
			return true;
		case R.id.menu_clear_hints:
			getContentResolver().delete(NameHintProvider.CONTENT_URI, null,
					null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean isConnectionAvailable() {
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		return i != null && i.isConnected();
	}

	public void gotoSearchForm() {
		if (!formFragment.isVisible()) {
			onBackPressed();
		}
	}

}