package de.hs_mannheim.planb.mobilegrowthmonitor.profiles;

//Finished

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
public class Girl5YearsOldTest {

    private int year, month, day;

    @Before
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -5);
        calendar.getTime();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Rule
    public ActivityTestRule<MainView> mActivityTestRule = new ActivityTestRule<>(MainView.class);

    @Test
    public void girl5YearsOldTest() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.et_firstname), isDisplayed()));
        appCompatEditText.perform(replaceText("Girl5YearsOld"));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.et_lastname), isDisplayed()));
        appCompatEditText2.perform(replaceText("Z"));

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

        onView(allOf(withId(R.id.tv_name), (withText("Girl5YearsOld Z, ")))).perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText3 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText3.perform(click());

        ViewInteraction appCompatEditText4 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText4.perform(replaceText("97"));

        ViewInteraction appCompatEditText5 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText5.perform(replaceText("16"));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView = onView(allOf(withId(R.id.tv_bmi)));
        textView.check(matches(withText("Der BMI ist 17,00.")));

        ViewInteraction textView2 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView2.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction textView4 = onView(allOf(withId(R.id.tv_height_category)));
        textView4.check(matches(withText("ist klein.")));

        ViewInteraction textView6 = onView(allOf(withId(R.id.tv_weight_category)));
        textView6.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction appCompatEditText7 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText7.perform(replaceText("109"));

        ViewInteraction appCompatEditText8 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText8.perform(replaceText("18"));

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction textView7 = onView(allOf(withId(R.id.tv_bmi)));
        textView7.check(matches(withText("Der BMI ist 15,15.")));

        ViewInteraction textView8 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView8.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction textView10 = onView(allOf(withId(R.id.tv_height_category)));
        textView10.check(matches(withText("ist normal.")));

        ViewInteraction textView12 = onView(allOf(withId(R.id.tv_weight_category)));
        textView12.check(matches(withText("Das bedeutet Normalgewicht!")));

        ViewInteraction appCompatEditText17 = onView(allOf(withId(R.id.et_height)));
        appCompatEditText17.perform(replaceText("121"));

        ViewInteraction appCompatEditText18 = onView(allOf(withId(R.id.et_weight)));
        appCompatEditText18.perform(replaceText("18"));

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton9.perform(click());

        ViewInteraction textView14 = onView(allOf(withId(R.id.tv_bmi)));
        textView14.check(matches(withText("Der BMI ist 12,29.")));

        ViewInteraction textView15 = onView(allOf(withId(R.id.tv_bmi_category)));
        textView15.check(matches(withText("Das bedeutet Untergewicht!")));

        ViewInteraction textView17 = onView(allOf(withId(R.id.tv_height_category)));
        textView17.check(matches(withText("ist groß.")));

        ViewInteraction textView19 = onView(allOf(withId(R.id.tv_weight_category)));
        textView19.check(matches(withText("Das bedeutet Normal" +
                "gewicht!")));
    }
}
