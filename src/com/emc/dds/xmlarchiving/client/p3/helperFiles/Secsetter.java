package com.emc.dds.xmlarchiving.client.p3.helperFiles;

public class Secsetter {

	public static byte[] setByte() {
		String mainkey = CONSTANTS.mainKEY;
		
		int mainKEY = getMainKey(CONSTANTS.mainkey0,CONSTANTS.mainkey1, mainkey);
		byte[] bytes = new byte[Integer.parseInt(CONSTANTS.mainkey2.substring(mainkey.length()))];
		int x = (bytes.length / 2) - 1;
		int j = mainKEY;
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(CONSTANTS.keys[j]);
			if(i==x); else if(i>=x) j--; else j++;
		}
		return bytes;		
		
	}
	
	private static int getMainKey(String mainkey0, String mainkey1, String mainkey) {
		int key0 = Integer.parseInt(mainkey0.substring(mainkey.length()));
		int key1 = Integer.parseInt(mainkey1.substring(mainkey.length()));
		return (key0/key1);
	}

}
