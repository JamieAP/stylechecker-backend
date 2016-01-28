// FrameSender.java (full implementation)
// DO NOT CHANGE THIS CLASS

/**
 * This class implements the sender side of the physical layer.
 * <P>
 * The source code supplied is already complete.  It does NOT
 * need to be changed and will NOT be submitted for assessment.
 * You may, if you wish, alter the methods in this class for the
 * purpose of debugging your implementation of MessageSender,
 * but be careful not to change the interface.
 */

public class FrameSender
{
    // Fields ----------------------------------------------------------

    private int mtu;              // maximum transfer unit (frame length limit)
    private boolean quiet;        // true=quiet mode (suppress prompts and status info)
    private boolean revealControlCodes; // true=make control codes visible

    // Constructor -----------------------------------------------------

    /**
     * FrameSender constructor.
     * Create and initialize new FrameSender.
     * @param mtu the maximum transfer unit (frame length) to support
     * @param quiet true for quiet mode which suppresses prompts and status info
     * @param revealControlCodes true if control codes are to be made visible
     */

    public FrameSender(int mtu, boolean quiet, boolean revealControlCodes)
    {
        // Initialize fields

        this.mtu = mtu;
        this.quiet = quiet;
        this.revealControlCodes = revealControlCodes;

        // Report status

        if (!quiet) {
            System.out.println("Physical layer ready : mtu = " + mtu
                + ", make control codes visible = "
                + (revealControlCodes ? "yes" : "no"));
        }
    }

    // Methods ---------------------------------------------------------

    /**
     * Get the MTU
     * @return the maximum transfer unit supported
     */

    public int getMTU()
    {
        return mtu;
    }

    // -----------------------------------------------------------------

    /**
     * Send a single frame.
     * If a message is split across several frames this method must be
     * called separately for each frame in turn.
     * @param frame the frame to be sent.  The length must not exceed
     * the MTU and there should be no extraneous leading or trailing
     * characters
     * @throws ProtocolException in the event of an error
     */

    public void sendFrame(String frame) throws ProtocolException
    {
        // Announce action

        if (!quiet)
            System.out.print("Sending frame   => ");

        // Print frame

        if (revealControlCodes)
            printAndRevealControlCodes(frame);  // reveal on
        else
            System.out.println(frame);          // reveal off
    }

    // -----------------------------------------------------------------

    /**
     * Print string and reveal any control codes (make them visible).
     * This private method helps reveal errors in the content of a
     * frame that may not be visible if it's printed out normally,
     * such as spurious null characters.
     * @param str string to be printed
     */

    private static void printAndRevealControlCodes(String str)
    {
        // Process string one character at a time

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);            // extract next char
            if (Character.isISOControl(ch))     // test for control code
                System.out.print("(CONTROL)");  // print warning text if found
            else
                System.out.print(ch);           // else just print the char
        }
        System.out.println();                   // terminate line
    }

} // end of class FrameSender

