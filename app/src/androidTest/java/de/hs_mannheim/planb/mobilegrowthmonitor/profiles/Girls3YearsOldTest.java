package de.hs_mannheim.planb.mobilegrowthmonitor.profiles;

//finished

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.PickerActions.setDate;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Girls3YearsOldTest {

    private int year, month, day;

    @Before
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -3);
        calendar.getTime();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Rule
    public ActivityTestRule<MainView> mActivityTestRule = new ActivityTestRule<>(MainView.class);

    @Test
    public void girls3YearsOldTest() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.et_firstname), isDisplayed()));
        appCompatEditText.perform(replaceText("Girl3YearsOl"));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.et_lastname), isDisplayed()));
        appCompatEditText2.perform(replaceText("L"));

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.et_firstname), withText("Girl3YearsOl"), isDisplayed()));
        appCompatEditText3.perform(replaceText("Girl3YearsOld"));

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.rb_sex_female), withText("Weiblich"),
                        withParent(withId(R.id.rg_gender)),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        onView(withId(R.id.dp_birthday)).perform(setDate(year, month+1, day));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.save_profile), withText("Speichern"), withContentDescription("Speichern"), isDisplayed()));
        actionMenuItemView.perform(click());

        onView(withId(R.id.rv_profileList)).perform(swipeUp()).perform(swipeUp());

        onView(allOf(withId(R.id.tv_name),(withText("Girl3YearsOld L, ")))).perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.et_height), withText("0.0"), isDisplayed()));
        appCompatEditText4.perform(replaceText("85"));

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.et_weight), withText("0.0"), isDisplayed()));
        appCompatEditText7.perform(replaceText("12"));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView = onView(allOf(withId(R.id.tv_bmi)));
        textView.check(matches(withText("Der BMI ist 16,60.")));

        ViewInteraction textView2 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView2.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction textView3 = onView(allOf(withId(R.id.tv_height_category)));
        textView3.check(matches(withText("ist klein.")));

        ViewInteraction textView5 = onView(allOf(withId(R.id.tv_weight_category)));
        textView5.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction appCompatEditText9 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText9.perform(replaceText("95"));

        ViewInteraction appCompatEditText14 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText14.perform(replaceText("14"));

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction textView6 = onView(allOf(withId(R.id.tv_bmi)));
        textView6.check(matches(withText("Der BMI ist 15,51.")));

        ViewInteraction textView7 = onView(allOf(withId(R.id.tv_height_category)));
        textView7.check(matches(withText("ist normal.")));

        ViewInteraction textView8 = onView(allOf(withId(R.id.tv_weight_category)));
        textView8.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction appCompatEditText15 = onView(
                allOf(withId(R.id.et_weight), withText("14"), isDisplayed()));
        appCompatEditText15.perform(replaceText("19"));

        ViewInteraction appCompatEditText16 = onView(
                allOf(withId(R.id.et_height), withText("95"), isDisplayed()));
        appCompatEditText16.perform(replaceText("110"));

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction textView9 = onView(allOf(withId(R.id.tv_bmi)));
        textView9.check(matches(withText("Der BMI ist 15,70.")));

        ViewInteraction textView10 = onView(allOf(withId(R.id.tv_height_category)));
        textView10.check(matches(withText("ist sehr groß.")));

        ViewInteraction textView11 = onView(allOf(withId(R.id.tv_weight_category)));
        textView11.check(matches(withText("Das bedeutet Übergewicht!")));

    }
}
