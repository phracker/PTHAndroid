package me.passtheheadphones.inbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;

import api.soup.MySoup;
import me.passtheheadphones.R;
import me.passtheheadphones.announcements.AnnouncementsActivity;
import me.passtheheadphones.barcode.BarcodeActivity;
import me.passtheheadphones.bookmarks.BookmarksActivity;
import me.passtheheadphones.callbacks.AddQuoteCallback;
import me.passtheheadphones.callbacks.OnLoggedInCallback;
import me.passtheheadphones.callbacks.ViewConversationCallbacks;
import me.passtheheadphones.callbacks.ViewUserCallbacks;
import me.passtheheadphones.forums.ForumActivity;
import me.passtheheadphones.inbox.conversation.ConversationFragment;
import me.passtheheadphones.login.LoggedInActivity;
import me.passtheheadphones.notifications.NotificationsActivity;
import me.passtheheadphones.profile.ProfileActivity;
import me.passtheheadphones.search.SearchActivity;
import me.passtheheadphones.subscriptions.SubscriptionsActivity;
import me.passtheheadphones.top10.Top10Activity;

/**
 * Activity for viewing the user's inbox and conversations
 */
public class InboxActivity extends LoggedInActivity implements ViewConversationCallbacks,
	ViewUserCallbacks, AddQuoteCallback, ConversationChangesPasser {

	private static final String CHANGES = "what.whatandroid.inboxactivity.CHANGES";

	/**
	 * Listener to alert when we've logged in
	 */
	private OnLoggedInCallback loginListener;

	/**
	 * Bundle containing information about any changes made to
	 * a conversation that was being viewed
	 */
	private Bundle conversationChanges;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setupNavDrawer();

		FragmentManager fm = getSupportFragmentManager();
		if (savedInstanceState != null){
			loginListener = (OnLoggedInCallback)fm.findFragmentById(R.id.container);
			conversationChanges = savedInstanceState.getBundle(CHANGES);
		}
		else {
			InboxFragment f = new InboxFragment();
			loginListener = f;
			fm.beginTransaction().add(R.id.container, f).commit();
		}
		//Since we're viewing the notifications unset the new notifications flag
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.edit()
			.putInt(getString(R.string.key_pref_new_messages), 0)
			.apply();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		if (hasChanges()){
			outState.putBundle(CHANGES, conversationChanges);
		}
	}

	@Override
	public void onBackPressed(){
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 0){
			fm.popBackStackImmediate();
			loginListener = (OnLoggedInCallback)fm.findFragmentById(R.id.container);
			//The conversation shows the subject in the title, so put back the activity title
			setTitle(getString(R.string.inbox));
		}
		else {
			super.onBackPressed();
		}
	}

	@Override
	public void onLoggedIn(){
		loginListener.onLoggedIn();
	}

	@Override
	public void viewUser(int id){
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra(ProfileActivity.USER_ID, id);
		startActivity(intent);
	}

	@Override
	public void viewConversation(int id){
		ConversationFragment f = ConversationFragment.newInstance(id);
		loginListener = f;
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, f)
			.addToBackStack(null)
			.commit();
	}

	@Override
	public void quote(String quote){
		//Notify the conversation fragment about the quote
		ConversationFragment f = (ConversationFragment)loginListener;
		if (f != null){
			f.quote(quote);
		}
	}

	@Override
	public void setChanges(Bundle changes){
		conversationChanges = changes;
	}

	@Override
	public Bundle getChanges(){
		return conversationChanges;
	}

	@Override
	public void consumeChanges(){
		conversationChanges = null;
	}

	@Override
	public boolean hasChanges(){
		return conversationChanges != null;
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
		else if (selection.equalsIgnoreCase(getString(R.string.bookmarks))){
			Intent intent = new Intent(this, BookmarksActivity.class);
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
		else if (selection.equalsIgnoreCase(getString(R.string.top10))){
			Intent intent = new Intent(this, Top10Activity.class);
			startActivity(intent);
		}
		else if (selection.equalsIgnoreCase(getString(R.string.torrents))){
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(SearchActivity.SEARCH, SearchActivity.TORRENT);
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
