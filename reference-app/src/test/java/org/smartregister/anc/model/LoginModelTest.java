package org.smartregister.anc.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.login.model.BaseLoginModel;
import org.smartregister.view.contract.BaseLoginContract;

/**
 * Created by ndegwamartin on 28/06/2018.
 */
public class LoginModelTest extends BaseUnitTest {

    private BaseLoginContract.Model model;

    @Before
    public void setUp() {
        model = new BaseLoginModel();
    }


    @Test
    public void testIsUserLoggedOutShouldReturnTrue() {
        Assert.assertTrue(model.isUserLoggedOut());
    }

    @Test
    public void testGetOpenSRPContextShouldReturnValidValue() {
        Assert.assertNotNull(model.getOpenSRPContext());
    }

    @Test
    public void testIsPasswordValidShouldTrueWhenPasswordValidatesCorrectly() {
        boolean result = model.isPasswordValid(DUMMY_PASSWORD);
        Assert.assertTrue(result);
    }

    @Test
    public void testIsPasswordValidShouldFalseWhenPasswordValidationFails() {
        boolean result = model.isPasswordValid("");
        Assert.assertFalse(result);
        result = model.isPasswordValid("A");
        Assert.assertFalse(result);
    }

    @Test
    public void testIsEmptyUsernameShouldTrueWhenIsEmpty() {
        boolean result = model.isEmptyUsername("");
        Assert.assertTrue(result);
    }

    @Test
    public void testIsEmptyUsernameShouldFalseWhenUsernameIsNotEmpty() {
        boolean result = model.isEmptyUsername(DUMMY_USERNAME);
        Assert.assertFalse(result);
    }

}
