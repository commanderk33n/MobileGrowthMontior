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

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Boy4YearsOldTest {

    private int year;
    private int month;
    private int day;

    @Rule
    public ActivityTestRule<MainView> mActivityTestRule = new ActivityTestRule<>(MainView.class);

    @Before
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -4);
        calendar.add(Calendar.MONTH, -1);
        calendar.getTime();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Test
    public void boy4YearsOldTest() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.et_firstname), isDisplayed()));
        appCompatEditText.perform(replaceText("Boy4YearsOld"));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.et_lastname), isDisplayed()));
        appCompatEditText2.perform(replaceText("Z"));

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

        onView(allOf(withId(R.id.tv_name),(withText("Boy4YearsOld Z, ")))).perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText3 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText3.perform(click());

        ViewInteraction appCompatEditText4 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText4.perform(replaceText("95.0"));

        ViewInteraction appCompatEditText5 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText5.perform(replaceText("19.0"));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView = onView(allOf(withId(R.id.tv_bmi)));
        textView.check(matches(withText("Der BMI ist 21,05.")));

        ViewInteraction textView2 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView2.check(matches(withText("Das bedeutet extremes Übergewicht!")));

        ViewInteraction textView3 = onView(allOf(withId(R.id.tv_height_category)));
        textView3.check(matches(withText("ist klein.")));

        ViewInteraction textView4 = onView(allOf(withId(R.id.tv_weight_category)));
        textView4.check(matches(withText("Das bedeutet Normalgewicht!")));

        pressBack();

        pressBack();

        ViewInteraction textView5 = onView(allOf(withId(R.id.tv_height)));
        textView5.check(matches(withText("0.95 m")));

        ViewInteraction textView6 = onView(allOf(withId(R.id.tv_weight)));
        textView6.check(matches(withText("19.0 kg")));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatEditText6 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText6.perform(click());


        ViewInteraction appCompatEditText9 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText9.perform(replaceText("103.0"));

        ViewInteraction appCompatEditText10 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText10.perform(replaceText("16.5"));

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction textView7 = onView(allOf(withId(R.id.tv_bmi)));
        textView7.check(matches(withText("Der BMI ist 15,55.")));

        ViewInteraction textView8 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView8.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction textView9 = onView(allOf(withId(R.id.tv_height_category)));
        textView9.check(matches(withText("ist normal.")));

        ViewInteraction textView10 = onView(allOf(withId(R.id.tv_weight_category)));
        textView10.check(matches(withText("Das bedeutet Normalgewicht!")));

        pressBack();
        pressBack();

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btn_measurements)));
        appCompatButton5.perform(click());

        ViewInteraction appCompatEditText13 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText13.perform(click());

        ViewInteraction appCompatEditText14 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText14.perform(replaceText("115.0"));

        ViewInteraction appCompatEditText15 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText15.perform(replaceText("23"));

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction textView11 = onView(allOf(withId(R.id.tv_bmi)));
        textView11.check(matches(withText("Der BMI ist 17,39.")));

        ViewInteraction textView12 = onView(allOf(withId(R.id.tv_height_category)));
        textView12.check(matches(withText("ist groß.")));

        ViewInteraction textView13 = onView(allOf(withId(R.id.tv_weight_category)));
        textView13.check(matches(withText("Das bedeutet Übergewicht!")));

        pressBack();
        pressBack();

        ViewInteraction textView14 = onView(allOf(withId(R.id.tv_height)));
        textView14.check(matches(withText("1.15 m")));

        ViewInteraction textView15 = onView(allOf(withId(R.id.tv_weight)));
        textView15.check(matches(withText("23.0 kg")));

    }
}
