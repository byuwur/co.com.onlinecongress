package com.byuwur.onlinecongress;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestCase1 {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityatr = new ActivityTestRule<>(MainActivity.class);
    private MainActivity mainActivity = null;
    private Instrumentation.ActivityMonitor monitorfirst = getInstrumentation().addMonitor(Firsttime.class.getName(),null,false);

    @Before
    public void setUp() throws Exception{
        mainActivity= mainActivityatr.getActivity();
    }

    @Test
    public void testCase1() throws Exception {
        View viewbtnreg = mainActivity.findViewById(R.id.logo);
        assertNotNull(viewbtnreg);
        Activity activityfirst = getInstrumentation().waitForMonitorWithTimeout(monitorfirst, 5000);
        assertNotNull(activityfirst);
        activityfirst.finish();
    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
    }
}