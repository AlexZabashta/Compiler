package misc;

import java.nio.ByteBuffer;

public class LittleEndian {
    public static byte[] encode(int num) {
        byte[] array = ByteBuffer.allocate(4).putInt(num).array().clone();

        int l = 0, r = array.length - 1;

        while (l < r) {
            array[l] ^= array[r];
            array[r] ^= array[l];
            array[l] ^= array[r];
            ++l;
            --r;
        }

        return array;

    }
}
