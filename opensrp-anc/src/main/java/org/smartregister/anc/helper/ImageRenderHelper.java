package org.smartregister.anc.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.R;
import org.smartregister.anc.util.ImageUtils;
import org.smartregister.domain.Photo;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ImageRenderHelper {

    private Context context;

    public ImageRenderHelper(Context context) {
        this.context = context;
    }

    private static final String TAG = ImageRenderHelper.class.getCanonicalName();

    public void refreshProfileImage(String clientBaseEntityId, ImageView profileImageView) {

        Photo photo = ImageUtils.profilePhotoByClientID(clientBaseEntityId);

        if (StringUtils.isNotBlank(photo.getFilePath())) {
            try {
                Bitmap myBitmap = BitmapFactory.decodeFile(photo.getFilePath());
                profileImageView.setImageBitmap(myBitmap);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());

                int backgroundResource = R.drawable.ic_african_girl;
                profileImageView.setImageDrawable(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? context.getDrawable(backgroundResource) : ContextCompat.getDrawable(context, backgroundResource));

            }
        } else {
            int backgroundResource = photo.getResourceId();
            profileImageView.setImageDrawable(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? context.getDrawable(backgroundResource) : ContextCompat.getDrawable(context, backgroundResource));


        }
        profileImageView.setTag(org.smartregister.R.id.entity_id, clientBaseEntityId);
        DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(clientBaseEntityId, OpenSRPImageLoader.getStaticImageListener(profileImageView, 0, 0));

    }
}
