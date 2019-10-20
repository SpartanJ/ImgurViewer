package com.ensoft.imgurviewer.service;

import android.net.Uri;
import android.util.Log;

import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.listener.ResourceLoadListener;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class ResourceSolverTest
{
	private static final String TAG = ResourceSolverTest.class.getCanonicalName();
	private boolean waitResponse;
	
	@Test
	public void resourceSolverTest() throws InterruptedException
	{
		ResourceSolver resourceSolver = new ResourceSolver( new ResourceLoadListener()
		{
			@Override
			public void loadVideo( Uri uri, MediaType mediaType, Uri referer )
			{
				waitResponse = false;
				Assert.assertNotNull(uri);
				Log.v(TAG, uri.toString());
			}
			
			@Override
			public void loadImage( Uri uri, Uri thumbnail )
			{
				waitResponse = false;
				Assert.assertNotNull(uri);
				Log.v(TAG, uri.toString());
			}
			
			@Override
			public void loadAlbum( Uri uri, Class<?> view )
			{
				waitResponse = false;
				Assert.assertNotNull(uri);
				Log.v(TAG, uri.toString());
			}
			
			@Override
			public void loadFailed( Uri uri, String error )
			{
				waitResponse = false;
				Log.e(TAG, uri.toString());
				Log.e(TAG, error);
				Assert.fail();
			}
		});
		
		String[] testUris = new String[] {
			"https://imgur.com/A61SaA1",
			"https://i.imgur.com/crGpqCV.mp4",
			"https://i.imgur.com/jxBXAMC.gifv",
			"http://imgur.com/gallery/Q95ko",
			"https://imgur.com/gallery/YcAQlkx",
			"http://imgur.com/topic/Aww/ll5Vk",
			"http://imgur.com/a/j6Orj",
			"https://gyazo.com/1eae60fbb44ba44cdcd89064ffbaacef",
			"https://imgflip.com/i/3dhvnl",
			"http://gfycat.com/DeadlyDecisiveGermanpinscher",
			"http://gfycat.com/ifr/JauntyTimelyAmazontreeboa",
			"https://gfycat.com/gifs/detail/UnconsciousLankyIvorygull",
			"https://v.redd.it/zv89llsvexdz",
			"https://streamable.com/dnd1",
			"https://streamable.com/moo",
			"https://clips.twitch.tv/FaintLightGullWholeWheat",
			//"https://clips.twitch.tv/rflegendary/UninterestedBeeDAESuppy" // TODO: FIX REDIRECTION
			"https://instagram.com/p/aye83DjauH/?foo=bar#abc",
			"https://www.instagram.com/p/BQ0eAlwhDrw/",
			"http://www.flickr.com/photos/forestwander-nature-pictures/5645318632/in/photostream/",
			"https://media.giphy.com/media/9r1gg8vm3lbTcQI1Gw/giphy.gif",
			"https://media.giphy.com/media/l4EoMdmBWzc69MSm4/giphy.gif",
			"https://streamja.com/ggz",
			"https://vimeo.com/247872788",
			"https://vimeo.com/311794663",
			"https://www.clippituser.tv/c/dnbaba",
			"https://www.clippituser.tv/c/apdmxa",
			"http://abelvera.deviantart.com/art/Roadhog-Vs-Reinhardt-660542128?ga_submit_new=10%3A1485794341&ga_type=edit&ga_changes=1&ga_recent=1",
			"https://dragonitearmy.deviantart.com/art/Pinyatta-751121451?ga_submit_new=10%3A1529760214",
			"https://www.pornhub.com/view_video.php?viewkey=648719015",
			"https://www.xvideos.com/video4588838/biker_takes_his_girl",
			//"http://www.xvideos.com/video4588838/biker_takes_his_girl", // TODO: FIX REDIRECTION
			//"https://spankbang.com/3vvn/video/fantasy+solo", // TODO: FIX SERVICE
			"https://www.youporn.com/watch/505835/sex-ed-is-it-safe-to-masturbate-daily/",
			"https://www.youporn.com/watch/561726/big-tits-awesome-brunette-on-amazing-webcam-show/?from=related3&al=2&from_id=561726&pos=4",
			"https://www.redtube.com/66418",
			"https://www.tube8.com/teen/kasia-music-video/229795/",
			"https://www.porntube.com/videos/porn-surfing-guide-porn-experts_1134180",
			"https://www.erome.com/a/bEFp5geB",
			"https://www.erome.com/a/9GFbNGCS",
			"https://www.xnxx.com/video-55awb78/skyrim_test_video",
			"https://xhamster.com/videos/femaleagent-shy-beauty-takes-the-bait-1509445",
			"https://pasaje13.tumblr.com/image/188445492882",
		};
		
		for ( String testUri :testUris )
		{
			Uri uri = Uri.parse( testUri );
			
			waitResponse = true;
			resourceSolver.solve( uri );
			
			while (waitResponse)
			{
				Thread.sleep(10);
			}
		}
	}
}
