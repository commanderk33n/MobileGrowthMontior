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
public class Girl1HalfYearsOldTest {

    private int year, month, day;

    @Before
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        calendar.add(Calendar.MONTH, -6);
        calendar.getTime();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Rule
    public ActivityTestRule<MainView> mActivityTestRule = new ActivityTestRule<>(MainView.class);

    @Test
    public void girl1HalfYearsOldTest() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.et_firstname), isDisplayed()));
        appCompatEditText.perform(replaceText("Girls1.5YearsOld"));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.et_lastname), isDisplayed()));
        appCompatEditText2.perform(replaceText("T"));

        ViewInteraction appCompatRadioButton2 = onView(
                allOf(withId(R.id.rb_sex_female), withText("Weiblich"),
                        withParent(withId(R.id.rg_gender)),
                        isDisplayed()));
        appCompatRadioButton2.perform(click());

        onView(withId(R.id.dp_birthday)).perform(setDate(year,month,day));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.save_profile), withText("Speichern"), withContentDescription("Speichern"), isDisplayed()));
        actionMenuItemView.perform(click());

        onView(withId(R.id.rv_profileList)).perform(swipeUp());

        onView(allOf(withId(R.id.tv_name),(withText("Girls1.5YearsOld T, ")))).perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.et_height), withText("0.0"), isDisplayed()));
        appCompatEditText4.perform(replaceText("70"));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.et_weight), withText("0.0"), isDisplayed()));
        appCompatEditText5.perform(replaceText("5"));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction textView = onView(allOf(withId(R.id.tv_bmi)));
        textView.check(matches(withText("Der BMI ist 10,20.")));

        ViewInteraction textView2 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView2.check(matches(withText("Das bedeutet extremes Untergewicht!")));

        ViewInteraction textView3 = onView(allOf(withId(R.id.tv_height_category)));
        textView3.check(matches(withText("ist sehr klein.")));

        ViewInteraction textView4 = onView(allOf(withId(R.id.tv_weight_category)));
        textView4.check(matches(withText("Das bedeutet extremes Untergewicht!")));

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.et_height), withText("70"), isDisplayed()));
        appCompatEditText8.perform(replaceText("80"));


        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.et_weight), withText("5"), isDisplayed()));
        appCompatEditText13.perform(replaceText("10"));

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction textView5 = onView(allOf(withId(R.id.tv_bmi)));
        textView5.check(matches(withText("Der BMI ist 15,62.")));

        ViewInteraction textView6 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView6.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction textView8 = onView(allOf(withId(R.id.tv_height_category)));
        textView8.check(matches(withText("ist normal.")));

        ViewInteraction textView10 = onView(allOf(withId(R.id.tv_weight_category)));
        textView10.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction appCompatEditText24 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText24.perform(replaceText("90"));

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton7.perform(click());

        ViewInteraction textView11 = onView(allOf(withId(R.id.tv_bmi)));
        textView11.check(matches(withText("Der BMI ist 12,34.")));

        ViewInteraction textView12 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView12.check(matches(withText("Das bedeutet Untergewicht!")));

        ViewInteraction textView13 = onView(allOf(withId(R.id.tv_height_category)));
        textView13.check(matches(withText("ist groß.")));

    }
}
