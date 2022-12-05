package net.rootdev.javardfa.jena.riot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.jena.riot.Lang;

/**
 * @author Grigor Iliev <grigor@grigoriliev.com>
 */
public class RDFaLang extends Lang {
	public static final RDFaLang HTML = new RDFaLang(
		"HTML", "text/html", Collections.singletonList("html")
	);
	public static final RDFaLang XHTML = new RDFaLang(
		"XHTML", "application/xhtml+xml", Arrays.asList("xhtml", "xht")
	);

	private RDFaLang(String langLabel, String mainContentType, List<String> fileExt) {
		super(
			langLabel,
			mainContentType,
			Collections.emptyList(),
			Collections.emptyList(),
			fileExt
		);
	}
}
