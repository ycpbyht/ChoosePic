package bclb.upload.album.utils;

import java.util.Random;

/**
 * 产生一个随机字符串，当强的作用是为了生成一个图片的名字。。。。
 * 比如压缩的图片名称，或者拍照的图片或者视频
 */
public class RandomString {

    /**
     * 产生随机字符串
     * */
    private static Random randGen = null;
    private static char[] numbersAndLetters = null;

    /**
     * @param length 随即字符串的长度
     * @return
     */
    public static final String randomString(int length) {
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
            randGen = new Random();
            numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
                    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        }
        char [] randBuffer = new char[length];
        for (int i=0; i<randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }

}
