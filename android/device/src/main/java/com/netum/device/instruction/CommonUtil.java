package com.netum.device.instruction;

import java.util.List;

public class CommonUtil {

    public static byte[] toPrimitives(Byte[] input) {
        byte[] output = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    public static byte[] toPrimitives(List<Byte> input) {
        byte[] output = new byte[input.size()];
        for (int i = 0; i < input.size(); i++) {
            output[i] = input.get(i);
        }
        return output;
    }
}
