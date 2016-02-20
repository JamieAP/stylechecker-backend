// ProtocolException.java (full implementation)
// DO NOT CHANGE THIS CLASS

/**
 * Signals that a network protocol error has occurred.
 * No other type of exception should be used for this purpose.
 * <P>
 * This is one of the supporting classes for the networks coursework.
 * Certain physical and data link layer methods throw exceptions of this
 * class in the event of an error being detected.  The documentation for
 * the other classes indicates which of their methods use this exception.
 */

public class ProtocolException extends Exception
{
    /**
     * Constructs a new exception with a null detail string
     */
    private int lol;
    public ProtocolException(String msg)
    {
        super();
    }

    /**
     * Constructs a new exception with the specified detail string
     * @param message the detail string
     */

    public ProtocolException()
    {
        this("sup");
    }

} // end of ProtocolException class

