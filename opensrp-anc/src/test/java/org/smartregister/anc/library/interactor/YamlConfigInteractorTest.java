package org.smartregister.anc.library.interactor;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({PatientRepositoryHelper.class, AncLibrary.class, Utils.class})
public class YamlConfigInteractorTest {

//    private final String baseEntityId = UUID.randomUUID().toString();
//    @Rule
//    public MockitoRule rule = MockitoJUnit.rule();
//    private ContactSummarySendContract.Interactor summaryInteractor;
//    @Captor
//    private ArgumentCaptor<Map<String, String>> detailsArgumentCaptor;
//    @Captor
//    private ArgumentCaptor<List<ContactSummaryModel>> upcomingContactsCaptor;
//    @Captor
//    private ArgumentCaptor<Integer> integerArgumentCaptor;
//    @Mock
//    private AncLibrary ancLibrary;
//
//    @Mock
//    private Context context;
//
//    @Mock
//    private DetailsRepository detailsRepository;
//
//    @Mock
//    private PreviousContactRepositoryHelper previousContactRepositoryHelper;
//
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//        summaryInteractor = new ContactSummaryInteractor(new AppExecutors(
//                Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(),
//                Executors.newSingleThreadExecutor()));
//    }
//
//    @Test
//    public void testFetchWomanDetails() {
//
//        ContactSummarySendContract.InteractorCallback callBack = Mockito.mock(
//                ContactSummarySendContract.InteractorCallback.class);
//
//        String firstName = "Elly";
//        String lastName = "Smith";
//
//        Map<String, String> details = new HashMap<>();
//        details.put(DBConstantsUtils.KeyUtils.FIRST_NAME, firstName);
//        details.put(DBConstantsUtils.KeyUtils.LAST_NAME, lastName);
//
//        PowerMockito.mockStatic(AncLibrary.class);
//
//        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
//
//        PowerMockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());
//
//        PowerMockito.mockStatic(PatientRepositoryHelper.class);
//
//        PowerMockito.when(PatientRepositoryHelper.getWomanProfileDetails(baseEntityId))
//                .thenReturn(details);
//
//        summaryInteractor.fetchWomanDetails(baseEntityId, callBack);
//
//        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT)).onWomanDetailsFetched(detailsArgumentCaptor.capture());
//
//        Assert.assertEquals(details, detailsArgumentCaptor.getValue());
//    }
//
//    @Test
//    public void testFetchUpcomingContactsWithReferralContacts() {
//        ContactSummarySendContract.InteractorCallback callBack = PowerMockito.mock(
//                ContactSummarySendContract.InteractorCallback.class);
//        ContactSummarySendContract.Interactor summaryInteractorSpy = Mockito.spy(summaryInteractor);
//
//        final List<String> contactSchedule = new ArrayList<>();
//        contactSchedule.add("10");
//        contactSchedule.add("20");
//        contactSchedule.add("30");
//        contactSchedule.add("40");
//
//        String contactScheduleList = "[10, 20, 30, 40]";
//
//        Map<String, String> details = new HashMap<>();
//        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "2");
//        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE, "2017-04-09");
//        details.put(DBConstantsUtils.KeyUtils.EDD, "2017-04-10");
//        details.put(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, "{ contact_schedule : \"" + contactSchedule.toString() + "\" }");
//
//        Facts facts = new Facts();
//        facts.put(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, contactScheduleList);
//
//        PowerMockito.mockStatic(AncLibrary.class);
//        PowerMockito.mockStatic(PatientRepositoryHelper.class);
//        PowerMockito.mockStatic(Utils.class);
//
//        PowerMockito.when(PatientRepositoryHelper.getWomanProfileDetails(baseEntityId)).thenReturn(details);
//        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
//
//        PowerMockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());
//        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
//        PowerMockito.when(context.getStringResource(R.string.contact_number)).thenReturn("Contact %s number");
//        PowerMockito.when(ancLibrary.getDetailsRepository()).thenReturn(detailsRepository);
//        PowerMockito.when(detailsRepository.getAllDetailsForClient(baseEntityId)).thenReturn(details);
//
//        PowerMockito.when(ancLibrary.getPreviousContactRepositoryHelper()).thenReturn(previousContactRepositoryHelper);
//        PowerMockito.when(previousContactRepositoryHelper.getImmediatePreviousSchedule(baseEntityId, "1")).thenReturn(facts);
//        PowerMockito.when(Utils.getListFromString(contactScheduleList)).thenReturn(contactSchedule);
//
//        summaryInteractorSpy.fetchUpcomingContacts(baseEntityId, "-2", callBack);
//
////        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT))
////                .onUpcomingContactsFetched(upcomingContactsCaptor.capture(), integerArgumentCaptor.capture());
//
////        Assert.assertNotNull(upcomingContactsCaptor.getValue());
////        Assert.assertEquals(contactSchedule.size(), upcomingContactsCaptor.getValue().size());
//
//        Assert.assertNotNull(integerArgumentCaptor.getValue());
//        Assert.assertEquals(-2, integerArgumentCaptor.getValue().intValue());
//
////        Assert.assertEquals("Contact 2 number", upcomingContactsCaptor.getValue().get(0).getContactName());
////        Assert.assertEquals("Contact 5 number",
////                upcomingContactsCaptor.getValue().get(contactSchedule.size() - 1).getContactName());
//    }
//
//    @Test
//    public void testFetchUpcomingContactsWithNoReferralContacts() {
//        ContactSummarySendContract.InteractorCallback callBack = PowerMockito.mock(
//                ContactSummarySendContract.InteractorCallback.class);
//        ContactSummarySendContract.Interactor summaryInteractorSpy = Mockito.spy(summaryInteractor);
//
//        final List<String> contactSchedule = new ArrayList<>();
//        contactSchedule.add("10");
//        contactSchedule.add("20");
//        contactSchedule.add("30");
//        contactSchedule.add("40");
//
//        String contactScheduleList = "[10, 20, 30, 40]";
//
//        Map<String, String> details = new HashMap<>();
//        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "2");
//        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE, "2017-04-09");
//        details.put(DBConstantsUtils.KeyUtils.EDD, "2017-04-10");
//        details.put(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, "{ contact_schedule : \"" + contactSchedule.toString() + "\" }");
//
//        Facts facts = new Facts();
//        facts.put(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, contactScheduleList);
//
//        PowerMockito.mockStatic(AncLibrary.class);
//        PowerMockito.mockStatic(PatientRepositoryHelper.class);
//        PowerMockito.mockStatic(Utils.class);
//
//        PowerMockito.when(PatientRepositoryHelper.getWomanProfileDetails(baseEntityId)).thenReturn(details);
//
//        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
//
//        PowerMockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());
//        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
//        PowerMockito.when(context.getStringResource(R.string.contact_number)).thenReturn("Contact %s number");
//        PowerMockito.when(ancLibrary.getDetailsRepository()).thenReturn(detailsRepository);
//        PowerMockito.when(detailsRepository.getAllDetailsForClient(baseEntityId)).thenReturn(details);
//        PowerMockito.when(Utils.getListFromString(contactScheduleList)).thenReturn(contactSchedule);
//
//        summaryInteractorSpy.fetchUpcomingContacts(baseEntityId, "", callBack);
//
//        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT))
//                .onUpcomingContactsFetched(upcomingContactsCaptor.capture(), integerArgumentCaptor.capture());
//
//        Assert.assertNotNull(upcomingContactsCaptor.getValue());
//        Assert.assertEquals(contactSchedule.size(), upcomingContactsCaptor.getValue().size());
//
//        Assert.assertNotNull(integerArgumentCaptor.getValue());
//        Assert.assertEquals(1, integerArgumentCaptor.getValue().intValue());
//
//        Assert.assertEquals("Contact 2 number", upcomingContactsCaptor.getValue().get(0).getContactName());
//        Assert.assertEquals("Contact 5 number",
//                upcomingContactsCaptor.getValue().get(contactSchedule.size() - 1).getContactName());
//    }
//
//    @Test
//    public void testFetchUpcomingContactsWithNoReferralContactsAndContactSchedule() {
//        ContactSummarySendContract.InteractorCallback callBack = PowerMockito.mock(
//                ContactSummarySendContract.InteractorCallback.class);
//        ContactSummarySendContract.Interactor summaryInteractorSpy = Mockito.spy(summaryInteractor);
//
//        final List<String> contactSchedule = new ArrayList<>();
//        String contactScheduleList = "[10, 20, 30, 40]";
//
//        Map<String, String> details = new HashMap<>();
//        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "2");
//        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE, "2017-04-09");
//        details.put(DBConstantsUtils.KeyUtils.EDD, "2017-04-10");
//
//        Facts facts = new Facts();
//        facts.put(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, contactScheduleList);
//
//        PowerMockito.mockStatic(AncLibrary.class);
//        PowerMockito.mockStatic(PatientRepositoryHelper.class);
//        PowerMockito.mockStatic(Utils.class);
//
//        PowerMockito.when(PatientRepositoryHelper.getWomanProfileDetails(baseEntityId)).thenReturn(details);
//
//        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
//
//        PowerMockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());
//
//        PowerMockito.when(ancLibrary.getDetailsRepository()).thenReturn(detailsRepository);
//        PowerMockito.when(detailsRepository.getAllDetailsForClient(baseEntityId)).thenReturn(details);
//        PowerMockito.when(Utils.getListFromString(contactScheduleList)).thenReturn(contactSchedule);
//
//        summaryInteractorSpy.fetchUpcomingContacts(baseEntityId, "", callBack);
//
//        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT))
//                .onUpcomingContactsFetched(upcomingContactsCaptor.capture(), integerArgumentCaptor.capture());
//
//        Assert.assertNotNull(upcomingContactsCaptor.getValue());
//        Assert.assertEquals(contactSchedule.size(), upcomingContactsCaptor.getValue().size());
//
//        Assert.assertNotNull(integerArgumentCaptor.getValue());
//        Assert.assertEquals(1, integerArgumentCaptor.getValue().intValue());
//
//        Assert.assertEquals(0, upcomingContactsCaptor.getValue().size());
//    }

}
