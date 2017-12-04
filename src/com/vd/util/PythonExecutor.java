/**
 *
 */
package com.vd.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vd.models.PythonParams;

/**
 * @author Vis
 *
 */
public class PythonExecutor {

	private static final String PYTHON_EXE_LOCATION = "C:/Users/visha/AppData/Local/Programs/Python/Python36-32/python";

	/**
	 * @param args
	 */

	public static void executePython(PythonParams params) {
		List<String> commandList = new ArrayList<>(Arrays.asList(PYTHON_EXE_LOCATION, params.getFileName()));
		commandList.addAll(params.getArgs());
		ProcessBuilder pb = new ProcessBuilder(commandList);
		try {
			Process p = pb.start();
		} catch (IOException e) {
			System.out.println("Unable to start python process");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
