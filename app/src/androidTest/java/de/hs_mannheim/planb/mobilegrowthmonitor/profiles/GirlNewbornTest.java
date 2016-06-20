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
public class GirlNewbornTest {

    private int year, month, day;

    @Rule
    public ActivityTestRule<MainView> mActivityTestRule = new ActivityTestRule<>(MainView.class);

    @Before
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.getTime();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Test
    public void girlNewbornTest() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.et_firstname), isDisplayed()));
        appCompatEditText.perform(replaceText("GirlNewbornTest"));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.et_lastname), isDisplayed()));
        appCompatEditText2.perform(replaceText("Y"));

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.rb_sex_female), withText("Weiblich"),
                        withParent(withId(R.id.rg_gender)),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        onView(withId(R.id.dp_birthday)).perform(setDate(year,month+1,day-1));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.save_profile), withText("Speichern"), withContentDescription("Speichern"), isDisplayed()));
        actionMenuItemView.perform(click());

        onView(withId(R.id.rv_profileList)).perform(swipeUp()).perform(swipeUp());

        onView(allOf(withId(R.id.tv_name), (withText("GirlNewbornTest Y, ")))).perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.et_height), withText("0.0"), isDisplayed()));
        appCompatEditText3.perform(click());

        ViewInteraction appCompatEditText4 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText4.perform(replaceText("30"));

        ViewInteraction appCompatEditText5 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText5.perform(replaceText("2"));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView = onView(allOf(withId(R.id.tv_bmi)));
        textView.check(matches(withText("Der BMI ist 22,22.")));

        ViewInteraction textView2 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView2.check(matches(withText("Das bedeutet extremes Übergewicht!")));

        ViewInteraction textView3 = onView(allOf(withId(R.id.tv_height_category)));
        textView3.check(matches(withText("ist sehr klein.")));

        ViewInteraction textView4 = onView(allOf(withId(R.id.tv_weight_category)));
        textView4.check(matches(withText("Das bedeutet extremes Untergewicht!")));

        ViewInteraction appCompatEditText7 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText7.perform(replaceText("49"));

        ViewInteraction appCompatEditText8 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText8.perform(replaceText("3.5"));

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction textView5 = onView(allOf(withId(R.id.tv_bmi)));
        textView5.check(matches(withText("Der BMI ist 14,57.")));

        ViewInteraction textView6 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView6.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction textView8 = onView(allOf(withId(R.id.tv_height_category)));
        textView8.check(matches(withText("ist normal.")));


        ViewInteraction textView10 = onView(allOf(withId(R.id.tv_weight_category)));
        textView10.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.et_height), withText("49"), isDisplayed()));
        appCompatEditText9.perform(replaceText(""));

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.et_height), isDisplayed()));
        appCompatEditText10.perform(click());

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.et_height), isDisplayed()));
        appCompatEditText11.perform(replaceText("59"));

        ViewInteraction appCompatEditText12 = onView(
                allOf(withId(R.id.et_weight), withText("3.5"), isDisplayed()));
        appCompatEditText12.perform(replaceText("8"));

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction textView11 = onView(allOf(withId(R.id.tv_bmi)));
        textView11.check(matches(withText("Der BMI ist 22,98.")));

        ViewInteraction textView12 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView12.check(matches(withText("Das bedeutet extremes Übergewicht!")));

        ViewInteraction textView14 = onView(allOf(withId(R.id.tv_height_category)));
        textView14.check(matches(withText("ist sehr groß.")));

        ViewInteraction textView16 = onView(allOf(withId(R.id.tv_weight_category)));
        textView16.check(matches(withText("Das bedeutet extremes Übergewicht!")));

    }
}
