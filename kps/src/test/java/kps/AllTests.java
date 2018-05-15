package kps.tests;

import org.junit.runners.Suite;

import org.junit.runner.RunWith;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LogTests.class,
        BusinessFiguresTests.class,
        KPSServerTests.class,
        TransportMapTests.class,
        DateRangeTests.class,
})
public class AllTests {

}
