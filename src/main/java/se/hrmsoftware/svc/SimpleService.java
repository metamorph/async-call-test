package se.hrmsoftware.svc;

public class SimpleService implements Service {

	private final StringBuilder sideEffect;

	public SimpleService(StringBuilder sideEffect) {
		this.sideEffect = sideEffect;
	}

	@Override
	public String hello(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Name need to be defined");
		}
		else {
			try {
				Thread.sleep(1000L);
				if ("wait".equals(name)) {
					Thread.sleep(6000L);
				}
				return String.format("Hello, %s", name);
			}
			catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void withSideEffect(int numberOfSideEffects) {
		for (int i = 0; i < numberOfSideEffects; i++) {
			sideEffect.append(String.format("SIDE EFFECT: %d", i));
		}
	}
}
