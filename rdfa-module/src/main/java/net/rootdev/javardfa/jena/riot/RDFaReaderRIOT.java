/*
 * (c) Copyright 2009 University of Bristol
 * All rights reserved.
 * [See end of file]
 */
package net.rootdev.javardfa.jena.riot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;

import net.rootdev.javardfa.Parser;
import net.rootdev.javardfa.ParserFactory;
import net.rootdev.javardfa.Setting;
import net.rootdev.javardfa.StatementSink;
import net.rootdev.javardfa.uri.IRIResolver;
import net.rootdev.javardfa.uri.URIExtractor11;

import org.apache.jena.atlas.lib.InternalErrorException;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.util.Context;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author Damian Steer <pldms@mac.com>
 * @author Grigor Iliev <grigor@grigoriliev.com>
 */

public class RDFaReaderRIOT implements ReaderRIOT {
    public static class HTMLRDFaReader extends RDFaReaderRIOT {
        @Override public XMLReader getReader() {
            return ParserFactory.createHTML5Reader();
        }

        @Override public void initParser(Parser parser) {
            parser.enable(Setting.ManualNamespaces);
        }
    }

    public static class XHTMLRDFaReader extends RDFaReaderRIOT {
        @Override public XMLReader getReader() throws SAXException {
            return ParserFactory.createNonvalidatingReader();
        }
    }

    static {
        RDFParserRegistry.registerLangTriples(
			RDFaLang.HTML,
			(language, profile) -> {
				if (!RDFaLang.HTML.equals(language)) {
					throw new InternalErrorException("Attempt to parse " + language + " as RDFa");
				}
				return new RDFaReaderRIOT.HTMLRDFaReader();
			}
		);
        RDFParserRegistry.registerLangTriples(
			RDFaLang.XHTML,
			(language, profile) -> {
				if (!RDFaLang.XHTML.equals(language)) {
					throw new InternalErrorException("Attempt to parse " + language + " as RDFa");
				}
				return new RDFaReaderRIOT.XHTMLRDFaReader();
			}
		);
    }

    @Override
    public void read(InputStream in, String baseURI, ContentType ct, StreamRDF output, Context context) {
        read(new InputStreamReader(in), baseURI, ct, output, context);
    }

    @Override
    public void read(Reader reader, String baseURI, ContentType ct, StreamRDF output, Context context) {
        runParser(new InputSource(reader), output, baseURI);
    }

    private XMLReader xmlReader;

    public void setReader(XMLReader reader) { this.xmlReader = reader; }
    public XMLReader getReader() throws SAXException { return xmlReader; }
    public void initParser(Parser parser) { }

    private void runParser(InputSource source, StreamRDF output, String base) {
        StatementSink sink = new RiotStatementSink(output);
        Parser parser = new Parser(
            sink,
            XMLOutputFactory.newInstance(),
            XMLEventFactory.newInstance(),
            new URIExtractor11(new IRIResolver())
        );
        parser.enable(Setting.OnePointOne);
        parser.setBase(base);
        initParser(parser);
        try {
            XMLReader xreader = getReader();
            xreader.setContentHandler(parser);
            xreader.parse(source);
        } catch (IOException ex) {
            throw new RuntimeException("IO Error when parsing", ex);
        } catch (SAXException ex) {
            throw new RuntimeException("SAX Error when parsing", ex);
        }
    }
}

/*
 * (c) Copyright 2009 University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
