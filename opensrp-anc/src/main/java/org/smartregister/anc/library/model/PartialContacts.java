package org.smartregister.anc.library.model;

import org.smartregister.anc.library.application.BaseAncApplication;
import org.smartregister.anc.library.repository.PartialContactRepository;
import org.smartregister.anc.library.util.Constants;
import org.smartregister.anc.library.util.DBConstants;

import java.util.List;
import java.util.Map;

public class PartialContacts {
    private Map<String, String> details;
    private String referral;
    private String baseEntityId;
    private boolean isFirst;
    private PartialContactRepository partialContactRepository;
    private List<PartialContact> partialContactList;

    public PartialContacts(Map<String, String> details, String referral, String baseEntityId, boolean isFirst) {
        this.details = details;
        this.referral = referral;
        this.baseEntityId = baseEntityId;
        this.isFirst = isFirst;
    }

    public PartialContactRepository getPartialContactRepository() {
        return partialContactRepository;
    }

    public List<PartialContact> getPartialContactList() {
        return partialContactList;
    }

    public PartialContacts invoke() {
        partialContactRepository = BaseAncApplication.getInstance().getPartialContactRepository();

        if (partialContactRepository != null) {
            if (isFirst) {
                partialContactList = partialContactRepository.getPartialContacts(baseEntityId, 1);
            } else {
                if (referral != null) {
                    partialContactList = partialContactRepository
                            .getPartialContacts(baseEntityId, Integer.valueOf(details.get(Constants.REFERRAL)));
                } else {
                    partialContactList = partialContactRepository.getPartialContacts(baseEntityId,
                            Integer.valueOf(details.get(DBConstants.KEY.NEXT_CONTACT)));
                }
            }
        } else {
            partialContactList = null;
        }
        return this;
    }
}

