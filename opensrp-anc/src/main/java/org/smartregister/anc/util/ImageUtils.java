package org.smartregister.anc.util;

import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;

/**
 * Created by ndegwamartin on 12/04/2018.
 */

public class ImageUtils {
    public static int getProfileImageResourceIDentifier() {
        return R.drawable.ic_african_girl;
    }

    public static Photo profilePhotoByClientID(String clientEntityId) {
        Photo photo = new Photo();
        ProfileImage profileImage = AncApplication.getInstance().getContext().imageRepository().findByEntityId(clientEntityId);
        if (profileImage != null) {
            photo.setFilePath(profileImage.getFilepath());
        } else {
            photo.setResourceId(getProfileImageResourceIDentifier());
        }
        return photo;
    }
}
