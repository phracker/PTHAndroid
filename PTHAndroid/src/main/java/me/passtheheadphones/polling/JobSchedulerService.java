package me.passtheheadphones.polling;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;

import org.json.JSONArray;

import java.io.IOException;

import api.notifications.Notifications;
import api.soup.MySoup;
import me.passtheheadphones.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by stu on 29/11/16.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
    private UpdateAppsAsyncTask updateTask = new UpdateAppsAsyncTask();
    private Notifications notifications;

    @Override
    public boolean onStartJob(JobParameters params) {
        // Note: this is preformed on the main thread.

        updateTask.execute(params);

        return true;
    }

    // Stopping jobs if our job requires change.

    @Override
    public boolean onStopJob(JobParameters params) {
        // Note: return true to reschedule this job.

        boolean shouldReschedule = updateTask.stopJob(params);

        return shouldReschedule;
    }

    private class UpdateAppsAsyncTask extends AsyncTask<JobParameters, Void, JobParameters[]> {

        @Override
        protected JobParameters[] doInBackground(JobParameters... params) {

            if (MySoup.isLoggedIn()) {
               api.index.Notifications notifications = MySoup.getIndex().getResponse().getNotifications();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (MySoup.isNotificationsEnabled()){
                    preferences.edit()
                            .putInt(getApplicationContext().getString(R.string.key_pref_num_notifications),
                                    notifications.getTorrentNotifications().intValue())
                            .apply();
                }
                preferences.edit()
                        .putBoolean(getApplicationContext().getString(R.string.key_pref_new_subscriptions),
                                notifications.hasNewSubscriptions())
                        .putInt(getApplicationContext().getString(R.string.key_pref_new_messages),
                                notifications.getMessages().intValue())
                        .apply();
            }

//            // Do updating and stopping logical here.
//            while (true) {
//                notifications = Notifications.page(1);
//                //If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
//                //requests per 10s so don't wait the whole time initially
//                if (notifications != null && !notifications.getStatus() && notifications.getError() != null
//                        && notifications.getError().equalsIgnoreCase("rate limit exceeded")) {
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                    }
//                } else {
//                    break;
//                }
//            }
            return params;
        }

        @Override
        protected void onPostExecute(JobParameters[] result) {
            for (JobParameters params : result) {
                if (!hasJobBeenStopped(params)) {
                    jobFinished(params, false);
                }
            }
        }

        private boolean hasJobBeenStopped(JobParameters params) {
            // Logic for checking stop.
            return false;
        }

        public boolean stopJob(JobParameters params) {
            // Logic for stopping a job. return true if job should be rescheduled.
            return false;
        }
    }
}