package Proj2.Project2;

/*
 * Licensed to Neo Technology under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.io.fs.FileUtils;

import Proj2.Project2.PopulateData.LabelTypes;

public class EmbeddedNeo4j {
	File databaseDirectory = new File("neo4j-hello-db");

	GraphDatabaseService graphDb;

	public static void main(final String[] args) throws IOException {
		EmbeddedNeo4j hello = new EmbeddedNeo4j();
		hello.createDb();

	}

	void createDb() throws IOException {

		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);
		PopulateData pd = new PopulateData();
		pd.insertData(graphDb);
		ResourceIterator<Node> users = graphDb.findNodes(PopulateData.LabelTypes.USER);
		ResourceIterator<Node> companies = graphDb.findNodes(PopulateData.LabelTypes.COMPANY);
		while (users.hasNext()) {
			pd.getUsersLocations(users.next());
			
		}

		
		 while(companies.hasNext()) { // pd.getCompanyUsers(companies.next());
			 pd.getCompanyUsers(companies.next());
		 }
		
	}

}