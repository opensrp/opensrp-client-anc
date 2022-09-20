package org.smartregister.anc.library.adapter;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.model.LibraryContent;

import java.util.ArrayList;
import java.util.List;

public class LibraryContentAdapterTest extends BaseUnitTest {
    private LibraryContentAdapter libraryContentAdapter;

    @Before
    public void setUp() {
        Context context = RuntimeEnvironment.application;
        List<LibraryContent> libraryContentList = getLibraryContentList();
        MockitoAnnotations.openMocks(this);
        libraryContentAdapter = new LibraryContentAdapter(libraryContentList, context);


    }

    @Test
    public void testGetItemCount() {
        Assert.assertEquals(getLibraryContentList().size(), libraryContentAdapter.getItemCount());
    }

//    @Test
//    public void testOnBindViewHolder() {
//        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
//        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(100, 200));
//        LibraryContentViewHolder viewHolder = libraryContentAdapter.onCreateViewHolder(viewGroup, 0);
//        Assert.assertNotNull(viewHolder);
//        Whitebox.getInternalState(libraryContentAdapter, "contentHeader");
//        libraryContentAdapter.onBindViewHolder(viewHolder, 0);
//    }

    public List<LibraryContent> getLibraryContentList() {
        List<LibraryContent> libraryContentList = new ArrayList<>();
        libraryContentList.add(new LibraryContent("HeaderList"));
        return libraryContentList;
    }
}
