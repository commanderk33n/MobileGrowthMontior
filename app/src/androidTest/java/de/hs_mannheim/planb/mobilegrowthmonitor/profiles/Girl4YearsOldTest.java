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
public class Girl4YearsOldTest {

    private int year, month, day;

    @Before
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -4);
        calendar.getTime();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Rule
    public ActivityTestRule<MainView> mActivityTestRule = new ActivityTestRule<>(MainView.class);

    @Test
    public void girl4YearsOldTest() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.et_firstname), isDisplayed()));
        appCompatEditText.perform(replaceText("Girl4YearsOld"));

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.et_lastname), isDisplayed()));
        appCompatEditText6.perform(replaceText("Z"));

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.rb_sex_female), withText("Weiblich"),
                        withParent(withId(R.id.rg_gender)),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        onView(withId(R.id.dp_birthday)).perform(setDate(year,month+1,day));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.save_profile), withText("Speichern"), withContentDescription("Speichern"), isDisplayed()));
        actionMenuItemView.perform(click());

        onView(withId(R.id.rv_profileList)).perform(swipeUp()).perform(swipeUp());

        onView(allOf(withId(R.id.tv_name), (withText("Girl4YearsOld Z, ")))).perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText8 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText8.perform(replaceText("90"));

        ViewInteraction appCompatEditText9 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText9.perform(replaceText("12"));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction textView = onView(allOf(withId(R.id.tv_bmi)));
        textView.check(matches(withText("Der BMI ist 14,81.")));

        ViewInteraction textView3 = onView(allOf(withId(R.id.tv_height_category)));
        textView3.check(matches(withText("ist klein.")));

        ViewInteraction textView5 = onView(allOf(withId(R.id.tv_weight_category)));
        textView5.check(matches(withText("Das bedeutet Untergewicht!")));

        ViewInteraction appCompatEditText12 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText12.perform(replaceText("102"));

        ViewInteraction appCompatEditText13 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText13.perform(replaceText("16"));

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction textView6 = onView(allOf(withId(R.id.tv_bmi)));
        textView6.check(matches(withText("Der BMI ist 15,37.")));

        ViewInteraction textView7 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView7.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction textView9 = onView(allOf(withId(R.id.tv_height_category)));
        textView9.check(matches(withText("ist normal.")));

        ViewInteraction textView11 = onView(allOf(withId(R.id.tv_weight_category)));
        textView11.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction appCompatEditText14 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText14.perform(replaceText("113"));

        ViewInteraction appCompatEditText15 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText15.perform(replaceText("20"));


        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction textView12 = onView(allOf(withId(R.id.tv_bmi)));
        textView12.check(matches(withText("Der BMI ist 15,66.")));

        ViewInteraction textView13 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView13.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction textView15 = onView(allOf(withId(R.id.tv_height_category)));
        textView15.check(matches(withText("ist groß.")));

        ViewInteraction textView17 = onView(allOf(withId(R.id.tv_weight_category)));
        textView17.check(matches(withText("Das bedeutet Normalgewicht!")));

    }
}
