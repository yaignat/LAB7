package network;

import java.io.Serial;
import java.io.Serializable;

public class PacketFragment implements Serializable {
    @Serial
    private static final long serialVersionUid = 1L;

    private final long messageId;
    private final int fragmentIndex;
    private final int totalFragments;
    private final byte[] data;

    public PacketFragment(long messageId, int fragmentIndex, int totalFragments, byte[] data) {
        this.messageId = messageId;
        this.fragmentIndex = fragmentIndex;
        this.totalFragments = totalFragments;
        this.data = data;
    }

    public long getMessageId() { return messageId; }
    public int getFragmentIndex() { return fragmentIndex; }
    public int getTotalFragments() { return totalFragments; }
    public byte[] getData() { return data; }
}
