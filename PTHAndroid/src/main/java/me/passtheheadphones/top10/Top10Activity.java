package me.passtheheadphones.top10;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import api.soup.MySoup;
import me.passtheheadphones.R;
import me.passtheheadphones.announcements.AnnouncementsActivity;
import me.passtheheadphones.barcode.BarcodeActivity;
import me.passtheheadphones.bookmarks.BookmarksActivity;
import me.passtheheadphones.callbacks.ViewTorrentCallbacks;
import me.passtheheadphones.forums.ForumActivity;
import me.passtheheadphones.inbox.InboxActivity;
import me.passtheheadphones.login.LoggedInActivity;
import me.passtheheadphones.notifications.NotificationsActivity;
import me.passtheheadphones.profile.ProfileActivity;
import me.passtheheadphones.search.SearchActivity;
import me.passtheheadphones.subscriptions.SubscriptionsActivity;
import me.passtheheadphones.torrentgroup.TorrentGroupActivity;

/**
 * Activity for displaying the user's top 10 torrents
 */
public class Top10Activity extends LoggedInActivity implements ViewTorrentCallbacks {
	private Top10Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();
		setTitle(getTitle());

		//If we're coming back from an orientation change or such, recover the fragment
		FragmentManager fm = getSupportFragmentManager();
		if (savedInstanceState != null){
			fragment = (Top10Fragment)fm.findFragmentById(R.id.container);
		}
		else {
			fragment = new Top10Fragment();
			fm.beginTransaction().add(R.id.container, fragment).commit();
		}
	}

	@Override
	public void onLoggedIn(){
		fragment.onLoggedIn();
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
}
