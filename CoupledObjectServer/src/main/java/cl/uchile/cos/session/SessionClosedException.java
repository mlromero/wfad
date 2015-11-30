package cl.uchile.cos.session;

public class SessionClosedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3541861812504325490L;

	public SessionClosedException() {
		super();
	}

	public SessionClosedException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public SessionClosedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SessionClosedException(String arg0) {
		super(arg0);
	}

	public SessionClosedException(Throwable arg0) {
		super(arg0);
	}

}
