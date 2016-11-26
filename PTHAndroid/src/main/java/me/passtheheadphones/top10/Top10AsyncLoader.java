package me.passtheheadphones.top10;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;

import api.top.TopTorrents;
import me.passtheheadphones.R;

/**
 * Use to load the categories of top 10 torrents
 */
public class Top10AsyncLoader extends AsyncTaskLoader<TopTorrents> {
	private TopTorrents topTorrents;
	private int topTorrentsToLoad = 10;

	public Top10AsyncLoader(Context context){
		super(context);
		topTorrentsToLoad = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_top_torrents), "10"));
	}

	@Override
	public TopTorrents loadInBackground(){
		if (topTorrents == null){
			while (true){
				topTorrents = TopTorrents.top(topTorrentsToLoad);
				//If we get rate limited wait and retry. It's very unlikely the user has
				//used all 5 of our requests per 10s so don't wait the whole time
				if (topTorrents != null && !topTorrents.getStatus() && topTorrents.getError() != null
					&& topTorrents.getError().equalsIgnoreCase("rate limit exceeded")){
					try {
						Thread.sleep(3000);
					}
					catch (InterruptedException e){
						Thread.currentThread().interrupt();
					}
				}
				else {
					break;
				}
			}
		}
		return topTorrents;
	}

	@Override
	protected void onStartLoading(){
		if (topTorrents != null){
			deliverResult(topTorrents);
		}
		else {
			forceLoad();
		}
	}
}
