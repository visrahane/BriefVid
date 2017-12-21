import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PythonExe {

	public static void main(String[] args) throws IOException {
		// Process p = Runtime.getRuntime().exec("python C:/Users/visha/PycharmProjects/PyramidBlending/pyramidBlend.py");
		//Process p = Runtime.getRuntime().exec("python testrhello.py ");
		ProcessBuilder pb = new ProcessBuilder("C:/Users/visha/AppData/Local/Programs/Python/Python36-32/python",
				"C:/Users/visha/PycharmProjects/PyramidBlending/stitch.py", "1.jpg", "4.jpg");

		List<String> commandList = new ArrayList<>(
				Arrays.asList("C:/Users/visha/AppData/Local/Programs/Python/Python36-32/python",
						"C:/Users/visha/PycharmProjects/PyramidBlending/stitch.py", "4.jpg", "5.jpg"));
		ProcessBuilder pb2 = new ProcessBuilder(commandList);
		Process p = pb2.start();
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String str;
		while (((str = in.readLine()) != null)) {
			System.out.println(str);
		}
	}

}
