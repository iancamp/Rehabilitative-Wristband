package Test;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import client.NetworkThread;

public class NetworkThreadTest {

	@Test
	public void test() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		NetworkThread net = new NetworkThread();
		Class<?>[] temp = new Class<?>[2]; //2 arguments
		String t = new String();
		temp[0] = t.getClass();
		temp[1] = t.getClass();
		Method method = net.getClass().getDeclaredMethod("parseAndCrop", temp);
		method.setAccessible(true);
		float s = (Float)method.invoke(net, new String("as123"), new String("as"));
		assertTrue(Math.abs(s - 123) < 0.0001);
		
		
	
		
		
	}

}
