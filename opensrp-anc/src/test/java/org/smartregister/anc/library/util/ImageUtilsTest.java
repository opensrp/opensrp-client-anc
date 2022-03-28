package org.smartregister.anc.library.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;
import org.smartregister.repository.ImageRepository;
import org.smartregister.util.ImageUtils;

/**
 * Created by ndegwamartin on 14/07/2018.
 */

@PrepareForTest(CoreLibrary.class)
@PowerMockIgnore({"org.powermock.*", "org.mockito.*",})
public class ImageUtilsTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    private ImageUtils imageUtils;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private CoreLibrary coreLibrary;
    @Mock
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        imageUtils = new ImageUtils();
    }

    @Test
    public void testImageUtilsInstantiatesCorrectly() {
        Assert.assertNotNull(imageUtils);
    }


    @Test
    public void testGetProfileImageResourceIDentifierShouldReturnCorrectResourceId() {
        Assert.assertEquals(DEFAULT_PROFILE_IMAGE_RESOURCE_ID, Utils.getProfileImageResourceIdentifier());
    }


    @Test
    public void testProfilePhotoByClientIdReturnsCorrectDefaultResourceIdForNonExistentBaseEntityId() {

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.imageRepository()).thenReturn(imageRepository);
        PowerMockito.when(imageRepository.findByEntityId(DUMMY_BASE_ENTITY_ID)).thenReturn(null);
        Photo photo = ImageUtils.profilePhotoByClientID(DUMMY_BASE_ENTITY_ID, Utils.getProfileImageResourceIdentifier());

        Assert.assertNotNull(photo);
        Assert.assertNotNull(photo.getResourceId());
        Assert.assertNull(photo.getFilePath());
        Assert.assertEquals(DEFAULT_PROFILE_IMAGE_RESOURCE_ID, photo.getResourceId());

    }

    @Test
    public void testProfilePhotoByClientIdReturnsCorrectPhotoObjectFilePathForValidProfileImage() {

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.imageRepository()).thenReturn(imageRepository);
        ProfileImage profileImage = new ProfileImage();
        profileImage.setFilepath(TEST_STRING);
        PowerMockito.when(imageRepository.findByEntityId(DUMMY_BASE_ENTITY_ID)).thenReturn(profileImage);
        Photo photo = ImageUtils.profilePhotoByClientID(DUMMY_BASE_ENTITY_ID, Utils.getProfileImageResourceIdentifier());

        Assert.assertNotNull(photo);
        Assert.assertEquals(0, photo.getResourceId());
        Assert.assertNotNull(photo.getFilePath());
        Assert.assertEquals(TEST_STRING, photo.getFilePath());


    }
}
