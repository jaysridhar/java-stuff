package sample;

import java.math.BigInteger;
import java.math.BigDecimal;

import java.util.Date;

public class sample1
{
    static public void main(String[] args) throws Exception
    {
	if ( args.length == 0 ) {
	    System.err.println("usage: java sample1 formatStr ..");
	    System.exit(1);
	}

	for (int i = 0 ; i < args.length ; i++) {
	    System.out.printf(args[i],
			      /*  1 */'p',
			      /*  2 */"Hello World",
			      /*  3 */93,
			      /*  4 */32.445,
			      /*  5 */new BigInteger("3278904234790239"),
			      /*  6 */new BigDecimal("348394389834535.4893483"),
			      /*  7 */null,
			      /*  8 */true,
			      /*  9 */false,
			      /* 10 */'\u00A5',
			      /* 11 */new Date(),
			      /* 12 */-36,
			      /* 13 */10000000);
	}
    }
}
