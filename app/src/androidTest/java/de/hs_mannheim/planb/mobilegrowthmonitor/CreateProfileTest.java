package de.hs_mannheim.planb.mobilegrowthmonitor;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import de.hs_mannheim.planb.mobilegrowthmonitor.profiles.MainView;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.PickerActions.setDate;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateProfileTest {

    private int year;
    private int month;
    private int day;
    private String date;

    @Rule
    public ActivityTestRule<MainView> mActivityTestRule = new ActivityTestRule<>(MainView.class);

    @Before
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.getTime();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        if (month > 9 && day >9 ) {
            date = year + "-" + month + "-" + day;
        } else if (day < 10 && month < 10) {
            date = year + "-0" + month + "-0" + day;
        }else if (month < 10){
            date = year + "-0" + month + "-" + day;
        }else{
            date = year + "-" + month + "-0" + day;
        }
        System.out.println(year + "-" + month + "-" + day);
    }

    @Test
    public void createProfileTest() {
        ViewInteraction imageButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.et_firstname), isDisplayed()));
        appCompatEditText.perform(replaceText("Max"));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.et_lastname), isDisplayed()));
        appCompatEditText2.perform(replaceText("Muster"));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.save_profile), withText("Speichern"), withContentDescription("Speichern"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction radioGroup = onView(
                allOf(withId(R.id.rg_gender), isDisplayed()));
        radioGroup.check(matches(isDisplayed()));

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.rb_sex_male), withText("Männlich"),
                        withParent(withId(R.id.rg_gender)),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        pressBack();

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.save_profile), withText("Speichern"), withContentDescription("Speichern"), isDisplayed()));
        actionMenuItemView2.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.tv_name), withText("Max Muster, "), isDisplayed()));
        textView.check(matches(withText("Max Muster, ")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tv_bday), withText("6 Jahre alt"), isDisplayed()));
        textView2.check(matches(withText("6 Jahre alt")));

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction actionMenuItemView3 = onView(
                allOf(withId(R.id.save_profile), withText("Speichern"), withContentDescription("Speichern"), isDisplayed()));
        actionMenuItemView3.perform(click());

        onView(withId(R.id.et_firstname)).check(matches(isDisplayed()));

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.et_firstname), isDisplayed()));
        appCompatEditText3.perform(replaceText("Maxi"));

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.et_lastname), isDisplayed()));
        appCompatEditText4.perform(replaceText("Muster"));

        ViewInteraction appCompatRadioButton2 = onView(
                allOf(withId(R.id.rb_sex_female), withText("Weiblich"),
                        withParent(withId(R.id.rg_gender)),
                        isDisplayed()));
        appCompatRadioButton2.perform(click());

        pressBack();

        onView(withId(R.id.dp_birthday)).perform(setDate(1990, 9, 15));

        ViewInteraction actionMenuItemView4 = onView(
                allOf(withId(R.id.save_profile), withText("Speichern"), withContentDescription("Speichern"), isDisplayed()));
        actionMenuItemView4.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.tv_name), withText("Maxi Muster, "), isDisplayed()));
        textView3.check(matches(withText("Maxi Muster, ")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.tv_bday), withText("25 Jahre alt"), isDisplayed()));
        textView4.check(matches(withText("25 Jahre alt")));

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.rv_profileList), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.tv_firstname), withText("Max,"), isDisplayed()));
        textView5.check(matches(withText("Max,")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.tv_age), withText("6"), isDisplayed()));
        textView6.check(matches(withText("6")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.iv_gender), isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.tv_date_last_measurement), withText("Datum"), isDisplayed()));
        textView7.check(matches(withText("Datum")));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.tv_height), withText("in m"), isDisplayed()));
        textView8.check(matches(withText("in m")));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.tv_weight), withText("in kg"), isDisplayed()));
        textView9.check(matches(withText("in kg")));

        pressBack();

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.rv_profileList), isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(1, click()));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.tv_firstname), withText("Maxi,"), isDisplayed()));
        textView10.check(matches(withText("Maxi,")));

        ViewInteraction textView11 = onView(
                allOf(withId(R.id.tv_age), withText("25"), isDisplayed()));
        textView11.check(matches(withText("25")));

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.iv_gender), isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.tv_date_last_measurement), withText("Datum"), isDisplayed()));
        textView12.check(matches(withText("Datum")));

        ViewInteraction textView13 = onView(
                allOf(withId(R.id.tv_height), withText("in m"), isDisplayed()));
        textView13.check(matches(withText("in m")));

        ViewInteraction textView14 = onView(
                allOf(withId(R.id.tv_weight), withText("in kg"), isDisplayed()));
        textView14.check(matches(withText("in kg")));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.et_height), withText("0.0"), isDisplayed()));
        appCompatEditText5.perform(click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.et_height), withText("0.0"), isDisplayed()));
        appCompatEditText6.perform(replaceText("165"));

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.et_weight), withText("0.0"), isDisplayed()));
        appCompatEditText7.perform(replaceText("55"));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton2.perform(click());

        pressBack();

        ViewInteraction textView15 = onView(
                allOf(withId(R.id.tv_bmi), isDisplayed()));
        textView15.check(matches(isDisplayed()));

        ViewInteraction textView16 = onView((withId(R.id.tv_height_category)));
        textView16.check(matches(isDisplayed()));

        ViewInteraction textView17 = onView(
                allOf(withId(R.id.tv_weight_category), isDisplayed()));
        textView17.check(matches(isDisplayed()));

        pressBack();

        ViewInteraction textView18 = onView(allOf(withId(R.id.tv_date_last_measurement), isDisplayed()));
        textView18.check(matches(withText(date)));

        ViewInteraction textView19 = onView(
                allOf(withId(R.id.tv_height), withText("1.65 m"), isDisplayed()));
        textView19.check(matches(withText("1.65 m")));

        ViewInteraction textView20 = onView(
                allOf(withId(R.id.tv_weight), withText("55.0 kg"), isDisplayed()));
        textView20.check(matches(withText("55.0 kg")));

        pressBack();

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.rv_profileList), isDisplayed()));
        recyclerView3.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction textView21 = onView(
                allOf(withId(R.id.tv_date_last_measurement), withText("Datum"), isDisplayed()));
        textView21.check(matches(withText("Datum")));

        ViewInteraction textView22 = onView(
                allOf(withId(R.id.tv_height), withText("in m"), isDisplayed()));
        textView22.check(matches(withText("in m")));

        ViewInteraction textView23 = onView(
                allOf(withId(R.id.tv_weight), withText("in kg"), isDisplayed()));
        textView23.check(matches(withText("in kg")));

        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("Weitere Optionen"), isDisplayed()));
        overflowMenuButton.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Delete Profile?"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction recyclerView4 = onView(
                allOf(withId(R.id.rv_profileList), isDisplayed()));
        recyclerView4.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction textView24 = onView(
                allOf(withId(R.id.tv_date_last_measurement), withText(date), isDisplayed()));
        textView24.check(matches(withText(date)));

        ViewInteraction textView25 = onView(
                allOf(withId(R.id.tv_height), withText("1.65 m"), isDisplayed()));
        textView25.check(matches(withText("1.65 m")));

        ViewInteraction textView26 = onView(
                allOf(withId(R.id.tv_weight), withText("55.0 kg"), isDisplayed()));
        textView26.check(matches(withText("55.0 kg")));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_graphs), withText("Auswertung"), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.btn_graphs), withText("Auswertung"), isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.et_height), withText("165.0"), isDisplayed()));
        appCompatEditText8.perform(click());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.et_height), withText("165.0"), isDisplayed()));
        appCompatEditText9.perform(replaceText("166"));

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.et_weight), withText("55.0"), isDisplayed()));
        appCompatEditText10.perform(replaceText("58.0"));

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton5.perform(click());


        pressBack();

        pressBack();

        ViewInteraction textView29 = onView(
                allOf(withId(R.id.tv_date_last_measurement), withText(date), isDisplayed()));
        textView29.check(matches(withText(date)));

        ViewInteraction textView30 = onView(
                allOf(withId(R.id.tv_height), withText("1.66 m"), isDisplayed()));
        textView30.check(matches(withText("1.66 m")));

        ViewInteraction textView31 = onView(
                allOf(withId(R.id.tv_weight), withText("58.0 kg"), isDisplayed()));
        textView31.check(matches(withText("58.0 kg")));

        pressBack();

    }
}
