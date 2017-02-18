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
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package us.nineworlds.serenity.ui.browser.tv.seasons;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import javax.inject.Inject;
import net.ganin.darv.DpadAwareRecyclerView;
import us.nineworlds.plex.rest.PlexappFactory;
import us.nineworlds.serenity.R;
import us.nineworlds.serenity.core.imageloader.SerenityBackgroundLoaderListener;
import us.nineworlds.serenity.core.imageloader.SerenityImageLoader;
import us.nineworlds.serenity.core.model.SeriesContentInfo;
import us.nineworlds.serenity.injection.BaseInjector;
import us.nineworlds.serenity.widgets.SerenityAdapterView;
import us.nineworlds.serenity.widgets.SerenityAdapterView.OnItemSelectedListener;

/**
 * @author dcarver
 *
 */
public class TVShowSeasonOnItemSelectedListener extends BaseInjector implements
		OnItemSelectedListener {

	private final Activity context;
	private final ImageLoader imageLoader;
	private View previous;
	private final ImageSize bgImageSize = new ImageSize(1280, 720);
	private SeriesContentInfo info;

	@Inject
	protected SerenityImageLoader serenityImageLoader;

	@Inject
	protected PlexappFactory plexFactory;

	public TVShowSeasonOnItemSelectedListener(View bgv, Activity activity) {
		context = activity;

		imageLoader = serenityImageLoader.getImageLoader();
	}

	@Override
	public void onItemSelected(SerenityAdapterView<?> av, View v, int position,
			long id) {
		info = (SeriesContentInfo) av.getItemAtPosition(position);
		ImageView mpiv = (ImageView) v.findViewById(R.id.posterImageView);
		DpadAwareRecyclerView episodeGrid = (DpadAwareRecyclerView) context
				.findViewById(R.id.episodeGridView);

		episodeGrid.setVisibility(View.VISIBLE);
		episodeGrid.setAdapter(new SeasonsEpisodePosterImageGalleryAdapter(
				context, info.getKey()));
//		episodeGrid
//		.setOnItemSelectedListener(new EpisodePosterOnItemSelectedListener());
//		episodeGrid.setOnItemClickListener(new GridVideoOnItemClickListener());
//		episodeGrid
//		.setOnItemLongClickListener(new GridVideoOnItemLongClickListener());

		if (previous != null) {
			previous.setPadding(0, 0, 0, 0);
			previous.refreshDrawableState();
		}

		previous = v;

		v.setPadding(5, 5, 5, 5);
		v.refreshDrawableState();

		TextView seasonsTitle = (TextView) context
				.findViewById(R.id.tvShowSeasonsTitle);
		seasonsTitle.setText(info.getTitle());

		changeBackgroundImage(mpiv);

	}

	/**
	 * Change the background image of the activity.
	 *
	 * Should be a background activity
	 *
	 * @param v
	 */
	private void changeBackgroundImage(View v) {

		SeriesContentInfo mi = info;

		if (mi.getBackgroundURL() != null) {
			View fanArt = context.findViewById(R.id.fanArt);
			String transcodingURL = plexFactory.getImageURL(
					mi.getBackgroundURL(), 1280, 720);

			imageLoader.loadImage(transcodingURL, bgImageSize,
					new SerenityBackgroundLoaderListener(fanArt,
							R.drawable.tvshows, context));
		}
	}

	@Override
	public void onNothingSelected(SerenityAdapterView<?> arg0) {

	}

}
