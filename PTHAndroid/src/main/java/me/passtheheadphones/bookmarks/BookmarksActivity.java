package me.passtheheadphones.bookmarks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import api.soup.MySoup;
import me.passtheheadphones.R;
import me.passtheheadphones.announcements.AnnouncementsActivity;
import me.passtheheadphones.artist.ArtistActivity;
import me.passtheheadphones.barcode.BarcodeActivity;
import me.passtheheadphones.callbacks.ViewArtistCallbacks;
import me.passtheheadphones.callbacks.ViewTorrentCallbacks;
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
 * Activity for viewing the user's torrent and artist bookmarks
 */
public class BookmarksActivity extends LoggedInActivity implements ViewTorrentCallbacks, ViewArtistCallbacks {
	private BookmarksFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();
		setTitle(getTitle());

		FragmentManager fm = getSupportFragmentManager();
		if (savedInstanceState != null){
			fragment = (BookmarksFragment)fm.findFragmentById(R.id.container);
		}
		else {
			fragment = new BookmarksFragment();
			fm.beginTransaction().add(R.id.container, fragment).commit();
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
		//Pass an argument to the activity telling it which to show?
		String selection = navDrawer.getItem(position);
		if (selection.equalsIgnoreCase(getString(R.string.announcements))){
			//Launch AnnouncementsActivity viewing announcements
			//For now both just return to the announcements view
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.ANNOUNCEMENTS);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.blog))){
			//Launch AnnouncementsActivity viewing blog posts
			Intent intent = new Intent(this, AnnouncementsActivity.class);
			intent.putExtra(AnnouncementsActivity.SHOW, AnnouncementsActivity.BLOGS);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.profile))){
			//Launch profile view activity
			Intent intent = new Intent(this, ProfileActivity.class);
			intent.putExtra(ProfileActivity.USER_ID, MySoup.getUserId());
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
}
