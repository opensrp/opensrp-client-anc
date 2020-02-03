package org.smartregister.anc.library.repository;

import org.smartregister.anc.library.util.DBConstantsUtils;

public class RegisterRepository {

    public String mainRegisterQuery(){
        return "";
    }

    public String getDetailsTable(){
        return DBConstantsUtils.RegisterTable.DETAILS;
    }

    public String getDemographicTable(){
        return DBConstantsUtils.RegisterTable.DEMOGRAPHIC;
    }
}
