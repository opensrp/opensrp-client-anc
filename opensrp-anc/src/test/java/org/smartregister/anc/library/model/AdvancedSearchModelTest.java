package org.smartregister.anc.library.model;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.AdvancedSearchContract;
import org.smartregister.anc.library.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;

import java.util.Map;

/**
 * Created by keyman on 30/06/2018.
 */
public class AdvancedSearchModelTest extends BaseUnitTest {

    private AdvancedSearchContract.Model model;

    private String payload = "[{\"type\":\"Client\",\"dateCreated\":1527602939357,\"dateEdited\":1527596372237,\"serverVersion\":1527596278946,\"baseEntityId\":\"62c4dd4b-8104-4e0c-a0b9-1d8d2b99572e\",\"identifiers\":{\"ANC_ID\":\"1489640\",\"OPENMRS_UUID\":\"3eff7fdd-95eb-471c-addc-059aa87722cb\"},\"addresses\":[],\"attributes\":{\"location\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"vht_name\":\"Justine Kalewa\",\"vht_phone\":\"0753446884\",\"school_name\":\"Jambula Girls School\",\"school_class\":\"P4\",\"Patient Image\":\"62c4dd4b-8104-4e0c-a0b9-1d8d2b99572e.jpg\",\"dose_one_date\":\"2018-05-29\",\"caretaker_name\":\"Bilha Mwanza\",\"caretaker_phone\":\"0289134981\"},\"firstName\":\"Brenda\",\"lastName\":\"Mwilu\",\"birthdate\":1117238400000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{},\"_id\":\"0f9d63b0-2749-4df6-a22c-61a58a4303b9\",\"_rev\":\"v9\"},{\"type\":\"Client\",\"dateCreated\":1527592804747,\"dateEdited\":1527588566499,\"serverVersion\":1527588538796,\"baseEntityId\":\"cc6c91ca-29e7-472a-b9fc-620dc58cb072\",\"identifiers\":{\"ANC_ID\":\"1519644\",\"OPENMRS_UUID\":\"aadf9fc3-6824-496c-aef1-1255f086876b\"},\"addresses\":[],\"attributes\":{\"location\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"school_name\":\"Jambula Girls School\",\"school_class\":\"P4\",\"Patient Image\":\"cc6c91ca-29e7-472a-b9fc-620dc58cb072.jpg\",\"dose_one_date\":\"2018-05-29\",\"caretaker_name\":\"Matteshia Beatrice\",\"caretaker_phone\":\"0723466484\"},\"firstName\":\"Ephanie\",\"lastName\":\"Muhiasi\",\"birthdate\":1054166400000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{},\"_id\":\"87773908-21c1-4c70-9372-ce8e1a54ab0e\",\"_rev\":\"v14\"},{\"type\":\"Client\",\"dateCreated\":1527602963377,\"dateEdited\":1527596010851,\"serverVersion\":1527595918918,\"baseEntityId\":\"51247db4-7b2c-413e-b6cf-ef21aad7441f\",\"identifiers\":{\"ANC_ID\":\"1470640\",\"OPENMRS_UUID\":\"e7cf908b-17fd-4f9d-805b-4940998352d0\"},\"addresses\":[],\"attributes\":{\"location\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"school_name\":\"Jambula Girls School\",\"school_class\":\"P5\",\"dose_one_date\":\"2018-05-29\",\"caretaker_name\":\"Zari Hadassah\",\"caretaker_phone\":\"0723111444\"},\"firstName\":\"Anita\",\"lastName\":\"Fabiola\",\"birthdate\":1022544000000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{},\"_id\":\"f82a3644-83e3-4fff-b141-dedf4ec30b5d\",\"_rev\":\"v5\"},{\"type\":\"Client\",\"dateCreated\":1527599511314,\"dateEdited\":1527599618104,\"serverVersion\":1527599511313,\"baseEntityId\":\"873970ba-1336-4e4c-bfcc-2fa3d0335bae\",\"identifiers\":{\"ANC_ID\":\"1538156\",\"OPENMRS_UUID\":\"7cf33f13-c2d1-4af4-aa34-8294e0dbfa26\"},\"addresses\":[{\"preferred\":null,\"addressType\":\"\",\"startDate\":null,\"endDate\":null,\"addressFields\":{\"address5\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\"},\"latitude\":null,\"longitude\":null,\"geopoint\":null,\"postalCode\":null,\"subTown\":null,\"town\":null,\"subDistrict\":null,\"countyDistrict\":null,\"cityVillage\":null,\"stateProvince\":null,\"country\":null}],\"attributes\":{\"location\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"school_name\":\"Nsalo Secondary School\",\"school_class\":\"P4\",\"dose_one_date\":\"2018-05-29\",\"caretaker_name\":\"Conway Abisi\",\"caretaker_phone\":\"0884349839\"},\"firstName\":\"Marilyn\",\"lastName\":\"Mwazembi\",\"birthdate\":1148860800000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"e36d54aa-10bd-4beb-8854-f5a2cd0db0ca\",\"_rev\":\"v2\"},{\"type\":\"Client\",\"dateCreated\":1527601568074,\"dateEdited\":1527601659105,\"serverVersion\":1527601568072,\"baseEntityId\":\"016c8ec0-96c3-4084-80d9-a1573891a09a\",\"identifiers\":{\"ANC_ID\":\"1541648\",\"OPENMRS_UUID\":\"b76af0db-fd76-48d2-8f8b-f2e6dda3b9a7\"},\"addresses\":[{\"preferred\":null,\"addressType\":\"\",\"startDate\":null,\"endDate\":null,\"addressFields\":{\"address5\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\"},\"latitude\":null,\"longitude\":null,\"geopoint\":null,\"postalCode\":null,\"subTown\":null,\"town\":null,\"subDistrict\":null,\"countyDistrict\":null,\"cityVillage\":null,\"stateProvince\":null,\"country\":null}],\"attributes\":{\"location\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"school_name\":\"Jambula Girls School\",\"school_class\":\"P3\",\"dose_one_date\":\"2018-05-29\",\"caretaker_name\":\"Michelle Abama\",\"caretaker_phone\":\"0721771600\"},\"firstName\":\"Malia\",\"lastName\":\"Abama\",\"birthdate\":1117324800000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"359743a0-4e07-4241-8042-cf5fbcf2a701\",\"_rev\":\"v2\"},{\"type\":\"Client\",\"dateCreated\":1527660980754,\"dateEdited\":1527654101898,\"serverVersion\":1527654059850,\"baseEntityId\":\"ec4040e1-7beb-4750-9788-29f8ee7a07e4\",\"identifiers\":{\"ANC_ID\":\"1548643\",\"OPENMRS_UUID\":\"85a376f7-9a88-421f-ae01-d220ba7c8ccd\"},\"addresses\":[{\"preferred\":null,\"addressType\":\"\",\"startDate\":null,\"endDate\":null,\"addressFields\":{\"address5\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\"},\"latitude\":null,\"longitude\":null,\"geopoint\":null,\"postalCode\":null,\"subTown\":null,\"town\":null,\"subDistrict\":null,\"countyDistrict\":null,\"cityVillage\":null,\"stateProvince\":null,\"country\":null}],\"attributes\":{\"location\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"school_name\":\"Jambula Girls School\",\"school_class\":\"P4\",\"Patient Image\":\"ec4040e1-7beb-4750-9788-29f8ee7a07e4.jpg\",\"dose_one_date\":\"2018-05-30\",\"caretaker_name\":\"Jsh\",\"caretaker_phone\":\"0852147853\"},\"firstName\":\"Hank\",\"lastName\":\"Hank\",\"birthdate\":1022716800000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"d9f584f3-b7fe-40f6-98b4-592f807e9170\",\"_rev\":\"v6\"},{\"type\":\"Client\",\"dateCreated\":1527670650002,\"dateEdited\":1527670672963,\"serverVersion\":1527670649985,\"baseEntityId\":\"fbd047b8-2a1c-40fc-87aa-746443a22f55\",\"identifiers\":{\"ANC_ID\":\"1554641\",\"OPENMRS_UUID\":\"9788ee7a-9257-497d-b335-e9666927b2d2\"},\"addresses\":[{\"preferred\":null,\"addressType\":\"\",\"startDate\":null,\"endDate\":null,\"addressFields\":{\"address5\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\"},\"latitude\":null,\"longitude\":null,\"geopoint\":null,\"postalCode\":null,\"subTown\":null,\"town\":null,\"subDistrict\":null,\"countyDistrict\":null,\"cityVillage\":null,\"stateProvince\":null,\"country\":null}],\"attributes\":{\"location\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"school_name\":\"Jambula Girls School\",\"school_class\":\"P5\",\"dose_one_date\":\"2018-05-30\",\"caretaker_name\":\"Ch\",\"caretaker_phone\":\"0788855555\"},\"firstName\":\"Dina\",\"lastName\":\"Walia\",\"birthdate\":1085875200000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"996da591-00ab-49a6-b95f-e466a8838ebd\",\"_rev\":\"v2\"},{\"type\":\"Client\",\"dateCreated\":1527699954284,\"dateEdited\":1527693355135,\"serverVersion\":1527693300614,\"baseEntityId\":\"7ef42f45-86aa-4d3c-aba9-b81dd7838798\",\"identifiers\":{\"ANC_ID\":\"1554658\",\"OPENMRS_UUID\":\"9afe3f5f-4cd9-4ed9-86c8-b6946d968118\"},\"addresses\":[],\"attributes\":{\"location\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"vht_name\":\"Ephanie\",\"vht_phone\":\"0723114665\",\"school_name\":\"Jambula Girls School\",\"school_class\":\"P5\",\"dose_one_date\":\"2018-05-30\",\"caretaker_name\":\"Mana\",\"caretaker_phone\":\"0987774455\"},\"firstName\":\"Martini\",\"lastName\":\"Wambio\",\"birthdate\":1243641600000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{},\"_id\":\"6717c853-4cda-4454-bd55-74b6aef34f7c\",\"_rev\":\"v6\"},{\"type\":\"Client\",\"dateCreated\":1527765919775,\"dateEdited\":1527766480602,\"serverVersion\":1527766381765,\"baseEntityId\":\"e724e5d3-8a44-4bab-86d0-b527522d668f\",\"identifiers\":{\"ANC_ID\":\"1587146\",\"OPENMRS_UUID\":\"87a86d6d-3a7c-4d01-9d80-1b931ac0b987\"},\"addresses\":[],\"attributes\":{\"location\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"school_name\":\"Jambula Girls School\",\"school_class\":\"P3\",\"Patient Image\":\"e724e5d3-8a44-4bab-86d0-b527522d668f.jpg\",\"dose_one_date\":\"2018-05-31\",\"caretaker_name\":\"Marta\",\"caretaker_phone\":\"0925412548\"},\"firstName\":\"Kayla\",\"lastName\":\"Mallory\",\"birthdate\":1243728000000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"598b983d-0992-4be1-8b2e-1a62369c0fe5\",\"_rev\":\"v5\"},{\"type\":\"Client\",\"dateCreated\":1527766259874,\"dateEdited\":1527766360267,\"serverVersion\":1527766259873,\"baseEntityId\":\"3a2933b5-835c-4fea-97f8-82cdd8ee57d3\",\"identifiers\":{\"ANC_ID\":\"1579143\",\"OPENMRS_UUID\":\"bea7690f-71f2-46ce-a407-4867175dd4ad\"},\"addresses\":[{\"preferred\":null,\"addressType\":\"\",\"startDate\":null,\"endDate\":null,\"addressFields\":{\"address5\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\"},\"latitude\":null,\"longitude\":null,\"geopoint\":null,\"postalCode\":null,\"subTown\":null,\"town\":null,\"subDistrict\":null,\"countyDistrict\":null,\"cityVillage\":null,\"stateProvince\":null,\"country\":null}],\"attributes\":{\"location\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"school_name\":\"Nsalo Secondary School\",\"school_class\":\"P5\",\"dose_one_date\":\"2018-05-31\",\"caretaker_name\":\"Tremor\",\"caretaker_phone\":\"0723166184\"},\"firstName\":\"Fridah\",\"lastName\":\"Akello\",\"birthdate\":1085961600000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"5f03fb65-af3f-4fdc-a31a-4eae46e5ac27\",\"_rev\":\"v2\"},{\"type\":\"Client\",\"dateCreated\":1527775764263,\"dateEdited\":1527775842194,\"serverVersion\":1527775764262,\"baseEntityId\":\"78762519-13b2-449b-bfb8-3853d306832f\",\"identifiers\":{\"ANC_ID\":\"1600147\",\"OPENMRS_UUID\":\"2e1e6d28-5bcc-43e7-ae04-4d73e2856555\"},\"addresses\":[{\"preferred\":null,\"addressType\":\"\",\"startDate\":null,\"endDate\":null,\"addressFields\":{\"address5\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\"},\"latitude\":null,\"longitude\":null,\"geopoint\":null,\"postalCode\":null,\"subTown\":null,\"town\":null,\"subDistrict\":null,\"countyDistrict\":null,\"cityVillage\":null,\"stateProvince\":null,\"country\":null}],\"attributes\":{\"location\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"school_name\":\"Jambula Girls School\",\"school_class\":\"P5\",\"dose_one_date\":\"2018-05-31\",\"caretaker_name\":\"Maxine Takodi\",\"caretaker_phone\":\"0723116445\"},\"firstName\":\"Shanice\",\"lastName\":\"Angani\",\"birthdate\":1085961600000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"2754d444-acde-4d2c-a3a8-8219bcfc4c59\",\"_rev\":\"v2\"},{\"type\":\"Client\",\"dateCreated\":1527775782545,\"dateEdited\":1527775842657,\"serverVersion\":1527775782544,\"baseEntityId\":\"de3251ab-c955-404c-8a4a-b083c254617e\",\"identifiers\":{\"ANC_ID\":\"1603646\",\"OPENMRS_UUID\":\"f6c5c560-f91a-40a1-8581-f98428ca1ae1\"},\"addresses\":[{\"preferred\":null,\"addressType\":\"\",\"startDate\":null,\"endDate\":null,\"addressFields\":{\"address5\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\"},\"latitude\":null,\"longitude\":null,\"geopoint\":null,\"postalCode\":null,\"subTown\":null,\"town\":null,\"subDistrict\":null,\"countyDistrict\":null,\"cityVillage\":null,\"stateProvince\":null,\"country\":null}],\"attributes\":{\"location\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"school_name\":\"Nsalo Secondary School\",\"school_class\":\"P4\",\"dose_one_date\":\"2018-05-31\",\"caretaker_name\":\"caretaker\",\"caretaker_phone\":\"0382839292\"},\"firstName\":\"Claire\",\"lastName\":\"Wachira\",\"birthdate\":1180569600000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"6071f009-a17e-4ad7-bccf-35e048bdb86e\",\"_rev\":\"v2\"},{\"type\":\"Client\",\"dateCreated\":1527775892510,\"dateEdited\":1527775963287,\"serverVersion\":1527775892509,\"baseEntityId\":\"638f7515-1345-4527-a76c-ec44e607d9ce\",\"identifiers\":{\"ANC_ID\":\"1596642\",\"OPENMRS_UUID\":\"8950ce20-bed9-4f63-aeea-8be2f14418de\"},\"addresses\":[{\"preferred\":null,\"addressType\":\"\",\"startDate\":null,\"endDate\":null,\"addressFields\":{\"address5\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\"},\"latitude\":null,\"longitude\":null,\"geopoint\":null,\"postalCode\":null,\"subTown\":null,\"town\":null,\"subDistrict\":null,\"countyDistrict\":null,\"cityVillage\":null,\"stateProvince\":null,\"country\":null}],\"attributes\":{\"location\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"school_name\":\"Nsalo Secondary School\",\"school_class\":\"P4\",\"dose_one_date\":\"2018-05-31\",\"caretaker_name\":\"Jabulane Akonyo\",\"caretaker_phone\":\"0723116483\"},\"firstName\":\"Faithlita\",\"lastName\":\"Wageni\",\"birthdate\":1117497600000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"63c069bd-5278-4d2d-b74b-861f0d9eee23\",\"_rev\":\"v2\"},{\"type\":\"Client\",\"dateCreated\":1527776136540,\"dateEdited\":1527776203913,\"serverVersion\":1527776136538,\"baseEntityId\":\"4bca3a8f-6d7c-4316-9b70-0221ffe29b0d\",\"identifiers\":{\"ANC_ID\":\"1609643\",\"OPENMRS_UUID\":\"b648b3df-f73d-4197-91cc-60460545408a\"},\"addresses\":[{\"preferred\":null,\"addressType\":\"\",\"startDate\":null,\"endDate\":null,\"addressFields\":{\"address5\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\"},\"latitude\":null,\"longitude\":null,\"geopoint\":null,\"postalCode\":null,\"subTown\":null,\"town\":null,\"subDistrict\":null,\"countyDistrict\":null,\"cityVillage\":null,\"stateProvince\":null,\"country\":null}],\"attributes\":{\"location\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"school_name\":\"Jambula Girls School\",\"school_class\":\"P4\",\"dose_one_date\":\"2018-05-31\",\"caretaker_name\":\"Karen Swesi\",\"caretaker_phone\":\"0772311544\"},\"firstName\":\"Tabitha\",\"lastName\":\"Kalero\",\"birthdate\":1085961600000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"cca61073-b51d-4157-b4be-01186a5f2e9c\",\"_rev\":\"v2\"},{\"type\":\"Client\",\"dateCreated\":1527776245620,\"dateEdited\":1527776324468,\"serverVersion\":1527776245619,\"baseEntityId\":\"12c9b8a4-f547-4ce2-8f13-91cb7634af05\",\"identifiers\":{\"ANC_ID\":\"1607142\",\"OPENMRS_UUID\":\"a47628f3-ce7e-45ae-9d65-106f7af1a781\"},\"addresses\":[{\"preferred\":null,\"addressType\":\"\",\"startDate\":null,\"endDate\":null,\"addressFields\":{\"address5\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\"},\"latitude\":null,\"longitude\":null,\"geopoint\":null,\"postalCode\":null,\"subTown\":null,\"town\":null,\"subDistrict\":null,\"countyDistrict\":null,\"cityVillage\":null,\"stateProvince\":null,\"country\":null}],\"attributes\":{\"location\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"school_name\":\"Nsalo Secondary School\",\"school_class\":\"P4\",\"dose_one_date\":\"2018-05-31\",\"caretaker_name\":\"Freshia Kamala\",\"caretaker_phone\":\"0723445645\"},\"firstName\":\"Smartphone\",\"lastName\":\"Adhiambo\",\"birthdate\":1085961600000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"cb0ad6b0-daa6-47c8-a412-729a971f35e6\",\"_rev\":\"v2\"},{\"type\":\"Client\",\"dateCreated\":1528124122861,\"dateEdited\":1528124162036,\"serverVersion\":1528124122798,\"baseEntityId\":\"81d824ed-e188-40ab-bcc6-50fd785bd6b0\",\"identifiers\":{\"ANC_ID\":\"1617653\",\"OPENMRS_UUID\":\"6491534f-4fe8-441d-b86b-0f654a47cd12\"},\"addresses\":[{\"preferred\":null,\"addressType\":\"\",\"startDate\":null,\"endDate\":null,\"addressFields\":{\"address5\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\"},\"latitude\":null,\"longitude\":null,\"geopoint\":null,\"postalCode\":null,\"subTown\":null,\"town\":null,\"subDistrict\":null,\"countyDistrict\":null,\"cityVillage\":null,\"stateProvince\":null,\"country\":null}],\"attributes\":{\"location\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"school_name\":\"Nsalo Secondary School\",\"school_class\":\"P4\",\"dose_one_date\":\"2018-06-04\",\"caretaker_name\":\"caretaker\",\"caretaker_phone\":\"0138928823\"},\"firstName\":\"Ramah\",\"lastName\":\"Pendo\",\"birthdate\":1141430400000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"9e5bf8e2-ea08-4e1b-bc49-13471627b924\",\"_rev\":\"v2\"},{\"type\":\"Client\",\"dateCreated\":1528109373833,\"dateEdited\":1528124162777,\"serverVersion\":1528124160841,\"baseEntityId\":\"057e446b-f6c4-4410-b3d1-6ef6a3313b50\",\"identifiers\":{\"ANC_ID\":\"1617646\",\"OPENMRS_UUID\":\"84a6419b-5fe4-4ffa-ad4e-cf18a58ee4c0\"},\"addresses\":[{\"preferred\":null,\"addressType\":\"\",\"startDate\":null,\"endDate\":null,\"addressFields\":{\"address5\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\"},\"latitude\":null,\"longitude\":null,\"geopoint\":null,\"postalCode\":null,\"subTown\":null,\"town\":null,\"subDistrict\":null,\"countyDistrict\":null,\"cityVillage\":null,\"stateProvince\":null,\"country\":null}],\"attributes\":{\"location\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"school_name\":\"Jambula Girls School\",\"school_class\":\"P4\",\"dose_one_date\":\"2018-06-04\",\"caretaker_name\":\"caretaker\",\"caretaker_phone\":\"0329883289\"},\"firstName\":\"Rosetta\",\"lastName\":\"Stone\",\"birthdate\":1149379200000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"relationships\":{},\"_id\":\"ad133f22-139f-447d-8171-dfeb5c05cc9e\",\"_rev\":\"v4\"},{\"type\":\"Client\",\"dateCreated\":1527661300797,\"dateEdited\":1528189326390,\"serverVersion\":1528189322493,\"baseEntityId\":\"4f42dc96-212d-4b9a-b725-79e2eaa87350\",\"identifiers\":{\"ANC_ID\":\"1525146\",\"OPENMRS_UUID\":\"433d8cf6-6490-4878-8802-88f8548a9a45\"},\"addresses\":[],\"attributes\":{\"location\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"vht_name\":\"Dhdb\",\"vht_phone\":\"0853998535\",\"school_name\":\"Jambula Girls School\",\"date_removed\":\"2018-06-05\",\"school_class\":\"P4\",\"dose_one_date\":\"2018-05-30\",\"caretaker_name\":\"Bomo Nsuta\",\"caretaker_phone\":\"0232893892\"},\"firstName\":\"Stephanie\",\"lastName\":\"Don\",\"birthdate\":1180396800000,\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{},\"_id\":\"129eaa1b-1885-4400-8329-0266d3eb2b6a\",\"_rev\":\"v9\"}]";

    @Before
    public void setUp() {
        model = new AdvancedSearchModel();
    }

    @Test
    public void testCreateLocalEditMap() {
        String firstName = "first_name";
        String lastName = "last_name";
        String ancId = "anc_id";
        String edd = "edd";
        String dob = "dob";
        String phoneNumber = "phone_number";
        String alternateContact = "alternate_contact";
        boolean isLocal = true;

        Map<String, String> editMap = model.createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);

        Assert.assertNotNull(editMap);
        Assert.assertEquals(7, editMap.size());

        Assert.assertEquals(firstName, editMap.get(DBConstantsUtils.KeyUtils.FIRST_NAME));
        Assert.assertEquals(lastName, editMap.get(DBConstantsUtils.KeyUtils.LAST_NAME));
        Assert.assertEquals(ancId, editMap.get(DBConstantsUtils.KeyUtils.ANC_ID));
        Assert.assertEquals(edd, editMap.get(DBConstantsUtils.KeyUtils.EDD));
        Assert.assertEquals(dob, editMap.get(DBConstantsUtils.KeyUtils.DOB));
        Assert.assertEquals(phoneNumber, editMap.get(DBConstantsUtils.KeyUtils.PHONE_NUMBER));
        Assert.assertEquals(alternateContact, editMap.get(DBConstantsUtils.KeyUtils.ALT_NAME));

    }

    @Test
    public void testCreateGlobalEditMap() {
        String firstName = "first_name";
        String lastName = "last_name";
        String ancId = "anc_id";
        String edd = "edd";
        String dob = "dob";
        String phoneNumber = "phone_number";
        String alternateContact = "alternate_contact";
        boolean isLocal = false;

        Map<String, String> editMap = model.createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);

        Assert.assertNotNull(editMap);
        Assert.assertEquals(7, editMap.size());

        Assert.assertEquals(firstName, editMap.get(AdvancedSearchModel.GLOBAL_FIRST_NAME));
        Assert.assertEquals(lastName, editMap.get(AdvancedSearchModel.GLOBAL_LAST_NAME));
        Assert.assertEquals(AdvancedSearchModel.ANC_ID + ":" + ancId, editMap.get(AdvancedSearchModel.GLOBAL_IDENTIFIER));
        Assert.assertEquals(AdvancedSearchModel.EDD_ATTR + ":" + edd, editMap.get(AdvancedSearchModel.GLOBAL_ATTRIBUTE));
        Assert.assertEquals(dob, editMap.get(AdvancedSearchModel.GLOBAL_BIRTH_DATE));
        Assert.assertEquals(phoneNumber, editMap.get(AdvancedSearchModel.PHONE_NUMBER));
        Assert.assertEquals(alternateContact, editMap.get(AdvancedSearchModel.ALT_CONTACT_NAME));

    }

    @Test
    public void testCreateLocalEditMapWithMissingFields() {
        String lastName = "last_name";
        String edd = "edd";
        String phoneNumber = "phone_number";
        boolean isLocal = true;

        Map<String, String> editMap = model.createEditMap("", lastName, null, edd, "", phoneNumber, null, isLocal);

        Assert.assertNotNull(editMap);
        Assert.assertEquals(3, editMap.size());

        Assert.assertEquals(lastName, editMap.get(DBConstantsUtils.KeyUtils.LAST_NAME));
        Assert.assertEquals(edd, editMap.get(DBConstantsUtils.KeyUtils.EDD));
        Assert.assertEquals(phoneNumber, editMap.get(DBConstantsUtils.KeyUtils.PHONE_NUMBER));

    }

    @Test
    public void testCreateGlobalEditMapWithMissingFields() {
        String firstName = "first_name";
        String ancId = "anc_id";
        String dob = "dob";
        String alternateContact = "alternate_contact";
        boolean isLocal = false;

        Map<String, String> editMap = model.createEditMap(firstName, null, ancId, "", dob, null, alternateContact, isLocal);

        Assert.assertNotNull(editMap);
        Assert.assertEquals(4, editMap.size());

        Assert.assertEquals(firstName, editMap.get(AdvancedSearchModel.GLOBAL_FIRST_NAME));
        Assert.assertEquals(AdvancedSearchModel.ANC_ID + ":" + ancId, editMap.get(AdvancedSearchModel.GLOBAL_IDENTIFIER));
        Assert.assertEquals(dob, editMap.get(AdvancedSearchModel.GLOBAL_BIRTH_DATE));
        Assert.assertEquals(alternateContact, editMap.get(AdvancedSearchModel.ALT_CONTACT_NAME));

    }

    @Test
    public void testCreateSearchString() {
        String firstName = "first_name";
        String lastName = "last_name";
        String ancId = "anc_id";
        String edd = "edd";
        String dob = "dob";
        String phoneNumber = "phone_number";
        String alternateContact = "alternate_contact";

        String searchString = model.createSearchString(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);
        Assert.assertEquals("First name: first_name; Last name: last_name; ANC ID: anc_id; Edd: edd; Dob: dob; Mobile phone number: phone_number; Alternate contact name: alternate_contact", searchString);
    }

    @Test
    public void testCreateSearchStringWithOneField() {
        String firstName = "first_name";
        String searchString = model.createSearchString(firstName, null, null, null, "", "", "");
        Assert.assertEquals("First name: first_name", searchString);
    }


    @Test
    public void testCreateSearchStringWithSomeFields() {
        String lastName = "last_name";
        String edd = "edd";
        String phoneNumber = "phone_number";

        String searchString = model.createSearchString(null, lastName, null, edd, "", phoneNumber, "");
        Assert.assertEquals("Last name: last_name; Edd: edd; Mobile phone number: phone_number", searchString);
    }

    @Test
    public void testMainCondition() {

        String firstName = "first_name";
        String lastName = "last_name";
        String ancId = "anc_id";
        String edd = "edd";
        String dob = "dob";
        String phoneNumber = "phone_number";
        String alternateContact = "alternate_contact";

        Map<String, String> editMap = model.createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, true);
        String mainCondition = model.getMainConditionString(editMap);

        Assert.assertEquals(" first_name Like '%first_name%' AND last_name Like '%last_name%' AND register_id Like '%anc_id%' AND edd Like '%edd%' AND dob Like '%dob%' AND phone_number Like '%phone_number%' AND alt_name Like '%alternate_contact%' ", mainCondition);
    }

    @Test
    public void testMainConditionForBlankFields() {

        Map<String, String> editMap = model.createEditMap(null, "", null, "", null, "", null, true);
        String mainCondition = model.getMainConditionString(editMap);

        Assert.assertEquals("", mainCondition);
    }

    @Test
    public void testMainConditionWithSomeFields() {

        String firstName = "first_name";
        String ancId = "anc_id";
        String dob = "dob";
        String alternateContact = "alternate_contact";

        Map<String, String> editMap = model.createEditMap(firstName, null, ancId, "", dob, null, alternateContact, true);
        String mainCondition = model.getMainConditionString(editMap);

        Assert.assertEquals(" first_name Like '%first_name%' AND register_id Like '%anc_id%' AND dob Like '%dob%' AND alt_name Like '%alternate_contact%' ", mainCondition);
    }

    @Test
    public void testCreateMatrixCursor() {
        Response<String> response = new Response<>(ResponseStatus.success, payload);
        AdvancedMatrixCursor matrixCursor = model.createMatrixCursor(response);

        Assert.assertNotNull(matrixCursor);
        Assert.assertEquals(18, matrixCursor.getCount());

    }

    @Test
    public void testCreateMatrixCursorWithBlankResponse() {
        AdvancedMatrixCursor matrixCursor = model.createMatrixCursor(null);

        Assert.assertNotNull(matrixCursor);
        Assert.assertEquals(0, matrixCursor.getCount());

        matrixCursor = model.createMatrixCursor(null);

        Assert.assertNotNull(matrixCursor);
        Assert.assertEquals(0, matrixCursor.getCount());

    }

    @Test
    public void testCreateMatrixCursorWithFailedResponse() {
        Response<String> response = new Response<>(ResponseStatus.failure, payload);
        AdvancedMatrixCursor matrixCursor = model.createMatrixCursor(response);

        Assert.assertNotNull(matrixCursor);
        Assert.assertEquals(0, matrixCursor.getCount());

    }


}

