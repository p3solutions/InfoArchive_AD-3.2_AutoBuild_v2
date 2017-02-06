package com.emc.internal.utils;

import com.emc.dds.xmlarchiving.client.p3.util.SecurityUtil;

public class InstanceConstants {

	private static SecurityUtil sec;
	private static boolean valid = false;
	
	public static SecurityUtil getSec() {
		return sec;
	}

	public static void setSec(SecurityUtil sec) {
		InstanceConstants.sec = sec;
	}

	public static boolean isValid() {
		return valid;
	}

	public static void setValid(boolean valid) {
		InstanceConstants.valid = valid;
	}

}
