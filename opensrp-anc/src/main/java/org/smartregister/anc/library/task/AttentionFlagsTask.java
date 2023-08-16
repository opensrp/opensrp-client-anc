package org.smartregister.anc.library.task;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.domain.AttentionFlag;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.FilePathUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

public class AttentionFlagsTask {
    private final BaseHomeRegisterActivity baseHomeRegisterActivity;
    private final List<AttentionFlag> attentionFlagList = new ArrayList<>();
    private final CommonPersonObjectClient pc;
    private AppExecutors appExecutors;

    public AttentionFlagsTask(BaseHomeRegisterActivity baseHomeRegisterActivity, CommonPersonObjectClient pc) {
        this.baseHomeRegisterActivity = baseHomeRegisterActivity;
        this.pc = pc;
    }

    public void execute() {
        appExecutors = new AppExecutors();
        appExecutors.diskIO().execute(() -> {
            this.addAttentionFlagsService();
            appExecutors.mainThread().execute(this::showAttentionFlagsDialogOnPostExec);
        });
    }

    protected void addAttentionFlagsService() {
        try {
            JSONObject jsonObject = new JSONObject(AncLibrary.getInstance().getDetailsRepository().getAllDetailsForClient(pc.getCaseId()).get(ConstantsUtils.DetailsKeyUtils.ATTENTION_FLAG_FACTS));

            Facts facts = new Facts();
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                String ValueObject = jsonObject.optString(key);
                String value = Utils.returnTranslatedStringJoinedValue(ValueObject);
                if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(value.replace(",","").trim())) {
                    facts.put(key, value);
                } else {
                    facts.put(key, ValueObject);
                }
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
    }

    protected void showAttentionFlagsDialogOnPostExec() {
        baseHomeRegisterActivity.showAttentionFlagsDialog(attentionFlagList);
    }
}
