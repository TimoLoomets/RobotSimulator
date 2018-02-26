import org.json.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class buildReader {
    private Scanner scanner;

	JSONObject reader2(String file) throws FileNotFoundException, JSONException {
        scanner = new Scanner(new File(file));
		String content = scanner.useDelimiter("\\Z").next();
        return new JSONObject(content);
    }
}