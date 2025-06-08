public class HammingDecoder {
    public static int detectErrorPosition(String code) {
        int r = 0;
        int n = code.length();
        while (Math.pow(2, r) < n + 1) r++;

        int errorPosition = 0;
        for (int i = 0; i < r; i++) {
            int parity = 0;
            for (int j = 1; j <= n; j++) {
                if (((j >> i) & 1) == 1) {
                    parity ^= (code.charAt(j - 1) - '0');
                }
            }
            if (parity == 1) {
                errorPosition += (1 << i);
            }
        }
        return errorPosition;
    }

    public static String correctCode(String code, int errorPos) {
        if (errorPos < 1 || errorPos > code.length()) return code;
        char[] bits = code.toCharArray();
        bits[errorPos - 1] = (bits[errorPos - 1] == '0') ? '1' : '0';
        return new String(bits);
    }

    public static String extractDataBits(String correctedCode) {
        StringBuilder data = new StringBuilder();
        for (int i = 1; i <= correctedCode.length(); i++) {
            if (!isPowerOfTwo(i)) {
                data.append(correctedCode.charAt(i - 1));
            }
        }
        return data.toString();
    }

    private static boolean isPowerOfTwo(int x) {
        return (x & (x - 1)) == 0;
    }
}
