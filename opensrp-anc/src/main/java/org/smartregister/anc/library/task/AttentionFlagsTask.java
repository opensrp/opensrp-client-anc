package org.smartregister.anc.library.task;

import android.os.AsyncTask;

import org.jeasy.rules.api.Facts;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.domain.AttentionFlag;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.FilePathUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

public class AttentionFlagsTask extends AsyncTask<Void, Void, Void> {
    private final BaseHomeRegisterActivity baseHomeRegisterActivity;
    private final List<AttentionFlag> attentionFlagList = new ArrayList<>();
    private final CommonPersonObjectClient pc;

    public AttentionFlagsTask(BaseHomeRegisterActivity baseHomeRegisterActivity, CommonPersonObjectClient pc) {
        this.baseHomeRegisterActivity = baseHomeRegisterActivity;
        this.pc = pc;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            JSONObject jsonObject = new JSONObject(AncLibrary.getInstance().getDetailsRepository().getAllDetailsForClient(pc.getCaseId()).get(ConstantsUtils.DetailsKeyUtils.ATTENTION_FLAG_FACTS));

            Facts facts = new Facts();
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                facts.put(key, jsonObject.get(key));
            }

            Iterable<Object> ruleObjects = AncLibrary.getInstance().readYaml(FilePathUtils.FileUtils.ATTENTION_FLAGS);
            for (Object ruleObject : ruleObjects) {
                YamlConfig attentionFlagConfig = (YamlConfig) ruleObject;
                if (attentionFlagConfig != null && attentionFlagConfig.getFields() != null) {
                    for (YamlConfigItem yamlConfigItem : attentionFlagConfig.getFields()) {
                        if (AncLibrary.getInstance().getAncRulesEngineHelper()
                                .getRelevance(facts, yamlConfigItem.getRelevance())) {
                            attentionFlagList.add(new AttentionFlag(Utils.fillTemplate(yamlConfigItem.getTemplate(), facts), attentionFlagConfig.getGroup().equals(ConstantsUtils.AttentionFlagUtils.RED)));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e, " --> ");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        baseHomeRegisterActivity.showAttentionFlagsDialog(attentionFlagList);
    }
}
