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
import static android.support.test.espresso.Espresso.pressBack;
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

/**
 * Created by Laura on 17.06.2016.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class Boy2MonthOldTest {


    private int year;
    private int month;
    private int day;

    @Rule
    public ActivityTestRule<MainView> mActivityTestRule = new ActivityTestRule<>(MainView.class);

    @Before
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2);
        calendar.getTime();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Test
    public void boy2MonthOldTest() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.et_firstname), isDisplayed()));
        appCompatEditText.perform(replaceText("Boy2MonthOld"));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.et_lastname), isDisplayed()));
        appCompatEditText2.perform(replaceText("P"));

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.rb_sex_male), withText("Männlich"),
                        withParent(withId(R.id.rg_gender)),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        onView(withId(R.id.dp_birthday)).perform(setDate(year, month+1, day));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.save_profile), withText("Speichern"), withContentDescription("Speichern"), isDisplayed()));
        actionMenuItemView.perform(click());

        onView(withId(R.id.rv_profileList)).perform(swipeUp());

        onView(allOf(withId(R.id.tv_name),(withText("Boy2MonthOld P, ")))).perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.et_height), withText("0.0"), isDisplayed()));
        appCompatEditText3.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.et_height), withText("0.0"), isDisplayed()));
        appCompatEditText4.perform(replaceText("58"));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.et_weight), withText("0.0"), isDisplayed()));
        appCompatEditText5.perform(replaceText("6"));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.tv_bmi), withText("Der BMI ist 17,83."), isDisplayed()));
        textView.check(matches(withText("Der BMI ist 17,83.")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tv_bmi_category)));
        textView2.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.tv_height_category)));
        textView3.check(matches(withText("ist normal.")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.tv_weight_category)));
        textView4.check(matches(withText("Das bedeutet Normalgewicht!")));

        pressBack();

        pressBack();

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.tv_height), withText("0.58 m"), isDisplayed()));
        textView5.check(matches(withText("0.58 m")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.tv_weight), withText("6.0 kg"), isDisplayed()));
        textView6.check(matches(withText("6.0 kg")));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.et_height), withText("58.0"), isDisplayed()));
        appCompatEditText9.perform(replaceText("66.0"));

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.et_weight), withText("6.0"), isDisplayed()));
        appCompatEditText10.perform(replaceText("4.0"));

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.tv_bmi), withText("Der BMI ist 9,18."), isDisplayed()));
        textView7.check(matches(withText("Der BMI ist 9,18.")));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.tv_bmi_category)));
        textView8.check(matches(withText("Das bedeutet extremes Untergewicht!")));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.tv_height_category)));
        textView9.check(matches(withText("ist sehr groß.")));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.tv_weight_category)));
        textView10.check(matches(withText("Das bedeutet Untergewicht!")));

        pressBack();

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btn_measurements)));
        appCompatButton5.perform(click());

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.et_height)));
        appCompatEditText13.perform(click());

        ViewInteraction appCompatEditText14 = onView(
                allOf(withId(R.id.et_height)));
        appCompatEditText14.perform(replaceText("54.0"));

        ViewInteraction appCompatEditText15 = onView(
                allOf(withId(R.id.et_weight), withText("4.0"), isDisplayed()));
        appCompatEditText15.perform(replaceText("5.0"));

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction textView11 = onView(
                allOf(withId(R.id.tv_bmi)));
        textView11.check(matches(withText("Der BMI ist 17,14.")));

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.tv_height_category)));
        textView12.check(matches(withText("ist klein.")));

        ViewInteraction textView13 = onView(
                allOf(withId(R.id.tv_weight_category)));
        textView13.check(matches(withText("Das bedeutet Normalgewicht!")));

        pressBack();
        pressBack();

        ViewInteraction textView14 = onView(
                allOf(withId(R.id.tv_height), withText("0.54 m"), isDisplayed()));
        textView14.check(matches(withText("0.54 m")));

        ViewInteraction textView15 = onView(
                allOf(withId(R.id.tv_weight), withText("5.0 kg"), isDisplayed()));
        textView15.check(matches(withText("5.0 kg")));

    }
}