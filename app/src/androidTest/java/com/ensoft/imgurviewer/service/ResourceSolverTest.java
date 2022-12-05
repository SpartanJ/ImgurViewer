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
			"https://prnt.sc/10jpuxg",
			"https://prnt.sc/10jpuxg/direct",
			"https://prntscr.com/10jpuxg",
			"https://prntscr.com/10jpuxg/direct",
			"https://www.reddit.com/gallery/pc1m7t",
			"https://v.redd.it/zv89llsvexdz",
			"https://v.redd.it/ulz7g757bb581",
			"https://www.reddit.com/gallery/huxc4s",
			"https://www.redgifs.com/watch/terrificaridbrownbutterfly",
			"https://redgifs.com/watch/jaggedunselfishgannet",
			"https://www.redgifs.com/watch/beautifuluncomfortableegret",
			"https://redgifs.com/watch/unwrittengreenazurevase",
			"https://gfycat.com/whoppingcostlyairedale",
			"https://clips.twitch.tv/FaintLightGullWholeWheat",
			"http://imgur.com/gallery/fADjkcW",
			"https://imgur.com/A61SaA1",
			"https://i.imgur.com/jxBXAMC.gifv",
			"https://imgur.com/gallery/YcAQlkx",
			"http://imgur.com/topic/Aww/ll5Vk",
			"http://imgur.com/a/j6Orj",
			"https://www.gifdeliverynetwork.com/latecompetentamericanavocet",
			"https://www.gfycat.com/temptingimpuregermanspaniel",
			"https://ibb.co/f2D3BNg",
			"https://gyazo.com/1eae60fbb44ba44cdcd89064ffbaacef",
			"https://imgflip.com/i/3dhvnl",
			"http://gfycat.com/DeadlyDecisiveGermanpinscher",
			"http://gfycat.com/ifr/JauntyTimelyAmazontreeboa",
			"https://streamable.com/dnd1",
			"https://streamable.com/moo",
			"https://instagram.com/p/aye83DjauH/?foo=bar#abc",
			"https://www.instagram.com/p/BQ0eAlwhDrw/",
			"http://www.flickr.com/photos/forestwander-nature-pictures/5645318632/in/photostream/",
			"https://www.flickr.com/photos/10795027@N08/37124872560/in/photostream/lightbox/",
			"https://www.flickr.com/photos/wernerkrause/albums/72157649599416957",
			"https://giphy.com/gifs/warnerbrosde-R6gvnAxj2ISzJdbA63",
			"https://giphy.com/gifs/love-i-you-that-70s-show-2dQ3FMaMFccpi",
			"https://streamja.com/6BGLa",
			"https://vimeo.com/247872788",
			"https://vimeo.com/311794663",
			"https://www.clippituser.tv/c/dnbaba",
			"https://www.clippituser.tv/c/apdmxa",
			"http://abelvera.deviantart.com/art/Roadhog-Vs-Reinhardt-660542128?ga_submit_new=10%3A1485794341&ga_type=edit&ga_changes=1&ga_recent=1",
			"https://pasaje13.tumblr.com/image/188445492882",
			"https://dragonitearmy.deviantart.com/art/Pinyatta-751121451?ga_submit_new=10%3A1529760214",
			"https://www.xvideos.com/video55870371/loba_apex_legends_gameplay",
			"http://www.xvideos.com/video38486371/is_this_meme_still_relevant_",
			"https://www.youporn.com/watch/16337494/apex-legends-lifeline-to-the-rescue/",
			"https://www.redtube.com/16962391",
			"https://www.tube8.com/amateur/uploading-6222021-expo-5/81483201/",
			"https://www.porntube.com/videos/porn-surfing-guide-porn-experts_1134180",
			"https://www.erome.com/a/bEFp5geB",
			"https://www.erome.com/a/Wh45ErMk",
			"https://www.xnxx.com/video-55awb78/skyrim_test_video",
			"https://spankbang.com/22xtb/video/",
			"https://la.spankbang.com/22xtb/video/",
			"https://xhamster.com/videos/asmr-you-know-i-like-you-xhnBhXU",
			"https://nhentai.net/g/169217/",
			"https://www.pornhub.com/view_video.php?viewkey=ph5effde87227d2",
			"https://pbs.twimg.com/ad_img/1000017167482372096/kLkQhpg3?format=jpg&name=orig",
			"https://pbs.twimg.com/amplify_video_thumb/1000001578898604032/img/NRKFDjURSieuHaZu.png",
			"https://pbs.twimg.com/amplify_video_thumb/1000001578898604032/img/NRKFDjURSieuHaZu.jpg:large",
			"https://pbs.twimg.com/amplify_video_thumb/1000001578898604032/img/NRKFDjURSieuHaZu.jpg?name=small",
			"https://pbs.twimg.com/amplify_video_thumb/1000001578898604032/img/NRKFDjURSieuHaZu?format=png&name=medium",
			"https://pbs.twimg.com/media/EDzS7VrU0AAFL4_.jpg",
			"https://pbs.twimg.com/media/EDzS7VrU0AAFL4_.jpg:small",
			"https://pbs.twimg.com/media/EDzS7VrU0AAFL4_.jpg?name=orig",
			"https://pbs.twimg.com/media/EDzS7VrU0AAFL4_?format=jpg",
			"https://pbs.twimg.com/media/EDzS7VrU0AAFL4_?format=png&name=4096x4096",
			"https://pbs.twimg.com/ext_tw_video_thumb/1594625806856114176/pu/img/v3wf_yoo5XJfR-jZ.jpg",
			"https://pbs.twimg.com/ext_tw_video_thumb/1594625806856114176/pu/img/v3wf_yoo5XJfR-jZ.jpg:orig",
			"https://pbs.twimg.com/ext_tw_video_thumb/1594625806856114176/pu/img/v3wf_yoo5XJfR-jZ.png?name=4096x4096",
			"https://pbs.twimg.com/ext_tw_video_thumb/1594625806856114176/pu/img/v3wf_yoo5XJfR-jZ?format=jpg&name=medium",
			"https://pbs.twimg.com/profile_images/1883433672/poza_01.doc",
			"https://pbs.twimg.com/profile_banners/1329804233390997505/1655005904",
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
