package com.photofall.rest.security;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class SecurityHandler {
	private static Random rand = new Random((new Date()).getTime());
	
	public static String encrypt(String str){
		BASE64Encoder encoder = new BASE64Encoder();
		byte[] salt = new byte[8];
		rand.nextBytes(salt);
		return encoder.encode(salt) + encoder.encode(str.getBytes());
	}
	
	public static String decrypt(String encStr){
		if (encStr.length() > 12) {
			String cipher = encStr.substring(12);
			BASE64Decoder decoder = new BASE64Decoder();
			try {
			    return new String(decoder.decodeBuffer(cipher));
			} catch (IOException e) {
				return "failed to decrypt";
			}
		}
		else{
			return "encrypted pass is less that 13 length";
		}
	}
	
}
