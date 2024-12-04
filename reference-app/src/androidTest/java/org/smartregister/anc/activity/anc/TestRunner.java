package org.smartregister.anc.activity.anc;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                LoginActivityTest.class,
                HomePageActivityTest.class,
                ProfilePageTests.class,
                RegisterFamilyMemberPageTests.class,
                RemoveFamilyMemberTest.class,
                ContactsActivityTest.class,
                ProfileContainerTest.class,
                AdvancedSearchTests.class

        }
)




public class TestRunner {


}
