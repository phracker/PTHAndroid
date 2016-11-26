package me.passtheheadphones.request;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import api.requests.Request;
import api.soup.MySoup;
import me.passtheheadphones.R;
import me.passtheheadphones.announcements.AnnouncementsActivity;
import me.passtheheadphones.artist.ArtistActivity;
import me.passtheheadphones.barcode.BarcodeActivity;
import me.passtheheadphones.bookmarks.BookmarksActivity;
import me.passtheheadphones.callbacks.ViewArtistCallbacks;
import me.passtheheadphones.callbacks.ViewTorrentCallbacks;
import me.passtheheadphones.callbacks.ViewUserCallbacks;
import me.passtheheadphones.forums.ForumActivity;
import me.passtheheadphones.inbox.InboxActivity;
import me.passtheheadphones.login.LoggedInActivity;
import me.passtheheadphones.notifications.NotificationsActivity;
import me.passtheheadphones.profile.ProfileActivity;
import me.passtheheadphones.search.SearchActivity;
import me.passtheheadphones.subscriptions.SubscriptionsActivity;
import me.passtheheadphones.top10.Top10Activity;
import me.passtheheadphones.torrentgroup.TorrentGroupActivity;

/**
 * View information about a request
 */
public class RequestActivity extends LoggedInActivity
	implements ViewArtistCallbacks, ViewUserCallbacks, ViewTorrentCallbacks, VoteDialog.VoteDialogListener {
	/**
	 * Param to pass the request id to be shown
	 */
	public final static String REQUEST_ID = "what.whatandroid.REQUEST_ID";
	private RequestFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();
		setTitle(getTitle());

		FragmentManager manager = getSupportFragmentManager();
		int intentId = getIntent().getIntExtra(REQUEST_ID, 1);
		if (savedInstanceState != null){
			fragment = (RequestFragment)manager.findFragmentById(R.id.container);
		}
		else {
			fragment = RequestFragment.newInstance(intentId);
			manager.beginTransaction().add(R.id.container, fragment).commit();
		}
	}

	@Override
	public void onLoggedIn(){
		fragment.onLoggedIn();
	}

	@Override
	public void viewArtist(int id){
		Intent intent = new Intent(this, ArtistActivity.class);
		intent.putExtra(ArtistActivity.ARTIST_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewUser(int id){
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra(ProfileActivity.USER_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewTorrentGroup(int id){
		Intent intent = new Intent(this, TorrentGroupActivity.class);
		intent.putExtra(TorrentGroupActivity.GROUP_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewTorrent(int group, int torrent){
		Intent intent = new Intent(this, TorrentGroupActivity.class);
		intent.putExtra(TorrentGroupActivity.GROUP_ID, group);
		intent.putExtra(TorrentGroupActivity.TORRENT_ID, torrent);
		startActivity(intent);
	}

	@Override
	public void addBounty(int request, long amt){
		if (request != -1){
			new AddBountyTask().execute(request, amt);
		}
		else {
			Toast.makeText(this, "Invalid request id", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(int position){
		if (navDrawer == null){
			return;
		}
		String selection = navDrawer.getItem(position);
		if (selection.equalsIgnoreCase(getString(R.string.announcements))){
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.ANNOUNCEMENTS);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.profile))){
			Intent intent = new Intent(this, ProfileActivity.class);
			intent.putExtra(ProfileActivity.USER_ID, MySoup.getUserId());
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.bookmarks))){
			Intent intent = new Intent(this, BookmarksActivity.class);
			startActivity(intent);
		}
		else if (selection.contains(getString(R.string.messages))){
			Intent intent = new Intent(this, InboxActivity.class);
			startActivity(intent);
		}
		else if (selection.contains(getString(R.string.notifications))){
			Intent intent = new Intent(this, NotificationsActivity.class);
			startActivity(intent);
		}
		else if (selection.contains(getString(R.string.subscriptions))){
			Intent intent = new Intent(this, SubscriptionsActivity.class);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.forums))){
			Intent intent = new Intent(this, ForumActivity.class);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.top10))){
			Intent intent = new Intent(this, Top10Activity.class);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.blog))){
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.BLOGS);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.torrents))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.TORRENT);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.artists))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.ARTIST);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.requests))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.REQUEST);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.users))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.USER);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.barcode_lookup))){
			Intent intent = new Intent(this, BarcodeActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * Add bounty to some request. params should be { requestId, voteAmount }
	 */
	private class AddBountyTask extends AsyncTask<Number, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Number... params){
			int id = params[0].intValue();
			long amt = params[1].longValue();
			return Request.addBounty(id, amt);
		}

		@Override
		protected void onPreExecute(){
			setProgressBarIndeterminate(true);
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected void onPostExecute(Boolean status){
			setProgressBarIndeterminate(false);
			setProgressBarIndeterminateVisibility(false);
			if (status){
				Toast.makeText(RequestActivity.this, "Bounty added", Toast.LENGTH_LONG).show();
				fragment.refresh();
			}
			else {
				Toast.makeText(RequestActivity.this, "Could not add bounty", Toast.LENGTH_LONG).show();
			}
		}
	}
}
