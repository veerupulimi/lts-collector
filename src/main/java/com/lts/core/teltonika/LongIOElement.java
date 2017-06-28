package com.lts.core.teltonika;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Ernestas Vaiciukevicius (ernestas.vaiciukevicius@teltonika.lt)
 * 
 *         <p>
 *         IOElement with support of "long" (8 byte) property values.
 *         </p>
 */
public class LongIOElement extends IOElement {

	private Hashtable<Integer, Long> longProperties = new Hashtable<Integer, Long>();

	public long[] getLongProperty(int id) {
		Long longValue = (Long) longProperties.get(Integer.valueOf(id));
		long[] ret = null;

		if (longValue != null) {
			ret = new long[] { id, longValue.longValue() };
		} else {
			int[] intRet = super.getProperty(id);
			if (intRet != null) {
				ret = new long[] { intRet[0], intRet[1] };
			}
		}

		return ret;
	}

	public void addLongProperty(long[] prop) {
		if (prop[0] < Integer.MIN_VALUE || prop[0] > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Wrong id value:" + prop[0]);
		}

		longProperties.put(Integer.valueOf((int) prop[0]), Long
				.valueOf(prop[1]));
	}

	public void removeLongProperty(int id) {
		longProperties.remove(Integer.valueOf(id));
	}

	public int[] getAvailableLongProperties() {
		int[] intRet = super.getAvailableProperties();
		int[] ret = null;

		if (intRet == null) {
			intRet = new int[0];
		}

		// Iterating to see the values of HashMap
		System.out.println("hashtable iteration starts");
		int str;
		Set<Integer> set = longProperties.keySet();
		Iterator<Integer> itr = set.iterator();

		while (itr.hasNext()) {
			str = itr.next();
			System.out.println(str + ": " + longProperties.get(str));
		}
		System.out.println("hashtable iteration ends");

		try {
			synchronized (longProperties) {

				try {
					ret = new int[intRet.length + longProperties.size()];
					System.out.println("ret size=" + ret.length);
					System.arraycopy(intRet, 0, ret, 0, intRet.length);

					int ind = intRet.length;
					Enumeration<Integer> longPropertyEnumeration = longProperties.keys();
					while (longPropertyEnumeration.hasMoreElements()) {
						Integer key = (Integer) longPropertyEnumeration
								.nextElement();
						System.out.println("key=" + key);
						ret[ind++] = key.intValue();
					}
				} catch (Exception e) {
					System.out.println("inner::" + e);
				}

			}
		} catch (Exception e) {
			System.out.println("outer::" + e);
		}

		return ret;
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();

		for (Integer id : longProperties.keySet()) {
			stringBuffer.append("[" + id + "=" + longProperties.get(id) + "] ");
		}

		return super.toString() + stringBuffer.toString();
	}

	public boolean equals(Object arg0) {
		if (arg0 instanceof LongIOElement) {
			if (!super.equals(arg0)) {
				return false;
			}
			return longProperties.equals(((LongIOElement) arg0).longProperties);
		} else {
			if (longProperties.size() == 0) {
				return super.equals(arg0);
			} else {
				return false;
			}
		}
	}

}
