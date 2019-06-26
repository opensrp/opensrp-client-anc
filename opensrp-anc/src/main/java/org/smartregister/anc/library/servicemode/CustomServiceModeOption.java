package org.smartregister.anc.library.servicemode;

import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.ServiceModeOption;

/**
 * Created by keyman on 26/06/2018.
 */

public class CustomServiceModeOption extends ServiceModeOption {

    private final String name;
    private final int[] headerTextResourceIds;
    private final int[] columnWeights;

    public CustomServiceModeOption(SmartRegisterClientsProvider provider, String name, int[] headerTextResourceIds,
                                   int[] columnWeights) {
        super(provider);
        this.name = name;
        this.headerTextResourceIds = headerTextResourceIds;
        this.columnWeights = columnWeights;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public SecuredNativeSmartRegisterActivity.ClientsHeaderProvider getHeaderProvider() {
        return new SecuredNativeSmartRegisterActivity.ClientsHeaderProvider() {
            @Override
            public int count() {
                return headerTextResourceIds.length;
            }

            @Override
            public int weightSum() {
                int sum = 0;
                for (int columnWeight : columnWeights) {
                    sum += columnWeight;
                }
                return sum;
            }

            @Override
            public int[] weights() {
                return columnWeights;
            }

            @Override
            public int[] headerTextResourceIds() {
                return headerTextResourceIds;
            }
        };
    }
}
