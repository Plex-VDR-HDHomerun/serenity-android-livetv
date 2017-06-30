/**
 * The MIT License (MIT)
 * Copyright (c) 2012 David Carver
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package us.nineworlds.serenity.ui.browser.tv.episodes;

import android.support.v7.widget.RecyclerView;

import us.nineworlds.plex.rest.model.impl.MediaContainer;
import us.nineworlds.serenity.R;
import us.nineworlds.serenity.core.model.VideoContentInfo;
import us.nineworlds.serenity.core.model.impl.EpisodeMediaContainer;
import us.nineworlds.serenity.jobs.EpisodesRetrievalJob;
import us.nineworlds.serenity.ui.adapters.AbstractPosterImageGalleryAdapter;
import us.nineworlds.serenity.ui.util.ImageUtils;
import us.nineworlds.serenity.volley.DefaultLoggingVolleyErrorListener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.birbit.android.jobqueue.JobManager;

import net.ganin.darv.DpadAwareRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

/**
 * Implementation of the Poster Image Gallery class for TV Shows.
 *
 * @author dcarver
 *
 */
public class EpisodePosterImageGalleryAdapter extends
        AbstractPosterImageGalleryAdapter {

    @Inject
    JobManager jobManager;

    @Inject
    EventBus eventBus;

    public EpisodePosterImageGalleryAdapter(Context c, String key) {
        super(c, key);
        eventBus.register(this);
    }

    @Override
    protected void fetchDataFromService() {
        retrieveEpisodes();
    }

    public void retrieveEpisodes() {
        EpisodesRetrievalJob episodesRetrievalJob = new EpisodesRetrievalJob(key);

        jobManager.addJobInBackground(episodesRetrievalJob);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poster_indicator_view, null);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoContentInfo pi = posterList.get(position);
        ImageView mpiv = (ImageView) holder.itemView
                .findViewById(R.id.posterImageView);
        mpiv.setBackgroundResource(R.drawable.gallery_item_background);
        mpiv.setScaleType(ImageView.ScaleType.FIT_XY);
        int width = ImageUtils.getDPI(300, context);
        int height = ImageUtils.getDPI(187, context);
        mpiv.setMaxHeight(height);
        mpiv.setMaxWidth(width);
        mpiv.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        holder.itemView.setLayoutParams(new DpadAwareRecyclerView.LayoutParams(width,
                height));

        serenityImageLoader.displayImage(pi.getImageURL(), mpiv);

        setWatchedStatus(holder.itemView, pi);
    }

    public class EpisodeViewHolder extends DpadAwareRecyclerView.ViewHolder {

        public EpisodeViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEpisodeResponse(MediaContainer response) {
        EpisodeMediaContainer episodes = new EpisodeMediaContainer(response);
        posterList = episodes.createVideos();
        notifyDataSetChanged();
        DpadAwareRecyclerView gallery = (DpadAwareRecyclerView) context
                .findViewById(R.id.moviePosterView);
        if (gallery != null) {
            gallery.requestFocus();
        }
    }

}
