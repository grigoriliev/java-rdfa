/*
 * (c) Copyright 2009 University of Bristol
 * All rights reserved.
 * [See end of file]
 */
package net.rootdev.javardfa.jena.riot;

import net.rootdev.javardfa.StatementSink;

import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.system.StreamRDF;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Damian Steer <pldms@mac.com>
 * @author Grigor Iliev <grigor@grigoriliev.com>
 */
public class RiotStatementSink implements StatementSink {
	private final StreamRDF outputStreamRDF;
	private Map<String, Resource> bnodeLookup;

	public RiotStatementSink(StreamRDF output) {
		outputStreamRDF = output;
	}

	@Override public void setBase(String base) {
		outputStreamRDF.base(base);
	}

	@Override
	public void start() {
		bnodeLookup = new HashMap<>();
		outputStreamRDF.start();
	}

	@Override
	public void end() {
		outputStreamRDF.finish();
		bnodeLookup = null;
	}

	@Override
	public void addObject(String subject, String predicate, String object) {
		outputStreamRDF.triple(
			Triple.create(
				getResource(subject).asNode(),
				ResourceFactory.createProperty(predicate).asNode(),
				getResource(object).asNode()
			)
		);
	}

	@Override
	public void addLiteral(String subject, String predicate, String lex, String lang, String datatype) {
		final Literal literal = datatype != null ?
			ResourceFactory.createTypedLiteral(
				lex, TypeMapper.getInstance().getSafeTypeByName(datatype)
			) : lang != null ? ResourceFactory.createLangLiteral(lex, lang) :
			ResourceFactory.createPlainLiteral(lex);
		outputStreamRDF.triple(
			Triple.create(
				getResource(subject).asNode(),
				ResourceFactory.createProperty(predicate).asNode(),
				literal.asNode()
			)
		);
	}

	@Override
	public void addPrefix(String prefix, String uri) {
		outputStreamRDF.prefix(prefix, uri);
	}

	private Resource getResource(String res) {
		if (res.startsWith("_:")) {
			if (bnodeLookup.containsKey(res)) {
				return bnodeLookup.get(res);
			}
			final Resource bnode = ResourceFactory.createResource(res);
			bnodeLookup.put(res, bnode);
			return bnode;
		} else {
			return ResourceFactory.createResource(res);
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

