package com.netum.device.instruction;

import java.util.ArrayList;
import java.util.List;

public class ModuleUtil {

    /**
     * Automatic inductive scanning
     * @return
     */
    public static byte[] AutomaticScan(){
        List<Byte> data = new ArrayList();
        data.add((byte)0x07);
        data.add((byte)0xC6);
        data.add((byte)0x04);
        data.add((byte)0x08);
        data.add((byte)0x00);
        data.add((byte)0x8A);
        data.add((byte)0x09);
        data.add((byte)0xFE);
        data.add((byte)0x94);
        return CommonUtil.toPrimitives(data);
    }
}
