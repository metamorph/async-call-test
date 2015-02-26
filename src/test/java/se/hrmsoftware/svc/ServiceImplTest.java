package se.hrmsoftware.svc;

import org.junit.Test;

import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class ServiceImplTest {

	private final StringBuilder sideEffect = new StringBuilder();
	private final Service normalService = new SimpleService(sideEffect);
	private final Service asyncService = new AnotherThreadService(Executors.newFixedThreadPool(5), normalService);

	private void _testHello(Service svc) {
		String expected = "Hello, dummy";
		String actual = svc.hello("dummy");
		assertEquals(expected, actual);
	}

	private void _testHelloException(Service svc) {
		try {
			svc.hello(null);
			fail("Should throw exception");
		}
		catch (IllegalArgumentException e) {
			// ok
		}
	}

	private void _testSideEffect(Service svc) {
		svc.withSideEffect(3);
		assertEquals("SIDE EFFECT: 0SIDE EFFECT: 1SIDE EFFECT: 2", sideEffect.toString());
	}

	@Test
	public void testHello() {
		_testHello(normalService);
	}

	@Test
	public void testHelloException() {
		_testHelloException(normalService);
	}

	@Test
	public void testSideEffect() {
		_testSideEffect(normalService);
	}

	@Test
	public void testHelloAsync() {
		_testHello(asyncService);
	}

	@Test
	public void testHelloExceptionAsync() {
		_testHelloException(asyncService);
	}

	@Test
	public void testSideEffectAsync() {
		_testSideEffect(asyncService);
	}

	@Test(expected = RuntimeException.class)
	public void testTimedOutHello() {
		asyncService.hello("wait");
	}
}