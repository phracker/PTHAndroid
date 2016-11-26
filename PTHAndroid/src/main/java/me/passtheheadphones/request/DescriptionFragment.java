package me.passtheheadphones.request;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.passtheheadphones.R;
import me.passtheheadphones.callbacks.LoadingListener;
import me.passtheheadphones.comments.HTMLListTagHandler;
import me.passtheheadphones.imgloader.HtmlImageHider;

/**
 * Fragment for holding the description of a torrent (or request)
 */
public class DescriptionFragment extends Fragment implements LoadingListener<String> {
    private TextView descriptionView;
    private String description;

    public DescriptionFragment() {
        //Required empty ctor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        descriptionView = (TextView) view.findViewById(R.id.description);
        descriptionView.setMovementMethod(LinkMovementMethod.getInstance());
        if (description != null) {
            descriptionView.setText(Html.fromHtml(description, new HtmlImageHider(getActivity()), new HTMLListTagHandler()));
        }
        return view;
    }

    @Override
    public void onLoadingComplete(String data) {
        description = data;
        if (isAdded()) {
            descriptionView.setText(Html.fromHtml(description, new HtmlImageHider(getActivity()), new HTMLListTagHandler()));
        }
    }
}
