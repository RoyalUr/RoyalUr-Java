package net.royalur.lut.store;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a set of ordered uint32 buffers, and uses merge sort to combine
 * them as needed. This is more efficient than using one big ordered buffer
 * and using insertion sort when each value is added.
 */
public class OrderedUInt32BufferSet {

    public static final int DEFAULT_ENTRIES_PER_BUFFER = 8 * 1024;

    private final int entriesPerBuffer;
    private final List<OrderedUInt32Buffer> buffers;

    public OrderedUInt32BufferSet(int entriesPerBuffer) {
        this.entriesPerBuffer = entriesPerBuffer;
        this.buffers = new ArrayList<>();
    }

    public OrderedUInt32BufferSet() {
        this(DEFAULT_ENTRIES_PER_BUFFER);
    }

    public boolean isSorted() {
        return buffers.size() <= 1;
    }

    public int getEntryCount() {
        int count = 0;
        for (OrderedUInt32Buffer buffer : buffers) {
            count += buffer.getEntryCount();
        }
        return count;
    }

    private OrderedUInt32Buffer allocateNewBuffer() {
        return new OrderedUInt32Buffer(entriesPerBuffer);
    }

    private OrderedUInt32Buffer mergeOrderedBuffers(
            OrderedUInt32Buffer input1,
            OrderedUInt32Buffer input2
    ) {
        int entryCount = input1.getEntryCount() + input2.getEntryCount();
        OrderedUInt32Buffer output = new OrderedUInt32Buffer(entryCount);

        int chunk1Size = input1.getEntryCount();
        int chunk2Size = input2.getEntryCount();
        if (chunk1Size == 0)
            return input2;
        if (chunk2Size == 0)
            return input1;

        int index1 = 0;
        int index2 = 0;
        int value1 = input1.get(index1);
        int value2 = input2.get(index2);

        while (true) {
            if (Integer.compareUnsigned(value1, value2) <= 0) {
                output.add(value1);
                index1 += 1;
                if (index1 >= chunk1Size)
                    break;

                value1 = input1.get(index1);

            } else {
                output.add(value2);
                index2 += 1;
                if (index2 >= chunk2Size)
                    break;

                value2 = input2.get(index2);
            }
        }

        while (index1 < chunk1Size) {
            output.add(input1.get(index1));
            index1 += 1;
        }
        while (index2 < chunk2Size) {
            output.add(input2.get(index2));
            index2 += 1;
        }
        return output;
    }

    private OrderedUInt32Buffer getNextBuffer() {
        if (buffers.isEmpty()) {
            buffers.add(allocateNewBuffer());
        }

        OrderedUInt32Buffer last = buffers.get(buffers.size() - 1);
        if (!last.isFull())
            return last;

        // Merge sort full chunks!
        while (buffers.size() >= 2) {
            int size = buffers.size();
            OrderedUInt32Buffer buffer1 = buffers.get(size - 1);
            OrderedUInt32Buffer buffer2 = buffers.get(size - 2);
            if (buffer1.getEntryCount() != buffer2.getEntryCount())
                break;

            OrderedUInt32Buffer sorted = mergeOrderedBuffers(buffer1, buffer2);
            buffers.remove(size - 1);
            buffers.remove(size - 2);
            buffers.add(sorted);
        }

        // Add a new chunk.
        OrderedUInt32Buffer next = allocateNewBuffer();
        buffers.add(next);
        return next;
    }

    public void sort() {
        while (buffers.size() >= 2) {
            int size = buffers.size();
            OrderedUInt32Buffer buffer1 = buffers.get(size - 1);
            OrderedUInt32Buffer buffer2 = buffers.get(size - 2);
            OrderedUInt32Buffer sorted = mergeOrderedBuffers(buffer1, buffer2);
            buffers.remove(size - 1);
            buffers.remove(size - 2);
            buffers.add(sorted);
        }
    }

    public void add(int value) {
        getNextBuffer().add(value);
    }

    public OrderedUInt32Buffer toSingleCompressedBuffer() {
        if (buffers.isEmpty())
            throw new IllegalStateException("Buffer set is empty");

        sort();
        if (buffers.size() != 1)
            throw new IllegalStateException("Invalid sort");

        OrderedUInt32Buffer buffer = buffers.get(0).compress();
        buffers.set(0, buffer);
        return buffer;
    }
}
