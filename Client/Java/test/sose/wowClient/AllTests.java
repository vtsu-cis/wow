package test.sose.wowClient;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.sose.wowBL.PersonTest;
import test.sose.wowDA.HackDATest;
import test.sose.wowDA.LocalDATest;
import test.sose.wowDA.MockDATest;

/**
 * @author Nick Guertin and Boomer Ransom
/**
 * @className - AllTests
 * @author - Nick Guertin and Boomer Ransom
 * @description - Testsuite to run all WOW tests.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
	PersonTest.class,
	MockDATest.class,
	CLITest.class,
	HackDATest.class,
	LocalDATest.class
})

public class AllTests {}