import java.io.File;

public class FileWithDeleteCharacter {
	public static void main(String[] args) {
		System.out.println("images/checks/620.jpg" + (char)12 + ".jpg");
		for ( int i = 0; i <= Character.MAX_VALUE; i++ ) {
			File f = new File("images/checks/620.jpg" + (char)i + (char)i + (char)i + (char)i + ".jpg");
			if ( f.exists() ) {
				System.out.println(i);
			}
		}
	}
}
