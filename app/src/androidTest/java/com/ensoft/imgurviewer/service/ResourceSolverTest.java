package com.ensoft.imgurviewer.service;

import android.net.Uri;
import android.util.Log;

import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.listener.AlbumProvider;
import com.ensoft.imgurviewer.service.listener.AlbumSolverListener;
import com.ensoft.imgurviewer.service.listener.ResourceLoadListener;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

@RunWith( AndroidJUnit4ClassRunner.class)
public class ResourceSolverTest
{
	private static final String TAG = ResourceSolverTest.class.getCanonicalName();
	private static final AlbumProvider[] ALBUM_PROVIDERS = AlbumProvider.getProviders();
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
				Assert.assertNotNull(uri);
				boolean providerFound = false;
				
				for ( AlbumProvider albumProvider : ALBUM_PROVIDERS )
				{
					if ( albumProvider.isAlbum( uri ) )
					{
						providerFound = true;
						
						albumProvider.getAlbum( uri, new AlbumSolverListener()
						{
							@Override
							public void onAlbumResolved( ImgurImage[] album )
							{
								waitResponse = false;
								
								for ( ImgurImage image : album )
								{
									if ( image.getLink() != null )
									{
										Log.v( TAG, image.getLink() );
									}
								}
							}
							
							@Override
							public void onImageResolved( ImgurImage image )
							{
								waitResponse = false;
								
								if ( image.getLink() != null )
								{
									Log.v( TAG, image.getLink() );
								}
							}
							
							@Override
							public void onAlbumError( String error )
							{
								waitResponse = false;
								Log.e( TAG, error );
								Assert.fail();
							}
						} );
						
						break;
					}
				}
				
				Assert.assertTrue( providerFound );
				
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
			"https://www.gfycat.com/giantunfoldedesok-rata",
			"https://www.pornhub.com/view_video.php?viewkey=ph5e13f804cbf69",
			"https://www.gifdeliverynetwork.com/unconsciouslankyivorygull",
			"https://gfycat.com/whoppingcostlyairedale",
			"https://redgifs.com/watch/grimyacademicafricanbushviper-bz-beauty",
			"https://redgifs.com/watch/calculatingsoreemperorpenguin-lauren-summer-swimsuit-bikini-beach",
			"https://www.redgifs.com/watch/grimyacademicafricanbushviper-bz-beauty",
			"https://www.redgifs.com/watch/calculatingsoreemperorpenguin-lauren-summer-swimsuit-bikini-beach",
			"https://ibb.co/f2D3BNg",
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
			"https://v.redd.it/zv89llsvexdz",
			"https://streamable.com/dnd1",
			"https://streamable.com/moo",
			"https://clips.twitch.tv/FaintLightGullWholeWheat",
			"https://instagram.com/p/aye83DjauH/?foo=bar#abc",
			"https://www.instagram.com/p/BQ0eAlwhDrw/",
			"http://www.flickr.com/photos/forestwander-nature-pictures/5645318632/in/photostream/",
			"https://www.flickr.com/photos/10795027@N08/37124872560/in/photostream/lightbox/",
			"https://www.flickr.com/photos/wernerkrause/albums/72157649599416957",
			"https://media.giphy.com/media/9r1gg8vm3lbTcQI1Gw/giphy.gif",
			"https://media.giphy.com/media/l4EoMdmBWzc69MSm4/giphy.gif",
			"https://streamja.com/ggz",
			"https://vimeo.com/247872788",
			"https://vimeo.com/311794663",
			"https://www.clippituser.tv/c/dnbaba",
			"https://www.clippituser.tv/c/apdmxa",
			"http://abelvera.deviantart.com/art/Roadhog-Vs-Reinhardt-660542128?ga_submit_new=10%3A1485794341&ga_type=edit&ga_changes=1&ga_recent=1",
			"https://dragonitearmy.deviantart.com/art/Pinyatta-751121451?ga_submit_new=10%3A1529760214",
			"https://www.xvideos.com/video4588838/biker_takes_his_girl",
			"https://www.youporn.com/watch/505835/sex-ed-is-it-safe-to-masturbate-daily/",
			"https://www.youporn.com/watch/561726/big-tits-awesome-brunette-on-amazing-webcam-show/?from=related3&al=2&from_id=561726&pos=4",
			"https://www.redtube.com/16962391",
			"https://www.tube8.com/teen/kasia-music-video/229795/",
			"https://www.porntube.com/videos/porn-surfing-guide-porn-experts_1134180",
			"https://www.erome.com/a/bEFp5geB",
			"https://www.erome.com/a/Wh45ErMk",
			"https://www.xnxx.com/video-55awb78/skyrim_test_video",
			"https://xhamster.com/videos/erotic-asmr-2766455",
			"https://pasaje13.tumblr.com/image/188445492882",
			"https://spankbang.com/22xtb/video/",
			"https://la.spankbang.com/22xtb/video/",
			"http://www.xvideos.com/video4588838/biker_takes_his_girl"
		};
		
		for ( String testUri :testUris )
		{
			Uri uri = Uri.parse( testUri );
			
			waitResponse = true;
			Log.v(TAG, "Testing: " + uri.toString());
			resourceSolver.solve( uri );
			
			while (waitResponse)
			{
				Thread.sleep(10);
			}
		}
	}
}
