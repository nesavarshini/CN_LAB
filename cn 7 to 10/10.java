public class CRC {
    public static String xor(String a, String b) {
        String result = "";
        for (int i = 1; i < b.length(); i++)
            result += a.charAt(i) == b.charAt(i) ? '0' : '1';
        return result;
    }

    public static String mod2div(String dividend, String divisor) {
        int pick = divisor.length();
        String tmp = dividend.substring(0, pick);
        while (pick < dividend.length()) {
            if (tmp.charAt(0) == '1')
                tmp = xor(divisor, tmp) + dividend.charAt(pick);
            else
                tmp = xor("0".repeat(pick), tmp) + dividend.charAt(pick);
            pick++;
        }
        return tmp.charAt(0) == '1' ? xor(divisor, tmp) : xor("0".repeat(pick), tmp);
    }

    public static void main(String[] args) {
        String data = "100100";
        String divisor = "1101";
        String codeword = data + "000";
        String remainder = mod2div(codeword, divisor);
        System.out.println("Remainder: " + remainder);
    }
}