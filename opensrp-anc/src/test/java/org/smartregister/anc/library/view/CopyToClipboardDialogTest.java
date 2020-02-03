package org.smartregister.anc.library.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseUnitTest;

/**
 * Created by ndegwamartin on 24/05/2018.
 */

public class CopyToClipboardDialogTest extends BaseUnitTest {

    private final String RANDOM_TEST_STRING = "Random Test String";
    @Mock
    private View view;
    @Mock
    private Bundle bundle;

    @Test
    public void callingConstructorInstantiatesDialogCorrectly() {
        CopyToClipboardDialog copyToClipboardDialog = new CopyToClipboardDialog(RuntimeEnvironment.application);
        Assert.assertNotNull(copyToClipboardDialog);

        copyToClipboardDialog = new CopyToClipboardDialog(RuntimeEnvironment.application, 0);
        Assert.assertNotNull(copyToClipboardDialog);
    }

    @Test
    public void onClickHandlerExecutesCorrectlyOnInvocation() {

        CopyToClipboardDialog copyToClipboardDialog = new CopyToClipboardDialog(RuntimeEnvironment.application);
        Assert.assertNotNull(copyToClipboardDialog);

        copyToClipboardDialog.onClick(view);

        copyToClipboardDialog.setContent(RANDOM_TEST_STRING);
        copyToClipboardDialog.onClick(view);
    }

    @Test
    public void invokationOfOnCreateMethodRendersDialogViewCorrectly() {

        CopyToClipboardDialog copyToClipboardDialog = new CopyToClipboardDialog(RuntimeEnvironment.application);
        Assert.assertNotNull(copyToClipboardDialog);

        Assert.assertNull(copyToClipboardDialog.findViewById(R.id.copyToClipboardHeader));

        copyToClipboardDialog = new CopyToClipboardDialog(RuntimeEnvironment.application);
        copyToClipboardDialog.setContent(RANDOM_TEST_STRING);
        copyToClipboardDialog.onCreate(bundle);
        Assert.assertEquals(RANDOM_TEST_STRING, ((TextView) copyToClipboardDialog.findViewById(R.id.copyToClipboardHeader)).getText());
    }
}
