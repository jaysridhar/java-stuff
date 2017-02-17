package sample;

import java.math.BigInteger;
import java.math.BigDecimal;

import java.util.Date;
import java.util.Formatter;

public class sample2
{
    static public void main(String[] args) throws Exception
    {
	StringBuilder sbuf = new StringBuilder();
	Formatter fmt = new Formatter(sbuf);
	fmt.format("PI = %f%n", Math.PI);
	System.out.print(sbuf.toString());
	sbuf.append("That's all folks.");
	System.out.print(sbuf.toString());
    }
}
