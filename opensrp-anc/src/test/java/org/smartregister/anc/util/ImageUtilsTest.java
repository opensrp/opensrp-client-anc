package org.smartregister.anc.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.Context;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.util.ImageUtils;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;
import org.smartregister.repository.ImageRepository;

/**
 * Created by ndegwamartin on 14/07/2018.
 */

@PrepareForTest(AncApplication.class)
public class ImageUtilsTest extends BaseUnitTest {

    private ImageUtils imageUtils;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private AncApplication ancApplication;

    @Mock
    private Context context;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

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
        Assert.assertEquals(DEFAULT_PROFILE_IMAGE_RESOURCE_ID, imageUtils.getProfileImageResourceIDentifier());
    }


    @Test
    public void testProfilePhotoByClientIdReturnsCorrectDefaultResourceIdForNonExistentBaseEntityId() {

        PowerMockito.mockStatic(AncApplication.class);
        PowerMockito.when(AncApplication.getInstance()).thenReturn(ancApplication);
        PowerMockito.when(ancApplication.getContext()).thenReturn(context);
        PowerMockito.when(context.imageRepository()).thenReturn(imageRepository);
        PowerMockito.when(imageRepository.findByEntityId(DUMMY_BASE_ENTITY_ID)).thenReturn(null);
        Photo photo = ImageUtils.profilePhotoByClientID(DUMMY_BASE_ENTITY_ID);

        Assert.assertNotNull(photo);
        Assert.assertNotNull(photo.getResourceId());
        Assert.assertNull(photo.getFilePath());
        Assert.assertEquals(DEFAULT_PROFILE_IMAGE_RESOURCE_ID, photo.getResourceId());

    }

    @Test
    public void testProfilePhotoByClientIdReturnsCorrectPhotoObjectFilePathForValidProfileImage() {

        PowerMockito.mockStatic(AncApplication.class);
        PowerMockito.when(AncApplication.getInstance()).thenReturn(ancApplication);
        PowerMockito.when(ancApplication.getContext()).thenReturn(context);
        PowerMockito.when(context.imageRepository()).thenReturn(imageRepository);
        ProfileImage profileImage = new ProfileImage();
        profileImage.setFilepath(TEST_STRING);
        PowerMockito.when(imageRepository.findByEntityId(DUMMY_BASE_ENTITY_ID)).thenReturn(profileImage);
        Photo photo = ImageUtils.profilePhotoByClientID(DUMMY_BASE_ENTITY_ID);

        Assert.assertNotNull(photo);
        Assert.assertEquals(0, photo.getResourceId());
        Assert.assertNotNull(photo.getFilePath());
        Assert.assertEquals(TEST_STRING, photo.getFilePath());


    }
}
