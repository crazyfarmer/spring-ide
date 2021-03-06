package org.springframework.ide.eclipse.gettingstarted.dashboard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.springframework.ide.eclipse.gettingstarted.GettingStartedActivator;
import org.springframework.ide.eclipse.gettingstarted.content.GettingStartedContent;
import org.springframework.ide.eclipse.gettingstarted.guides.GettingStartedGuide;
import org.springframework.web.util.HtmlUtils;

public class GeneratedGuidesDashboardPage extends GuidesDashboardPage {
	
	File dashHtml;
	
	public GeneratedGuidesDashboardPage() throws URISyntaxException, IOException {
		URL entry = GettingStartedActivator.getDefault().getBundle().getEntry("resources/dashboard.html");
		dashHtml = new File(FileLocator.toFileURL(entry).toURI());
		//generateHtml();
		setUrl(dashHtml.toURI().toString());
		setUseJavaScript(true);
	}
	
	private void generateHtml() throws URISyntaxException, IOException {
		GettingStartedGuide[] guides = GettingStartedContent.getInstance().getGuides();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dashHtml));
		try {
			preamble(out);
			for (GettingStartedGuide g : guides) {
				
				out.write(
						"     <li><a href=\""+g.getHomePage()+"\">"+g.getDisplayName()+"</a><br>\n"+
					    "         "+HtmlUtils.htmlEscape(describe(g)) + "\n"+
					    "         <a class=\"gs-guide-import\" href=\""+g.getHomePage()+"\">Import</a>\n" +
					    "     </li>\n"
				);
				
			}
			postamble(out);
		} finally {
			out.close();
		}
	}


	private String describe(GettingStartedGuide g) {
		String desc = g.getDescription();
		if (desc==null || "".equals(desc.trim())) {
			return "<no description>";
		}
		return desc;
	}

	private void postamble(BufferedWriter out) throws IOException {
		out.write("	</ul>   \n" + 
				"</body>\n" + 
				"<!-- ------------------------------------------------------------------------------- -->\n" + 
				"</html>");
	}


	private void preamble(BufferedWriter out) throws IOException {
		out.write("<!DOCTYPE html>\n" + 
				"<html>\n" + 
				"<!-- ------------------------------------------------------------------------------- -->\n" + 
				"<head>\n" + 
				"   <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n" + 
				"   <meta charset=\"utf-8\">\n" + 
				"   <title>Getting Started With Spring</title>\n" + 
				"    \n" + 
				"   \n" + 
				"   <script src=\"scripts/jquery.js\"></script>\n" + 
				"   <script src=\"scripts/sts-download-buttons.js\"></script>\n" + 
				"</head>   \n" + 
				"<!-- ------------------------------------------------------------------------------- -->\n" + 
				"<body>\n" +
				"  <ul>\n");
	}
	
}
