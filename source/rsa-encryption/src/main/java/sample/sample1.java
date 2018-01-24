package sample;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Arrays;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class sample1
{
    static private Base64.Encoder encoder = Base64.getEncoder();
    static SecureRandom srandom = new SecureRandom();

    static private void processFile(Cipher ci,InputStream in,OutputStream out)
	throws javax.crypto.IllegalBlockSizeException,
	       javax.crypto.BadPaddingException,
	       java.io.IOException
    {
	byte[] ibuf = new byte[1024];
	int len;
	while ((len = in.read(ibuf)) != -1) {
	    byte[] obuf = ci.update(ibuf, 0, len);
	    if ( obuf != null ) out.write(obuf);
	}
	byte[] obuf = ci.doFinal();
	if ( obuf != null ) out.write(obuf);
    }

    static private void processFile(Cipher ci,String inFile,String outFile)
	throws javax.crypto.IllegalBlockSizeException,
	       javax.crypto.BadPaddingException,
	       java.io.IOException
    {
	try (FileInputStream in = new FileInputStream(inFile);
	     FileOutputStream out = new FileOutputStream(outFile)) {
		processFile(ci, in, out);
	    }
    }

    static private void doGenkey(String[] args)
	throws java.security.NoSuchAlgorithmException,
	       java.io.IOException
    {
	if ( args.length == 0 ) {
	    System.err.println("genkey -- need fileBase");
	    return;
	}

	int index = 0;
	String fileBase = args[index++];
	KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
	kpg.initialize(2048);
	KeyPair kp = kpg.generateKeyPair();
	try (FileOutputStream out = new FileOutputStream(fileBase + ".key")) {
		out.write(kp.getPrivate().getEncoded());
	    }

	try (FileOutputStream out = new FileOutputStream(fileBase + ".pub")) {
		out.write(kp.getPublic().getEncoded());
	    }
    }

    /* Larger data gives:
     *
     * javax.crypto.IllegalBlockSizeException: Data must not be longer
     * than 245 bytes
     */
    static private void doEncrypt(String[] args)
	throws java.security.NoSuchAlgorithmException,
	       java.security.spec.InvalidKeySpecException,
	       javax.crypto.NoSuchPaddingException,
	       javax.crypto.BadPaddingException,
	       java.security.InvalidKeyException,
	       javax.crypto.IllegalBlockSizeException,
	       java.io.IOException
    {
	if ( args.length != 2 ) {
	    System.err.println("enc pvtKeyFile inputFile");
	    System.exit(1);
	}

	int index = 0;
	String pvtKeyFile = args[index++];
	String inputFile = args[index++];
	byte[] bytes = Files.readAllBytes(Paths.get(pvtKeyFile));
	PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
	KeyFactory kf = KeyFactory.getInstance("RSA");
	PrivateKey pvt = kf.generatePrivate(ks);

	Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	cipher.init(Cipher.ENCRYPT_MODE, pvt);
	processFile(cipher, inputFile, inputFile + ".enc");
    }

    static private void doDecrypt(String[] args)
	throws java.security.NoSuchAlgorithmException,
	       java.security.spec.InvalidKeySpecException,
	       javax.crypto.NoSuchPaddingException,
	       javax.crypto.BadPaddingException,
	       java.security.InvalidKeyException,
	       javax.crypto.IllegalBlockSizeException,
	       java.io.IOException
    {
	if ( args.length != 2 ) {
	    System.err.println("dec pubKeyFile inputFile");
	    System.exit(1);
	}

    	int index = 0;
	String pubKeyFile = args[index++];
	String inputFile = args[index++];
	byte[] bytes = Files.readAllBytes(Paths.get(pubKeyFile));
	X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
	KeyFactory kf = KeyFactory.getInstance("RSA");
	PublicKey pub = kf.generatePublic(ks);

	Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	cipher.init(Cipher.DECRYPT_MODE, pub);
	processFile(cipher, inputFile, inputFile + ".ver");
    }

    static private void doEncryptRSAWithAES(String[] args)
	throws java.security.NoSuchAlgorithmException,
	       java.security.InvalidAlgorithmParameterException,
	       java.security.InvalidKeyException,
	       java.security.spec.InvalidKeySpecException,
	       javax.crypto.NoSuchPaddingException,
	       javax.crypto.BadPaddingException,
	       javax.crypto.IllegalBlockSizeException,
	       java.io.IOException
    {
	if ( args.length != 2 ) {
	    System.err.println("enc pvtKeyFile inputFile");
	    System.exit(1);
	}

	int index = 0;
	String pvtKeyFile = args[index++];
	String inputFile = args[index++];
	byte[] bytes = Files.readAllBytes(Paths.get(pvtKeyFile));
	PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
	KeyFactory kf = KeyFactory.getInstance("RSA");
	PrivateKey pvt = kf.generatePrivate(ks);

	KeyGenerator kgen = KeyGenerator.getInstance("AES");
	kgen.init(128);
	SecretKey skey = kgen.generateKey();

	byte[] iv = new byte[128/8];
	srandom.nextBytes(iv);
	IvParameterSpec ivspec = new IvParameterSpec(iv);

	try (FileOutputStream out = new FileOutputStream(inputFile + ".enc")) {
		{
		    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		    cipher.init(Cipher.ENCRYPT_MODE, pvt);
		    byte[] b = cipher.doFinal(skey.getEncoded());
		    out.write(b);
		    System.err.println("AES Key Length: " + b.length);
		}

		out.write(iv);
		System.err.println("IV Length: " + iv.length);

		Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);
		try (FileInputStream in = new FileInputStream(inputFile)) {
			processFile(ci, in, out);
		    }
	    }
    }

    static private void doDecryptRSAWithAES(String[] args)
	throws java.security.NoSuchAlgorithmException,
	       java.security.InvalidAlgorithmParameterException,
	       java.security.InvalidKeyException,
	       java.security.spec.InvalidKeySpecException,
	       javax.crypto.NoSuchPaddingException,
	       javax.crypto.BadPaddingException,
	       javax.crypto.IllegalBlockSizeException,
	       java.io.IOException
    {
	if ( args.length != 2 ) {
	    System.err.println("enc pvtKeyFile inputFile");
	    System.exit(1);
	}

	int index = 0;
	String pubKeyFile = args[index++];
	String inputFile = args[index++];
	byte[] bytes = Files.readAllBytes(Paths.get(pubKeyFile));
	X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
	KeyFactory kf = KeyFactory.getInstance("RSA");
	PublicKey pub = kf.generatePublic(ks);

	try (FileInputStream in = new FileInputStream(inputFile)) {
		SecretKeySpec skey = null;
		{
		    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		    cipher.init(Cipher.DECRYPT_MODE, pub);
		    byte[] b = new byte[256];
		    in.read(b);
		    byte[] keyb = cipher.doFinal(b);
		    skey = new SecretKeySpec(keyb, "AES");
		}

		byte[] iv = new byte[128/8];
		in.read(iv);
		IvParameterSpec ivspec = new IvParameterSpec(iv);

		Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ci.init(Cipher.DECRYPT_MODE, skey, ivspec);

		try (FileOutputStream out = new FileOutputStream(inputFile+".ver")){
			processFile(ci, in, out);
		    }
	    }
    }

    static public void main(String[] args) throws Exception
    {
	if ( args.length == 0 ) {
	    System.err.print("usage: java sample1 command params..\n" +
			     "where commands are:\n" +
			     "  genkey fileBase\n" +
			     "  tnyenc pvtKeyFile inputFile\n" +
			     "  tnydec pubKeyFile inputFile\n" +
			     "  enc pvtKeyFile inputFile\n" +
			     "  dec pubKeyFile inputFile\n");
	    System.exit(1);
	}

	int index = 0;
	String command = args[index++];
	String[] params = Arrays.copyOfRange(args, index, args.length);
	if ( command.equals("genkey") ) doGenkey(params);
	else if ( command.equals("tnyenc") ) doEncrypt(params);
	else if ( command.equals("tnydec") ) doDecrypt(params);
	else if ( command.equals("enc") ) doEncryptRSAWithAES(params);
	else if ( command.equals("dec") ) doDecryptRSAWithAES(params);
	else throw new Exception("Unknown command: " + command);
    }
}
