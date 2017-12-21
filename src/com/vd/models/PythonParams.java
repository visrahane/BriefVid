/**
 *
 */
package com.vd.models;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vis
 *
 */
public class PythonParams {

	private String fileName;

	private List<String> args;

	public PythonParams(String fileName, ArrayList<String> args) {
		this.args = (List<String>) args.clone();
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PythonParams [fileName=").append(fileName).append(", args=").append(args).append("]");
		return builder.toString();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

}
