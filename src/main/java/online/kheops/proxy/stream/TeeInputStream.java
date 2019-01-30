package online.kheops.proxy.stream;

import java.io.*;

import static java.lang.Math.min;

public class TeeInputStream extends FilterInputStream {
    private final OutputStream outputStream;
    private long bytesSinceMark = 0;
    private long bytesRewound = 0;

    private static final int BUFFER_ARRAY_LENGTH = 4096;
    private final byte[] bufferArray = new byte[BUFFER_ARRAY_LENGTH];
    private final byte[] finishBufferArray = new byte[BUFFER_ARRAY_LENGTH];

    public TeeInputStream(final InputStream inputStream, final OutputStream outputStream) {
        super(inputStream);
        this.outputStream = outputStream;
    }

    public synchronized void finish() throws IOException {
        while (true) {
            if (read(finishBufferArray, 0 , BUFFER_ARRAY_LENGTH) == -1) {
                break;
            }
        }
    }

    @Override
    public synchronized int read() throws IOException {
        final int readInt = super.read();

        if (readInt == -1) {
            return readInt;
        }

        if (bytesRewound == 0) {
            outputStream.write(readInt);
        } else {
            bytesRewound--;
        }

        bytesSinceMark++;

        return readInt;
    }

    @Override
    public synchronized int read(final byte[] b, final int off, final int len) throws IOException {
        final int readCount = super.read(b, off, len);
        if (readCount == -1) {
            return readCount;
        }

        int bytesToWrite = readCount;

        if (bytesRewound > 0) {
            final int bytesToForward = (int)min(bytesRewound, bytesToWrite);
            bytesToWrite -= bytesToForward;
            bytesRewound -= bytesToForward;
        }

        outputStream.write(b, off + (readCount - bytesToWrite), bytesToWrite);
        bytesSinceMark += readCount;

        return readCount;
    }

    @Override
    public synchronized long skip(final long n) throws IOException {
        long skippedBytes = 0;

        while (skippedBytes < n) {
            final int bytesToRead = (int) min(BUFFER_ARRAY_LENGTH, n-skippedBytes);
            final int bytesRead = read(bufferArray, 0, bytesToRead);
            if (bytesRead == -1) {
                break;
            }
            skippedBytes += bytesRead;
// TODO bring this code back in once the dcm4che skip/skipFully bug is fixed
//            if (bytesRead < bytesToRead) {
//                break;
//            }
        }

        return skippedBytes;
    }

    @Override
    public synchronized void mark(final int readlimit) {
        bytesSinceMark = 0;
        super.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        bytesRewound += bytesSinceMark;
        bytesSinceMark = 0;
        super.reset();
    }
}
