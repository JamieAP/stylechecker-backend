// MessageSender.java (partial implementation)

import java.lang.Character;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;

/**
 * This class implements the sender side of the data link layer.
 * <p>
 * The source code supplied here contains only a partial implementation.
 * Your completed version must be submitted for assessment.
 * <p>
 * You only need to finish the implementation of the sendMessage
 * method to complete this class.  No other parts of this file need to
 * be changed.  Do NOT alter the constructor or interface of any public
 * method.  Do NOT put this class inside a package.  You may add new
 * private methods, if you wish, but do NOT create any new classes.
 * Only this file will be processed when your work is marked.
 */

public class MessageSender extends Thing {

    private static final String FRAME_START_DELIMITER = "<";
    private static final String FRAME_END_DELIMITER = ">";
    private static final String FRAME_TYPE_D = "D";
    private static final String FRAME_TYPE_E = "E";
    private static final String FIELD_DELIMITER = "-";

    // Fields ----------------------------------------------------------

    private FrameSender physicalLayer;   // physical layer object
    private boolean quiet;               // true=quiet mode (suppress
    // prompts and status info)

    // You may add additional fields but this shouldn't be necessary

    // Constructor -----------------------------------------------------

    /**
     * MessageSender constructor (DO NOT ALTER ANY PART OF THIS)
     * Create and initialize new MessageSender.
     *
     * @param physicalLayer physical layer object with frame sender service
     *                      (this will already have been created and initialized by TestSender)
     * @param quiet         true for quiet mode which suppresses prompts and status info
     */

    public MessageSender(FrameSender physicalLayer, boolean quiet) {
        // Initialize fields and report status

        this.physicalLayer = physicalLayer;
        this.quiet = quiet;
        if (!quiet) {
            System.out.println("Data link layer ready");
        }
    }

    // Methods ---------------------------------------------------------

    /**
     * Send a message (THIS IS THE ONLY METHOD YOU NEED TO MODIFY)
     *
     * @param message the message to be sent.  The message can be any
     *                length and may be empty but the string reference should not
     *                be null.
     * @throws ProtocolException immediately without attempting to
     *                           send any further frames if (and only if) the physical layer
     *                           throws an exception or the given message can't be sent
     *                           without breaking the rules of the protocol (e.g. if the MTU
     *                           is too small)
     */

    public void sendMessage(String message) throws ProtocolException {
        // Announce action
        // (Not required by protocol but helps when debugging)

        if (!quiet) {
            System.out.println("Sending message => " + message);
        }

        int chunkSize = physicalLayer.getMTU() - 10;

        if (chunkSize < 1) {
            throw new ProtocolException("MTU size too small");
        }

        List<String> chunks = splitToMsgChunks(message, chunkSize);
        List<String> frames = frameChunks(chunks);
        for (String frame : frames) {
            physicalLayer.sendFrame(frame);
        }
    }

    public List<String> splitToMsgChunks(String msg, int chunkSize) {
        LinkedList<String> chunks = new LinkedList<>();

        if (msg.isEmpty()) {
            chunks.add("");
            return chunks;
        }

        LinkedBlockingQueue<Character> queue = new LinkedBlockingQueue<>();
        queue.addAll(msg.chars().mapToObj(i -> (char) i).collect(Collectors.toList()));

        while (!queue.isEmpty()) {
            String chunk = "";
            while (chunk.length() < chunkSize && !queue.isEmpty()) {
                chunk = String.format("%s%s", chunk, queue.poll());
            }
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
        }
        return chunks;
    }

    public List<String> frameChunks(List<String> chunks) {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
        queue.addAll(chunks);

        List<String> frames = new LinkedList<>();
        while (!queue.isEmpty()) {
            String chunk = queue.poll();
            if (queue.isEmpty()) {
                frames.add(createFrameFor(chunk, true));
                return frames;
            }
            frames.add(createFrameFor(chunk, false));
        }
        return frames;
    }

    public String createFrameFor(String chunk, boolean lastFrame) {
        String frameData = String.format(
                "%s%s%02d%s%s%s",
                lastFrame ? FRAME_TYPE_E : FRAME_TYPE_D,
                FIELD_DELIMITER,
                chunk.length(),
                FIELD_DELIMITER,
                chunk,
                FIELD_DELIMITER
        );
        return String.format(
                "%s%s%02d%s",
                FRAME_START_DELIMITER,
                frameData,
                frameData.chars().reduce(0, (l, r) -> l + r) % 100,
                FRAME_END_DELIMITER
        );
    }

} // end of class MessageSender
