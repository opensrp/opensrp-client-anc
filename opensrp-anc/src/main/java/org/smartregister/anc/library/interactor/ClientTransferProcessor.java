package org.smartregister.anc.library.interactor;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.Map;

public interface ClientTransferProcessor {
    void startTransferProcessing(@NonNull JSONObject closeForm) throws Exception;

    String transferForm();

    Map<String, String> columnMap();

    Map<String, String> details();

    JSONObject populateTransferForm(JSONObject closeForm);
}
