package org.smartregister.anc.library.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class TemplateUtilsTest {

    @Test
    public void testGetTemplateAsJsonShouldReturnEmptyJson() throws IOException {
        Context context = PowerMockito.mock(Context.class);
        Resources resources = PowerMockito.mock(Resources.class);
        Configuration configuration = PowerMockito.mock(Configuration.class);
        AssetManager assetManager = PowerMockito.mock(AssetManager.class);
        configuration.locale = Locale.getDefault();
        PowerMockito.when(resources.getConfiguration()).thenReturn(configuration);
        InputStream inputStream = new ByteArrayInputStream("{}".getBytes());

        PowerMockito.when(assetManager.open("template" + "/" + "templateName" + ".json")).thenReturn(inputStream);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(context.getAssets()).thenReturn(assetManager);

        Assert.assertEquals(0, TemplateUtils.getTemplateAsJson(context, "templateName").length());
    }
}