package PlanetaryDefense.PD.wiki;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Shell {

	/** Returns null if it failed for some reason.
     */
    public static ArrayList<String> command(final String cmdline,
    final String directory) {
        try {
            Process process = 
                new ProcessBuilder(new String[] {"bash", "-c", cmdline})
                    .redirectErrorStream(true)
                    .directory(new File(directory))
                    .start();

            ArrayList<String> output = new ArrayList<String>();
            BufferedReader br = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line = null;
            while ( (line = br.readLine()) != null ){
				//System.out.println(line);
				output.add(line);
			}
               

            //There should really be a timeout here.
            if (0 != process.waitFor())
                return null;

            return output;

        } catch (Exception e) {
            //Warning: doing this is no good in high quality applications.
            //Instead, present appropriate error messages to the user.
            //But it's perfectly fine for prototyping.

            return null;
        }
    }

    public static void main(String[] args) {
        changeWikiPwd("/usr/bin/php changePassword.php --user cody --password testtest", "/var/www/html/pdwiki/maintenance");

        /*test("find . -type f -printf '%T@\\\\t%p\\\\n' "
            + "| sort -n | cut -f 2- | "
            + "sed -e 's/ /\\\\\\\\ /g' | xargs ls -halt");*/

    }

    public static void changeWikiPwd(String user, String password) {
        String cmdline = "/usr/bin/php changePassword.php --user " + user + " --password " + password;
		String directory = "/var/www/html/pdwiki/maintenance";
		ArrayList<String> output = command(cmdline, directory);
        if (null == output)
            System.out.println("\n\n\t\tCOMMAND FAILED: " + cmdline);
        else
            for (String line : output)
                System.out.println(line);

    }
}