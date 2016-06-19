package de.hs_mannheim.planb.mobilegrowthmonitor.profiles;

//espresso test fails

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
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

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NewbornBoyTestBMI {
    private int year, month, day;

    @Before
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.getTime();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Rule
    public ActivityTestRule<MainView> mActivityTestRule = new ActivityTestRule<>(MainView.class);


    @Test
    public void newbornBoyTestBMI() {
        ViewInteraction floatingActionButton = onView(
                allOf(ViewMatchers.withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());


        ViewInteraction appCompatEditText4 = onView((withId(R.id.et_firstname)));
        appCompatEditText4.perform(replaceText("BoyNewborn"));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.et_lastname), isDisplayed()));
        appCompatEditText5.perform(replaceText("T"));

        pressBack();

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.rb_sex_male), withText("Männlich"),
                        withParent(withId(R.id.rg_gender)),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        onView(withId(R.id.dp_birthday)).perform(setDate(year, month, day));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.save_profile), withText("Speichern"), withContentDescription("Speichern"), isDisplayed()));
        actionMenuItemView.perform(click());

        onView(withId(R.id.rv_profileList)).perform(swipeUp()).perform(swipeUp()).perform(swipeUp());

        onView(allOf(withId(R.id.tv_name), (withText("BoyNewborn T, ")))).perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText6 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText6.perform(click());

        ViewInteraction appCompatEditText7 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText7.perform(replaceText("50"));

        ViewInteraction appCompatEditText8 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText8.perform(replaceText("3.5"));

        pressBack();

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView = onView(allOf(withId(R.id.tv_bmi)));
        textView.check(matches(withText("Der BMI ist 14,00.")));

        ViewInteraction textView2 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView2.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction textView3 = onView(allOf(withId(R.id.tv_height_category)));
        textView3.check(matches(withText("ist normal."))); // until 18.06.

        ViewInteraction textView4 = onView(allOf(withId(R.id.tv_weight_category)));
        textView4.check(matches(withText("Das bedeutet Normalgewicht!")));

        pressBack();

        ViewInteraction textView5 = onView((withId(R.id.tv_height)));
        textView5.check(matches(withText("0.5 m")));

        ViewInteraction textView6 = onView(allOf(withId(R.id.tv_weight)));
        textView6.check(matches(withText("3.5 kg")));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatEditText9 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText9.perform(replaceText("2.2"));

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction textView7 = onView(allOf(withId(R.id.tv_bmi)));
        textView7.check(matches(withText("Der BMI ist 8,80.")));

        ViewInteraction textView8 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView8.check(matches(withText("Das bedeutet extremes Untergewicht!")));

        ViewInteraction textView9 = onView(allOf(withId(R.id.tv_height_category)));
        textView9.check(matches(withText("ist normal.")));

        ViewInteraction textView10 = onView(allOf(withId(R.id.tv_weight_category)));
        textView10.check(matches(withText("Das bedeutet Untergewicht!")));

        pressBack();

        ViewInteraction textView11 = onView(allOf(withId(R.id.tv_height)));
        textView11.check(matches(withText("0.5 m")));

        ViewInteraction textView12 = onView(allOf(withId(R.id.tv_weight)));
        textView12.check(matches(withText("2.2 kg")));

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction appCompatEditText10 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText10.perform(replaceText("5.2"));

        ViewInteraction appCompatEditText11 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText11.perform(replaceText("55"));


        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction textView13 = onView(allOf(withId(R.id.tv_bmi)));
        textView13.check(matches(withText("Der BMI ist 17,19.")));

        ViewInteraction textView14 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView14.check(matches(withText("Das bedeutet Übergewicht!")));

        ViewInteraction textView15 = onView(allOf(withId(R.id.tv_weight_category)));
        textView15.check(matches(withText("Das bedeutet extremes Übergewicht!")));

        ViewInteraction textView16 = onView(allOf(withId(R.id.tv_height_category)));
        textView16.check(matches(withText("ist groß.")));

    }
}
