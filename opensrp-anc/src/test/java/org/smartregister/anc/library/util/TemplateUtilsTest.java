package org.smartregister.anc.library.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.anc.library.activity.BaseUnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

@RunWith(PowerMockRunner.class)
public class TemplateUtilsTest extends BaseUnitTest {

    @Rule
    private ExpectedException expectedException = ExpectedException.none();

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

    @Test
    public void testGetTemplateAsJsonShouldReturnNullWhenExceptionIsThrown() throws IOException {
        Context context = PowerMockito.mock(Context.class);
        Resources resources = PowerMockito.mock(Resources.class);
        Configuration configuration = PowerMockito.mock(Configuration.class);
        AssetManager assetManager = PowerMockito.mock(AssetManager.class);
        configuration.locale = Locale.getDefault();
        PowerMockito.when(resources.getConfiguration()).thenReturn(configuration);
        InputStream inputStream = new ByteArrayInputStream("{}".getBytes());

        PowerMockito.when(assetManager.open("template" + "/" + "template-Name" + ".json")).thenReturn(inputStream);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(context.getAssets()).thenReturn(assetManager);
        expectedException.expect(NullPointerException.class);
        Assert.assertEquals(null, TemplateUtils.getTemplateAsJson(context, "templateName").length());
    }

}