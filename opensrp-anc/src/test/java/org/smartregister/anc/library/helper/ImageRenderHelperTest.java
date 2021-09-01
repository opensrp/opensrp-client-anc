package org.smartregister.anc.library.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.domain.Photo;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.util.ImageUtils;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by ndegwamartin on 14/07/2018.
 */
@PrepareForTest({ImageUtils.class, DrishtiApplication.class, BitmapFactory.class, ContextCompat.class})
@PowerMockIgnore({
        "org.powermock.*",
        "org.mockito.*",
})
public class ImageRenderHelperTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    private ImageRenderHelper imageRenderHelper;
    @Mock
    private ImageView imageView;
    private Photo photo;
    @Mock
    private OpenSRPImageLoader openSRPImageLoader;
    @Mock
    private Bitmap bitmap;
    @Mock
    private Context context;
    @Mock
    private Drawable drawable;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        imageRenderHelper = new ImageRenderHelper(context);
        photo = new Photo();
        photo.setResourceId(DEFAULT_PROFILE_IMAGE_RESOURCE_ID);
    }

    @Test
    public void testImageRenderHelperInstantiatesCorrectly() {
        Assert.assertNotNull(imageRenderHelper);
    }

    @Test
    public void testRefreshProfileImageShouldRenderDefaultBitmapOnIncorrectImageFilepath() {
        ImageRenderHelper spyRenderHelper = Mockito.spy(imageRenderHelper);

        photo.setFilePath(TEST_STRING);

        PowerMockito.mockStatic(ImageUtils.class);
        PowerMockito.when(ImageUtils.profilePhotoByClientID(DUMMY_BASE_ENTITY_ID, Utils.getProfileImageResourceIdentifier())).thenReturn(photo);

        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getCachedImageLoaderInstance()).thenReturn(openSRPImageLoader);

        PowerMockito.mockStatic(BitmapFactory.class);
        PowerMockito.when(BitmapFactory.decodeFile(TEST_STRING)).thenReturn(bitmap);

        spyRenderHelper.refreshProfileImage(DUMMY_BASE_ENTITY_ID, imageView, Utils.getProfileImageResourceIdentifier());

        Mockito.verify(imageView).setImageBitmap(ArgumentMatchers.any(Bitmap.class));

    }

    @Test(expected = NullPointerException.class)
    public void testRefreshProfileImageShouldRenderDefaultImageDrawableOnException() {
        ImageRenderHelper spyRenderHelper = Mockito.spy(imageRenderHelper);

        photo.setFilePath(TEST_STRING);

        PowerMockito.mockStatic(ImageUtils.class);
        PowerMockito.when(ImageUtils.profilePhotoByClientID(DUMMY_BASE_ENTITY_ID, Utils.getProfileImageResourceIdentifier())).thenReturn(photo);

        PowerMockito.doReturn(drawable).when(context).getDrawable(ArgumentMatchers.anyInt());

        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.when(ContextCompat.getDrawable(context, DEFAULT_PROFILE_IMAGE_RESOURCE_ID)).thenReturn(drawable);

        spyRenderHelper.refreshProfileImage(DUMMY_BASE_ENTITY_ID, null, Utils.getProfileImageResourceIdentifier());

        Mockito.verify(imageView).setImageDrawable(ArgumentMatchers.any(Drawable.class));

    }

    @Test
    public void testRefreshProfileImageShouldRenderDefaultImageDrawable() {
        ImageRenderHelper imageRenderHelperSpy = PowerMockito.spy(imageRenderHelper);

        photo.setFilePath("");

        PowerMockito.mockStatic(ImageUtils.class);
        PowerMockito.when(ImageUtils.profilePhotoByClientID(DUMMY_BASE_ENTITY_ID, Utils.getProfileImageResourceIdentifier())).thenReturn(photo);

        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getCachedImageLoaderInstance()).thenReturn(openSRPImageLoader);

        PowerMockito.doReturn(drawable).when(context).getDrawable(ArgumentMatchers.anyInt());

        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.when(ContextCompat.getDrawable(context, DEFAULT_PROFILE_IMAGE_RESOURCE_ID)).thenReturn(drawable);


        imageRenderHelperSpy.refreshProfileImage(DUMMY_BASE_ENTITY_ID, imageView, Utils.getProfileImageResourceIdentifier());

        Mockito.verify(imageView).setImageDrawable(ArgumentMatchers.any(Drawable.class));

    }
}
