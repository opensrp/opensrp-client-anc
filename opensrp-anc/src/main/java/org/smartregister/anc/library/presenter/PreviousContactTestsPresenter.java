package org.smartregister.anc.library.presenter;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.PreviousContactsTests;
import org.smartregister.anc.library.domain.LastContactDetailsWrapper;
import org.smartregister.anc.library.domain.TestResults;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.domain.YamlConfigWrapper;
import org.smartregister.anc.library.interactor.PreviousContactsTestsInteractor;
import org.smartregister.anc.library.util.FilePathUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class PreviousContactTestsPresenter implements PreviousContactsTests.Presenter {
    private WeakReference<PreviousContactsTests.View> mProfileView;
    private PreviousContactsTests.Interactor mProfileInteractor;

    public PreviousContactTestsPresenter(PreviousContactsTests.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new PreviousContactsTestsInteractor(this);
    }

    public void onDestroy(boolean isChangingConfiguration) {

        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mProfileInteractor = null;
        }

    }

    @Override
    public PreviousContactsTests.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

    @Override
    public void loadPreviousContactsTest(String baseEntityId, String contactNo, String lastContactRecordDate)
            throws ParseException, IOException {
        List<LastContactDetailsWrapper> lastContactDetailsTestsWrapperList = new ArrayList<>();
        Facts previousContactsFacts =
                AncLibrary.getInstance().getPreviousContactRepository().getPreviousContactTestsFacts(baseEntityId);

        List<YamlConfigWrapper> lastContactTests = addTestsRuleObjects(previousContactsFacts);

        Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(lastContactRecordDate);

        lastContactDetailsTestsWrapperList.add(new LastContactDetailsWrapper(contactNo,
                new SimpleDateFormat("dd MMM " + "yyyy", Locale.getDefault()).format(lastContactDate), lastContactTests,
                previousContactsFacts));

        getProfileView().setUpContactTestsDetailsRecycler(lastContactDetailsTestsWrapperList);
    }

    @Override
    public List<TestResults> loadAllTestResults(String baseEntityId, String keysToFetch, String dateKey, String contactNo) {
        return new ArrayList<>();
    }


    private List<YamlConfigWrapper> addTestsRuleObjects(Facts facts) throws IOException {
        List<YamlConfigWrapper> lastContactTests = new ArrayList<>();
        Iterable<Object> testsRuleObjects = AncLibrary.getInstance().readYaml(FilePathUtils.FileUtils.PROFILE_LAST_CONTACT_TEST);

        for (Object ruleObject : testsRuleObjects) {
            List<YamlConfigWrapper> yamlConfigList = new ArrayList<>();
            int valueCount = 0;

            YamlConfig testsConfig = (YamlConfig) ruleObject;

            if (testsConfig.getSubGroup() != null) {
                yamlConfigList.add(new YamlConfigWrapper(null, testsConfig.getSubGroup(), null, ""));
            }

            for (YamlConfigItem yamlConfigItem : testsConfig.getFields()) {
                if (AncLibrary.getInstance().getAncRulesEngineHelper()
                        .getRelevance(facts, yamlConfigItem.getRelevance())) {
                    yamlConfigList.add(new YamlConfigWrapper(null, null, yamlConfigItem, ""));
                    valueCount = +1;
                }
            }

            if (testsConfig.getTestResults() != null) {
                yamlConfigList.add(new YamlConfigWrapper(null, null, null, testsConfig.getTestResults()));
            }

            if (valueCount > 0) {
                lastContactTests.addAll(yamlConfigList);
            }
        }

        return lastContactTests;
    }
}
