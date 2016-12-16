package me.passtheheadphones.torrentgroup;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import api.soup.MySoup;
import api.torrents.torrents.TorrentGroup;
import me.passtheheadphones.R;
import me.passtheheadphones.announcements.AnnouncementsActivity;
import me.passtheheadphones.artist.ArtistActivity;
import me.passtheheadphones.barcode.BarcodeActivity;
import me.passtheheadphones.bookmarks.BookmarksActivity;
import me.passtheheadphones.callbacks.LoadingListener;
import me.passtheheadphones.callbacks.ViewArtistCallbacks;
import me.passtheheadphones.callbacks.ViewTorrentCallbacks;
import me.passtheheadphones.callbacks.ViewUserCallbacks;
import me.passtheheadphones.forums.ForumActivity;
import me.passtheheadphones.inbox.InboxActivity;
import me.passtheheadphones.login.LoggedInActivity;
import me.passtheheadphones.notifications.NotificationsActivity;
import me.passtheheadphones.profile.ProfileActivity;
import me.passtheheadphones.search.SearchActivity;
import me.passtheheadphones.settings.SettingsActivity;
import me.passtheheadphones.subscriptions.SubscriptionsActivity;
import me.passtheheadphones.top10.Top10Activity;
import me.passtheheadphones.torrentgroup.group.TorrentGroupFragment;
import me.passtheheadphones.torrentgroup.torrent.TorrentsFragment;

/**
 * View information about a torrent group and the torrents in it. Must pass in the intent at least one
 * of group id or torrent id within the group to view
 */
public class TorrentGroupActivity extends LoggedInActivity
		implements ViewArtistCallbacks, ViewTorrentCallbacks, ViewUserCallbacks, DownloadDialog.DownloadDialogListener,
		LoaderManager.LoaderCallbacks<TorrentGroup> {
	/**
	 * Param to pass the torrent group id to be shown
	 */
	public static final String GROUP_ID = "me.passtheheadphones.GROUP_ID",
			TORRENT_ID = "me.passtheheadphones.TORRENT_ID";
	/**
	 * For use in viewTorrent to indicate that the group of the torrent is the currently open one
	 */
	public static final int CURRENT_GROUP = -1;
	/**
	 * The torrent group and comments being viewed the various view fragments get their data from
	 * the activity using the torrent group callbacks
	 */
	private int groupId, torrentId;
	private LoadingListener<TorrentGroup> loadingListener;
	/**
	 * Patter to match group ids in urls
	 */
	private static final Pattern groupIdPattern = Pattern.compile(".*id=(\\d+).*"),
			torrentIdPattern = Pattern.compile(".*torrentid=(\\d+).*");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();
		setTitle(getTitle());

		//Check if our saved state matches the group we want to view
		Intent intent = getIntent();
		groupId = intent.getIntExtra(GROUP_ID, -1);
		torrentId = intent.getIntExtra(TORRENT_ID, -1);
		FragmentManager manager = getSupportFragmentManager();
		if (savedInstanceState != null) {
			Fragment f = manager.findFragmentById(R.id.container);
			loadingListener = (LoadingListener) f;
			Bundle args = new Bundle();
			args.putInt(GROUP_ID, groupId);
			args.putInt(TORRENT_ID, torrentId);
			getSupportLoaderManager().initLoader(0, args, this);
		} else {
			//If we're opening a group url parse out the group id
			if (intent.getScheme() != null && intent.getDataString() != null && intent.getDataString().contains("passtheheadphones.me")) {
				Matcher m = torrentIdPattern.matcher(intent.getDataString());
				if (m.find()) {
					torrentId = Integer.parseInt(m.group(1));
				}
				//If no torrent id we're just linking to a normal torrent
				else {
					m = groupIdPattern.matcher(intent.getDataString());
					if (m.find()) {
						groupId = Integer.parseInt(m.group(1));
					}
				}
				//If no id at all see if it's a search link maybe?
			}
			//We always want the group fragment to be accessible, in the case of viewing a specific torrent
			//we push it on the back stack so we can go back to it
			Fragment f = TorrentGroupFragment.newInstance(groupId);
			manager.beginTransaction().add(R.id.container, f).commit();
			if (torrentId != -1) {
				f = TorrentsFragment.newInstance(torrentId);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, f)
						.addToBackStack(null)
						.commit();
			}
			loadingListener = (LoadingListener) f;
		}
	}

	@Override
	public void onLoggedIn() {
		Bundle args = new Bundle();
		args.putInt(GROUP_ID, groupId);
		args.putInt(TORRENT_ID, torrentId);
		getSupportLoaderManager().initLoader(0, args, this);
	}

	@Override
	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStackImmediate();
			//The only fragment we go back to is the MasterFragment
			loadingListener = (LoadingListener) fm.findFragmentById(R.id.container);
			Bundle args = new Bundle();
			args.putInt(GROUP_ID, groupId);
			args.putInt(TORRENT_ID, torrentId);
			getSupportLoaderManager().initLoader(0, args, this);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public Loader<TorrentGroup> onCreateLoader(int id, Bundle args) {
		setProgressBarIndeterminate(true);
		setProgressBarIndeterminateVisibility(true);
		return new TorrentGroupAsyncLoader(this, args);
	}

	@Override
	public void onLoadFinished(Loader<TorrentGroup> loader, TorrentGroup data) {
		setProgressBarIndeterminateVisibility(false);
		if (data == null || !data.getStatus()) {
			Toast.makeText(this, "Could not load torrent group", Toast.LENGTH_LONG).show();
		} else {
			groupId = data.getId();
			loadingListener.onLoadingComplete(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<TorrentGroup> loader) {
	}

	@Override
	public void viewArtist(int id) {
		Intent intent = new Intent(this, ArtistActivity.class);
		intent.putExtra(ArtistActivity.ARTIST_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewUser(int id) {
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra(ProfileActivity.USER_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewTorrentGroup(int id) {
	}

	@Override
	public void viewTorrent(int group, int torrent) {
		if (group == CURRENT_GROUP) {
			TorrentsFragment f = TorrentsFragment.newInstance(torrent);
			loadingListener = f;
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, f)
					.addToBackStack(null)
					.commit();
			Bundle args = new Bundle();
			args.putInt(GROUP_ID, groupId);
			args.putInt(TORRENT_ID, torrentId);
			getSupportLoaderManager().initLoader(0, args, this);
		}
	}

	@Override
	public void sendToPywa(int torrentId) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String host = preferences.getString(getString(R.string.key_pref_pywhat_host), "");
		String port = preferences.getString(getString(R.string.key_pref_pywhat_port), "");
		String pass = preferences.getString(getString(R.string.key_pref_pywhat_password), "");
		if (host.isEmpty() || port.isEmpty() || pass.isEmpty()) {
			Toast.makeText(getApplicationContext(), "Please fill out your PyWA server information",
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		} else {
			new SendToPyWA().execute(host, port, pass, Integer.toString(torrentId));
		}
	}

	@Override
	public void downloadToPhone(int torrent, String link, String title) {
		//Register our listener for downloading torrents to the phone
		getApplicationContext().registerReceiver(new DownloadCompleteReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link));
		request.setTitle(title);
		dm.enqueue(request);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		if (navDrawer == null) {
			return;
		}
		//Pass an argument to the activity telling it which to show?
		String selection = navDrawer.getItem(position);
		if (selection.equalsIgnoreCase(getString(R.string.announcements))) {
			//Launch AnnouncementsActivity viewing announcements
			//For now both just return to the announcements view
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.ANNOUNCEMENTS);
			startActivity(intent);
		} else if (selection.equalsIgnoreCase(getString(R.string.blog))) {
			//Launch AnnouncementsActivity viewing blog posts
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.BLOGS);
			startActivity(intent);
		} else if (selection.equalsIgnoreCase(getString(R.string.profile))) {
			//Launch profile view activity
			Intent intent = new Intent(this, ProfileActivity.class);
			intent.putExtra(ProfileActivity.USER_ID, MySoup.getUserId());
			startActivity(intent);
		} else if (selection.equalsIgnoreCase(getString(R.string.bookmarks))) {
			Intent intent = new Intent(this, BookmarksActivity.class);
			startActivity(intent);
		} else if (selection.contains(getString(R.string.messages))) {
			Intent intent = new Intent(this, InboxActivity.class);
			startActivity(intent);
		} else if (selection.contains(getString(R.string.notifications))) {
			Intent intent = new Intent(this, NotificationsActivity.class);
			startActivity(intent);
		} else if (selection.contains(getString(R.string.subscriptions))) {
			Intent intent = new Intent(this, SubscriptionsActivity.class);
			startActivity(intent);
		} else if (selection.equalsIgnoreCase(getString(R.string.forums))) {
			Intent intent = new Intent(this, ForumActivity.class);
			startActivity(intent);
		} else if (selection.equalsIgnoreCase(getString(R.string.top10))) {
			Intent intent = new Intent(this, Top10Activity.class);
			startActivity(intent);
		} else if (selection.equalsIgnoreCase(getString(R.string.torrents))) {
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.TORRENT);
			startActivity(intent);
		} else if (selection.equalsIgnoreCase(getString(R.string.artists))) {
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.ARTIST);
			startActivity(intent);
		} else if (selection.equalsIgnoreCase(getString(R.string.requests))) {
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.REQUEST);
			startActivity(intent);
		} else if (selection.equalsIgnoreCase(getString(R.string.users))) {
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.USER);
			startActivity(intent);
		} else if (selection.equalsIgnoreCase(getString(R.string.barcode_lookup))) {
			Intent intent = new Intent(this, BarcodeActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * Async task to instruct the user's PyWA server to download some torrent from the site
	 * params should be: 0: host, 1: port, 2: PyWA password, 3: torrent id
	 */
	private class SendToPyWA extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			String url = params[0] + ":" + params[1] + "/dl.pywa?pass=" + params[2]
					+ "&site=passtheheadphones&id=" + params[3];
			try {
				String result = MySoup.scrapeOther(url);
				if (result.contains("success")) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status) {
				Toast.makeText(TorrentGroupActivity.this, "Torrent sent", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(TorrentGroupActivity.this, "Failed to send torrent, check PyWA settings",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
