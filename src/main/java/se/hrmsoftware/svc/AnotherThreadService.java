package se.hrmsoftware.svc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Delegating service impl that calls the wrapped service in another thread.
 */
public class AnotherThreadService implements Service {

	private final ExecutorService executorService;
	private final Service delegate;

	public AnotherThreadService(ExecutorService executorService, Service delegate) {
		this.executorService = executorService;
		this.delegate = delegate;
	}

	@Override
	public String hello(final String name) {
		return doIt(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return delegate.hello(name);
			}
		});
	}

	@Override
	public void withSideEffect(final int numberOfSideEffects) {
		doIt(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				delegate.withSideEffect(numberOfSideEffects);
				return null;
			}
		});
	}

	private <T> T doIt(Callable<T> callable) {
		Future<T> future = executorService.submit(callable);
		try {
			return future.get(5L, TimeUnit.SECONDS); // Wait for 5 seconds.
		}
		catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw RuntimeException.class.cast(cause);
			} else {
				throw new RuntimeException("Operation threw an unexpected typed exception", cause);
			}
		}
		catch (TimeoutException e) {
			future.cancel(true); // Cancel task
			throw new RuntimeException("Operation timed out and may not have completed", e);
		}
		catch (Exception e) {
			throw new RuntimeException("Unknown exception", e);
		}
	}
}
