package utils;

/**
 * Communication Format Conversion
 * 
 * When Java programming language and some of the windows, such as c, c + +,
 * delphi written procedures for communication networks, the need for
 * appropriate high conversion, the conversion between the low byte
 * **/

public class FormatTransfer {

	/**
	 * Int into the low byte first, byte after byte array of high
	 * 
	 * @param n
	 *            int
	 * @return byte[]
	 */
	public static byte[] toLH(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}

	public static byte[] toLH(long n) {
		byte[] b = new byte[8];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		b[4] = (byte) (n >> 32 & 0xff);
		b[5] = (byte) (n >> 40 & 0xff);
		b[6] = (byte) (n >> 48 & 0xff);
		b[7] = (byte) (n >> 56 & 0xff);
		return b;
	}

	/**
	 * Int into the high byte first, byte after byte array of low
	 * 
	 * @param n
	 *            int
	 * @return byte[]
	 */
	public static byte[] toHH(int n) {
		byte[] b = new byte[4];
		b[3] = (byte) (n & 0xff);
		b[2] = (byte) (n >> 8 & 0xff);
		b[1] = (byte) (n >> 16 & 0xff);
		b[0] = (byte) (n >> 24 & 0xff);
		return b;
	}

	public static byte[] toHH(long n) {
		byte[] b = new byte[8];
		b[7] = (byte) (n & 0xff);
		b[6] = (byte) (n >> 8 & 0xff);
		b[5] = (byte) (n >> 16 & 0xff);
		b[4] = (byte) (n >> 24 & 0xff);
		b[3] = (byte) (n >> 32 & 0xff);
		b[2] = (byte) (n >> 40 & 0xff);
		b[1] = (byte) (n >> 48 & 0xff);
		b[0] = (byte) (n >> 56 & 0xff);
		return b;
	}

	/**
	 * The short into the low byte first, byte after byte array of high
	 * 
	 * @param n
	 *            short
	 * @return byte[]
	 */
	public static byte[] toLH(short n) {
		byte[] b = new byte[2];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		return b;
	}

	/**
	 * The short into the high byte first, byte after byte array of low
	 * 
	 * @param n
	 *            short
	 * @return byte[]
	 */
	public static byte[] toHH(short n) {
		byte[] b = new byte[2];
		b[1] = (byte) (n & 0xff);
		b[0] = (byte) (n >> 8 & 0xff);
		return b;
	}

	/**
	 * Will be converted to int endian, byte after byte array of low
	 * 
	 * public static byte[] toHH(int number) { int temp = number; byte[] b = new
	 * byte[4]; for (int i = b.length - 1; i > -1; i--) { b = new Integer(temp &
	 * 0xff).byteValue(); temp = temp >> 8; } return b; }
	 * 
	 * public static byte[] IntToByteArray(int i) { byte[] abyte0 = new byte[4];
	 * abyte0[3] = (byte) (0xff & i); abyte0[2] = (byte) ((0xff00 & i) >> 8);
	 * abyte0[1] = (byte) ((0xff0000 & i) >> 16); abyte0[0] = (byte)
	 * ((0xff000000 & i) >> 24); return abyte0; }
	 */

	/**
	 * Will float into the low byte first, byte after byte array of high
	 */
	public static byte[] toLH(float f) {
		return toLH(Float.floatToRawIntBits(f));
	}

	/**
	 * Will float into the high byte first, byte after byte array of low
	 */
	public static byte[] toHH(float f) {
		return toHH(Float.floatToRawIntBits(f));
	}

	/**
	 * The String into a byte array
	 */
	public static byte[] stringToBytes(String s, int length) {
		while (s.getBytes().length < length) {
			s += " ";
		}
		return s.getBytes();
	}

//	/**
//	 * Converts the byte array to String
//	 *
//	 * @param b
//	 *            byte[]
//	 * @return String
//	 */
//	public static String bytesToString(byte[] b) {
//		StringBuffer result = new StringBuffer("");
//		int length = b.length;
//		for (int i = 0; i < length; i++) {
//			result.append((char) (b[i] & 0xff));
//		}
//		return result.toString();
//	}

	/**
	 * Convert a string to byte array
	 * 
	 * @param s
	 *            String
	 * @return byte[]
	 */
	public static byte[] stringToBytes(String s) {
		return s.getBytes();
	}

	/**
	 * The high byte array is converted to int
	 * 
	 * @param b
	 *            byte[]
	 * @return int
	 */
	public static int hBytesToInt(byte[] b) {
		int s = 0;
		for (int i = 0; i < 3; i++) {
			if (b[i] >= 0) {
				s = s + b[i];
			} else {
				s = s + 256 + b[i];
			}
			s = s * 256;
		}
		if (b[3] >= 0) {
			s = s + b[3];
		} else {
			s = s + 256 + b[3];
		}
		return s;
	}

	/**
	 * The low byte array is converted to int
	 * 
	 * @param b
	 *            byte[]
	 * @return int
	 */
	public static int lBytesToInt(byte[] b) {
		int s = 0;
		for (int i = 0; i < 3; i++) {
			if (b[3 - i] >= 0) {
				s = s + b[3 - i];
			} else {
				s = s + 256 + b[3 - i];
			}
			s = s * 256;
		}
		if (b[0] >= 0) {
			s = s + b[0];
		} else {
			s = s + 256 + b[0];
		}
		return s;
	}

	/**
	 * High byte array to short conversion
	 * 
	 * @param b
	 *            byte[]
	 * @return short
	 */
	public static short hBytesToShort(byte[] b) {
		int s = 0;
		if (b[0] >= 0) {
			s = s + b[0];
		} else {
			s = s + 256 + b[0];
		}
		s = s * 256;
		if (b[1] >= 0) {
			s = s + b[1];
		} else {
			s = s + 256 + b[1];
		}
		short result = (short) s;
		return result;
	}

	public static long hBytesToLong(byte[] x) {
		return ((((long) x[0] & 0xff) << 56) | (((long) x[1] & 0xff) << 48)
				| (((long) x[2] & 0xff) << 40) | (((long) x[3] & 0xff) << 32)
				| (((long) x[4] & 0xff) << 24) | (((long) x[5] & 0xff) << 16)
				| (((long) x[6] & 0xff) << 8) | (((long) x[7] & 0xff) << 0));
	}

	public static long lBytesToLong(byte[] x) {
		return ((((long) x[7] & 0xff) << 56) | (((long) x[6] & 0xff) << 48)
				| (((long) x[5] & 0xff) << 40) | (((long) x[4] & 0xff) << 32)
				| (((long) x[3] & 0xff) << 24) | (((long) x[2] & 0xff) << 16)
				| (((long) x[1] & 0xff) << 8) | (((long) x[0] & 0xff) << 0));
	}

	/**
	 * Low byte array to short conversion
	 * 
	 * @param b
	 *            byte[]
	 * @return short
	 */
	public static short lBytesToShort(byte[] b) {
		int s = 0;
		if (b[1] >= 0) {
			s = s + b[1];
		} else {
			s = s + 256 + b[1];
		}
		s = s * 256;
		if (b[0] >= 0) {
			s = s + b[0];
		} else {
			s = s + 256 + b[0];
		}
		short result = (short) s;
		return result;
	}

	/**
	 * High byte array is converted to float
	 * 
	 * @param b
	 *            byte[]
	 * @return float
	 */
	public static float hBytesToFloat(byte[] b) {
		int i = 0;
		i = ((((b[0] & 0xff) << 8 | (b[1] & 0xff)) << 8) | (b[2] & 0xff)) << 8
				| (b[3] & 0xff);
		return Float.intBitsToFloat(i);
	}

	/**
	 * Low byte array is converted to float
	 * 
	 * @param b
	 *            byte[]
	 * @return float
	 */
	public static float lBytesToFloat(byte[] b) {
		int i = 0;
		i = ((((b[3] & 0xff) << 8 | (b[2] & 0xff)) << 8) | (b[1] & 0xff)) << 8
				| (b[0] & 0xff);
		return Float.intBitsToFloat(i);
	}

	/**
	 * The byte array elements in descending order
	 */
	public static byte[] bytesReverseOrder(byte[] b) {
		int length = b.length;
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[length - i - 1] = b[i];
		}
		return result;
	}

	/**
	 * Print byte array
	 */
	public static void printBytes(byte[] bb) {
		int length = bb.length;
		for (int i = 0; i < length; i++) {
			System.out.print(bb + " ");
		}
		System.out.println("");
	}

	public static void logBytes(byte[] bb) {
		int length = bb.length;
		String out = "";
		for (int i = 0; i < length; i++) {
			out = out + bb + " ";
		}

	}

	/**
	 * Convert the value of type int to byte order reversed corresponding int
	 * value
	 * 
	 * @param i
	 *            int
	 * @return int
	 */
	public static int reverseInt(int i) {
		int result = FormatTransfer.hBytesToInt(FormatTransfer.toLH(i));
		return result;
	}

	/**
	 * Convert the value of short type is reversed byte order value
	 * corresponding shortֵ
	 * 
	 * @param s
	 *            short
	 * @return short
	 */
	public static short reverseShort(short s) {
		short result = FormatTransfer.hBytesToShort(FormatTransfer.toLH(s));
		return result;
	}

	/**
	 * Convert the value of float type is byte order reversed corresponding
	 * float valueֵ
	 * 
	 * @param f
	 *            float
	 * @return float
	 */
	public static float reverseFloat(float f) {
		float result = FormatTransfer.hBytesToFloat(FormatTransfer.toLH(f));
		return result;
	}

	public static boolean equals(byte[] arr1, byte[] arr2) {
		if (arr1.length != arr2.length) {
			return false;
		}
		for (int i = 0; i < arr1.length; i++) {
			if (arr1[i] != arr2[i]) {
				return false;
			}
		}
		return true;
	}

	// add by tom----------------------------------------------

	// byte[] to short
	// @param b
	// @param index
	// start from index
	// @return
	public static String BytesToString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
			ret += " ";
		}
		return ret + " ";
	}
	public static String BytesToString(byte[] b, int nlen) {
		String ret = "";
		for (int i = 0; i < nlen; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
			ret += " ";
		}
		return ret + " ";
	}
	public static short getShortFromByte(byte[] b, int index) {
		return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
	}
	/**
	 * 将byte数组转换为int数据
	 * @param b 字节数组
	 * @return 生成的int数据
	 */
	public static int getIntFromByte(byte[] b){
		return (((int)b[0]) << 24) + (((int)b[1]) << 16) + (((int)b[2]) << 8) + b[3];
	}

	// short to byte
	public final static byte[] getBytes(short s, boolean asc) {
		byte[] buf = new byte[2];
		if (asc)
			for (int i = buf.length - 1; i >= 0; i--) {
				buf[i] = (byte) (s & 0x00ff);
				s >>= 8;
			}
		else
			for (int i = 0; i < buf.length; i++) {
				buf[i] = (byte) (s & 0x00ff);
				s >>= 8;
			}
		return buf;
	}

	// compose two byte arraies
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	public static byte[] byteRevert(byte[] arrByte) {
		for (int i = 0; i < arrByte.length / 2; i++) {
			byte temp = arrByte[i];
			arrByte[i] = arrByte[arrByte.length - i - 1];
			arrByte[arrByte.length - i - 1] = temp;
		}
		return arrByte;
	}

	public static byte[] getByteArrFromString(String resource, int arrLength) {
		byte[] byteArr = new byte[arrLength];
		StringBuffer sbTemp = new StringBuffer();
		for (int i = 0; i < arrLength; i++) {
			if (sbTemp.length() > 0) {
				sbTemp.delete(0, 4);
			}
			sbTemp.append("00");
			sbTemp.append(resource.substring(i * 2, (i + 1) * 2));
			short number = Short.parseShort(sbTemp.toString(), 16);
			byteArr[i] = FormatTransfer.toHH(number)[1];
		}
		return byteArr;
	}
	/**
	 * 把16进制字符串转换成字节数组
	 * @param
	 * @return byte[]
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}
	private static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	public static boolean bytesEqual(byte[] byte1, byte[] byte2,
			int startIndex, int compareLen) {
		boolean result = true;
		if (byte1.length < compareLen || byte2.length < compareLen
				|| byte2.length < startIndex || byte1.length < startIndex) {
			result = false;
		}
		for (int i = 0; i < compareLen; i++) {
			if (byte1[i + startIndex] != byte2[i + startIndex]) {
				result = false;
				break;
			}
		}
		return result;
	}
	// add by tom----------------------------------------------

	public static long bytesToLong(byte[] b){
		int firstByte = 0;
		int secondByte = 0;
		int thirdByte = 0;
		int fourthByte = 0;

		firstByte = (0x000000FF & ((int) b[0]));
		secondByte = (0x000000FF & ((int) b[1]));
		thirdByte = (0x000000FF & ((int) b[2]));
		fourthByte = (0x000000FF & ((int) b[3]));

		Long result=((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
		return result;
	}
	public static String bytes2HexString(byte[] bytes) {

		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
			builder.append(String.format("%1$02x", b & 0xff) + " ");
		}

		return builder.toString().toUpperCase();
	}
}
