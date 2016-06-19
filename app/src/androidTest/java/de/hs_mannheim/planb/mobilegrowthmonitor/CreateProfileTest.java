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
public class CreateProfileTest {

    private int year, month, day;

    @Before
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -6);
        calendar.getTime();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Rule
    public ActivityTestRule<MainView> mActivityTestRule = new ActivityTestRule<>(MainView.class);

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

        onView(withId(R.id.rv_profileList)).perform(swipeUp());

        onView(allOf(withId(R.id.tv_name),(withText("Max Muster, ")))).perform(click());

        ViewInteraction textView5 = onView(allOf(withId(R.id.tv_firstname)));
        textView5.check(matches(withText("Max,")));

        ViewInteraction textView6 = onView(allOf(withId(R.id.tv_age)));
        textView6.check(matches(withText("6")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.iv_gender), isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.tv_height), withText("in m"), isDisplayed()));
        textView8.check(matches(withText("in m")));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.tv_weight), withText("in kg"), isDisplayed()));
        textView9.check(matches(withText("in kg")));

        pressBack();

        onView(withId(R.id.rv_profileList)).perform(swipeUp());

        onView(allOf(withId(R.id.tv_name),(withText("Maxi Muster, ")))).perform(click());

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.tv_firstname), withText("Maxi,"), isDisplayed()));
        textView10.check(matches(withText("Maxi,")));

        ViewInteraction textView11 = onView(
                allOf(withId(R.id.tv_age), withText("25"), isDisplayed()));
        textView11.check(matches(withText("25")));

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.iv_gender), isDisplayed()));
        imageView2.check(matches(isDisplayed()));

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


        ViewInteraction textView19 = onView(
                allOf(withId(R.id.tv_height), withText("1.65 m"), isDisplayed()));
        textView19.check(matches(withText("1.65 m")));

        ViewInteraction textView20 = onView(
                allOf(withId(R.id.tv_weight), withText("55.0 kg"), isDisplayed()));
        textView20.check(matches(withText("55.0 kg")));

        pressBack();

        onView(withId(R.id.rv_profileList)).perform(swipeUp());

        onView(allOf(withId(R.id.tv_name),(withText("Max Muster, ")))).perform(click());

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
                allOf(withId(R.id.title), withText("Profil löschen?"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction imageView3 = onView(
                allOf(withId(android.R.id.icon),
                        withParent(allOf(withId(R.id.title_template),
                                withParent(allOf(withId(R.id.topPanel),
                                        withParent(allOf(withId(R.id.parentPanel),
                                                withParent(allOf(withId(android.R.id.content),
                                                        withParent(withId(R.id.action_bar_root)))))))))),
                        isDisplayed()));
        imageView3.check(matches(isDisplayed()));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(android.R.id.button1), withText("Ja"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        appCompatButton3.perform(click());

        onView(withId(R.id.rv_profileList)).perform(swipeUp());

        onView(allOf(withId(R.id.tv_name),(withText("Maxi Muster, ")))).perform(click());

        ViewInteraction textView25 = onView(
                allOf(withId(R.id.tv_height), withText("1.65 m"), isDisplayed()));
        textView25.check(matches(withText("1.65 m")));

        ViewInteraction textView26 = onView(
                allOf(withId(R.id.tv_weight), withText("55.0 kg"), isDisplayed()));
        textView26.check(matches(withText("55.0 kg")));

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btn_graphs), withText("Auswertung"), isDisplayed()));
        appCompatButton4.perform(click());

        pressBack();

        ViewInteraction button = onView(
                allOf(withId(R.id.btn_graphs), withText("Auswertung"), isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btn_measurements), withText("Messung"), isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.et_height), withText("165.0"), isDisplayed()));
        appCompatEditText8.perform(click());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.et_height), withText("165.0"), isDisplayed()));
        appCompatEditText9.perform(replaceText("166"));

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.et_weight), withText("55.0"), isDisplayed()));
        appCompatEditText10.perform(replaceText("58.0"));

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.btn_enter_for_bmi), withText("Enter"), isDisplayed()));
        appCompatButton6.perform(click());


        pressBack();

        pressBack();

        ViewInteraction textView30 = onView(
                allOf(withId(R.id.tv_height), withText("1.66 m"), isDisplayed()));
        textView30.check(matches(withText("1.66 m")));

        ViewInteraction textView31 = onView(
                allOf(withId(R.id.tv_weight), withText("58.0 kg"), isDisplayed()));
        textView31.check(matches(withText("58.0 kg")));

        pressBack();

    }
}
