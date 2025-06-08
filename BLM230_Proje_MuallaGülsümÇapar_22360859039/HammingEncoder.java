public class HammingEncoder {
    public static String encode(String dataBits) {
        int m = dataBits.length();
        int r = 0;

        
        while (Math.pow(2, r) < (m + r + 1)) {
            r++;
        }

        int totalLength = m + r;
        char[] hammingCode = new char[totalLength + 1]; 

        
        for (int i = 1, j = 0; i <= totalLength; i++) {
            if (isPowerOfTwo(i)) {
                hammingCode[i] = '0'; 
            } else {
                hammingCode[i] = dataBits.charAt(j++);
            }
        }

        
        for (int i = 0; i < r; i++) {
            int pos = (int) Math.pow(2, i);
            int parity = 0;
            for (int j = 1; j <= totalLength; j++) {
                if (((j >> i) & 1) == 1) {
                    parity ^= (hammingCode[j] - '0');
                }
            }
            hammingCode[pos] = (char) (parity + '0');
        }

        
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= totalLength; i++) {
            result.append(hammingCode[i]);
        }

        return result.toString();
    }

    private static boolean isPowerOfTwo(int x) {
        return (x & (x - 1)) == 0;
    }
}
