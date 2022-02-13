package com.example.myapplication.streamcipher;

import org.bouncycastle.crypto.engines.Zuc128CoreEngine;
import org.bouncycastle.crypto.engines.Zuc128Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class BCZuc {
//    private static final String KEY128_1 = "00000000000000000000000000000000";
    private static final String IV128_1 = "00000000000000000000000000000000";



    public int intCipher(int in,String key){
        final Zuc128CoreEngine myEngine = new Zuc128Engine();
        final byte[] myData = intToByteArray(in);
        final byte[] myOutput = new byte[myData.length];

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(Hex.decode(key));
        final byte[] myIV = Hex.decode(IV128_1);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the cipher and create the keyStream */
        myEngine.init(true, myParms);
        myEngine.processBytes(myData, 0, myData.length, myOutput, 0);

        int out=byteArrayToInt(myOutput);
        return out;

    }

    public float floatCipher(float in,String key){
        final Zuc128CoreEngine myEngine = new Zuc128Engine();
        final byte[] myData = floatToByteArray(in);
        final byte[] myOutput = new byte[myData.length];

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(Hex.decode(key));
        final byte[] myIV = Hex.decode(IV128_1);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the cipher and create the keyStream */
        myEngine.init(true, myParms);
        myEngine.processBytes(myData, 0, myData.length, myOutput, 0);

        Float out=byteArrayToFloat(myOutput,0);
        return out;
    }



    public double doubleCipher(double in,String key){
        final Zuc128CoreEngine myEngine = new Zuc128Engine();
        final byte[] myData = doubleToByteArray(in);
        final byte[] myOutput = new byte[myData.length];

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(Hex.decode(key));
        final byte[] myIV = Hex.decode(IV128_1);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the cipher and create the keyStream */
        myEngine.init(true, myParms);
        myEngine.processBytes(myData, 0, myData.length, myOutput, 0);

        double out=byteArrayToDouble(myOutput,0);
        return out;
    }

    public long longCipher(long in,String key){
        final Zuc128CoreEngine myEngine = new Zuc128Engine();
        final byte[] myData = longToByteArray(in);
        final byte[] myOutput = new byte[myData.length];

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(Hex.decode(key));
        final byte[] myIV = Hex.decode(IV128_1);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the cipher and create the keyStream */
        myEngine.init(true, myParms);
        myEngine.processBytes(myData, 0, myData.length, myOutput, 0);

        long out=byteArrayToLong(myOutput,0);
        return out;
    }

    public String stringEncCipher(String in,String key) throws UnsupportedEncodingException {
        final Zuc128CoreEngine myEngine = new Zuc128Engine();
        final byte[] myData = in.getBytes("utf-8");
        final byte[] myOutput = new byte[myData.length];

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(Hex.decode(key));
        final byte[] myIV = Hex.decode(IV128_1);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the cipher and create the keyStream */
        myEngine.init(true, myParms);
        myEngine.processBytes(myData, 0, myData.length, myOutput, 0);
        byte[] bout= Base64.encode(myOutput);
        String out = new String(bout, "utf-8");

        return out;
    }

    public String stringDecCipher(String in,String key) throws UnsupportedEncodingException {
        final Zuc128CoreEngine myEngine = new Zuc128Engine();
        final byte[] myData = Base64.decode(in.getBytes("utf-8"));
        final byte[] myOutput = new byte[myData.length];

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(Hex.decode(key));
        final byte[] myIV = Hex.decode(IV128_1);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the cipher and create the keyStream */
        myEngine.init(true, myParms);
        myEngine.processBytes(myData, 0, myData.length, myOutput, 0);
        String out = new String(myOutput, "utf-8");

        return out;
    }

    public boolean boolCipher(boolean in,String key)  {
        final Zuc128CoreEngine myEngine = new Zuc128Engine();
        final byte[] myData =booleanToByteArray(in);
        final byte[] myOutput = new byte[myData.length];

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(Hex.decode(key));
        final byte[] myIV = Hex.decode(IV128_1);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the cipher and create the keyStream */
        myEngine.init(true, myParms);
        myEngine.processBytes(myData, 0, myData.length, myOutput, 0);

        boolean out=byteArrayToBoolean(myOutput);
        return out;
    }








    // int到byte[]
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    // byte[]转int
    public static int byteArrayToInt(byte[] bytes) {
        int value=0;
        for(int i = 0; i < 4; i++) {
            int num= (3-i) * 8;
            value +=(bytes[i] & 0xFF) << num;
        }
        return value;
    }

    // long转换为byte[8]数组
    public static byte[] longToByteArray(long l) {
        byte b[] = new byte[8];
        b[0] = (byte)  (0xff & (l >> 56));
        b[1] = (byte)  (0xff & (l >> 48));
        b[2] = (byte)  (0xff & (l >> 40));
        b[3] = (byte)  (0xff & (l >> 32));
        b[4] = (byte)  (0xff & (l >> 24));
        b[5] = (byte)  (0xff & (l >> 16));
        b[6] = (byte)  (0xff & (l >> 8));
        b[7] = (byte)  (0xff & l);
        return b;
    }

    // 从byte数组的index处的连续8个字节获得一个long
    public static long byteArrayToLong(byte[] arr, int index) {
        return 	(0xff00000000000000L 	& ((long)arr[index+0] << 56))  |
                (0x00ff000000000000L 	& ((long)arr[index+1] << 48))  |
                (0x0000ff0000000000L 	& ((long)arr[index+2] << 40))  |
                (0x000000ff00000000L 	& ((long)arr[index+3] << 32))  |
                (0x00000000ff000000L 	& ((long)arr[index+4] << 24))  |
                (0x0000000000ff0000L 	& ((long)arr[index+5] << 16))  |
                (0x000000000000ff00L 	& ((long)arr[index+6] << 8))   |
                (0x00000000000000ffL 	&  (long)arr[index+7]);
    }

    // double转换为byte[8]数组
    public static byte[] doubleToByteArray(double d) {
        long longbits = Double.doubleToLongBits(d);
        return longToByteArray(longbits);
    }
    // 从byte数组的index处的连续8个字节获得一个double
    public static double byteArrayToDouble(byte[] arr, int index) {
        return Double.longBitsToDouble(byteArrayToLong(arr, index));
    }
    // float转换为byte[4]数组
    public static byte[] floatToByteArray(float f) {
        int intbits = Float.floatToIntBits(f);//将float里面的二进制串解释为int整数
        return intToByteArray(intbits);
    }
    // 从byte数组的index处的连续4个字节获得一个float
    public static float byteArrayToFloat(byte[] arr, int index) {
        return Float.intBitsToFloat(byteArrayToInt(arr));
    }


    /**
     * 将boolean转成byte[]
     * @param val
     * @return byte[]
     */
    public static byte[] booleanToByteArray(boolean val) {
        int tmp = (val == false) ? 0 : 1;
        return ByteBuffer.allocate(4).putInt(tmp).array();
    }


    /**
     * 将byte[]转成boolean
     * @param data
     * @return boolean
     */
    public static boolean byteArrayToBoolean(byte[] data) {
        if (data == null || data.length < 4) {
            return false;
        }
        int tmp = ByteBuffer.wrap(data, 0, 4).getInt();
        return (tmp == 0) ? false : true;
    }

}


