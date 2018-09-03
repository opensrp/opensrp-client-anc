package org.smartregister.anc.helper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseUnitTest;

import java.lang.reflect.Field;

@RunWith(PowerMockRunner.class)
public class BottomNavigationHelperTest extends BaseUnitTest {
	
	@Mock
	private Bitmap bitmap;
	
	@Mock
	private Drawable drawable;
	
	@Mock
	private GradientDrawable gradientDrawable;
	
	@Mock
	private BitmapDrawable bitmapDrawable;
	
	@Mock
	private BottomNavigationMenuView bottomNavigationMenuView;
	
	@Mock
	private BottomNavigationItemView item;
	
	private BottomNavigationHelper bottomNavigationHelper;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		bottomNavigationHelper = new BottomNavigationHelper();
	}
	
	@PrepareForTest({ BitmapFactory.class })
	@Test
	public void testConvertDrawableToBitmap() {
		BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);
		
		Resources resources = Mockito.mock(Resources.class);
		Assert.assertNotNull(resources);
		
		Mockito.doReturn(drawable).when(resources).getDrawable(INITIALS_RESOURCE_ID);
		Assert.assertNotNull(drawable);
		
		PowerMockito.mockStatic(BitmapFactory.class);
		PowerMockito.when(BitmapFactory.decodeResource(resources, INITIALS_RESOURCE_ID)).thenReturn(bitmap);
		Assert.assertNull(bitmap.copy(Bitmap.Config.ARGB_8888, true));
		
		spyBottomNavigationHelper.convertDrawableResToBitmap(INITIALS_RESOURCE_ID, resources);
		
		Assert.assertNotNull(bitmap);
	}
	
	@PrepareForTest({ BitmapFactory.class, Bitmap.class })
	@Test
	public void testConvertGradientDrawableToBitmap() {
		BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);
		
		Resources resources = Mockito.mock(Resources.class);
		Assert.assertNotNull(resources);
		
		Mockito.doReturn(gradientDrawable).when(resources).getDrawable(R.drawable.initials_background);
		Assert.assertNotNull(gradientDrawable);
		
		int width = 27;
		int height = 27;
		PowerMockito.mockStatic(Bitmap.class);
		PowerMockito.when(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)).thenReturn(bitmap);
		Assert.assertNotNull(bitmap);
		
		spyBottomNavigationHelper.convertDrawableResToBitmap(INITIALS_RESOURCE_ID, resources);
		Assert.assertNotNull(bitmap);
		Mockito.verify(Bitmap.createBitmap(width ,height, Bitmap.Config.ARGB_8888));
		
	}
	
	@Test
	public void testConvertBitDrawableToBitmap() {
		BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);
		
		Resources resources = Mockito.mock(Resources.class);
		Mockito.doReturn(bitmapDrawable).when(resources).getDrawable(R.drawable.initials_background);
		Assert.assertNotNull(bitmapDrawable);
		
		Mockito.doReturn(bitmap).when(bitmapDrawable).getBitmap();
		spyBottomNavigationHelper.convertDrawableResToBitmap(INITIALS_RESOURCE_ID, resources);
		
		Assert.assertNotNull(bitmap);
	}
	
	@Test
	public void convertDrawableResToBitmap() {
		BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);
		
		Resources resources = Mockito.mock(Resources.class);
		Assert.assertNotNull(resources);
		
		Mockito.doReturn(bitmap).when(spyBottomNavigationHelper).convertDrawableResToBitmap(INITIALS_RESOURCE_ID,
				resources);
		Assert.assertNotNull(bitmap);
		
		spyBottomNavigationHelper.writeOnDrawable(INITIALS_RESOURCE_ID, INITIALS_TEXT, resources);
		
		Mockito.verify(bitmap).copy(Bitmap.Config.ARGB_8888, true);
	}
	
	@Test
	public void testDisableShiftMode() {
		BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);
		
		BottomNavigationView bottomNavigationView = Mockito.mock(BottomNavigationView.class);
		
		Mockito.doReturn(bottomNavigationMenuView).when(bottomNavigationView).getChildAt(0);
		Assert.assertNotNull(bottomNavigationMenuView);
		
		try {
			Field shiftingMode = bottomNavigationMenuView.getClass().getDeclaredField("mShiftingMode");
			
			shiftingMode.setAccessible(true);
			shiftingMode.setBoolean(bottomNavigationMenuView, false);
			shiftingMode.setAccessible(false);
			for (int i = 0; i < bottomNavigationMenuView.getChildCount(); i++) {
				Mockito.doReturn(item).when(bottomNavigationMenuView).getChildAt(i);
				Assert.assertNotNull(item);
				item.setShiftingMode(false);
				// set once again checked value, so view will be updated
				item.setChecked(item.getItemData().isChecked());
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		spyBottomNavigationHelper.disableShiftMode(bottomNavigationView);
		Mockito.verify(bottomNavigationView).getChildAt(0);
	}
}
