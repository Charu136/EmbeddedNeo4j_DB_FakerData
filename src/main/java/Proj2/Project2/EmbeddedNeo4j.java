package Proj2.Project2;

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

	//creates neo4j database 
	void createDb() throws IOException {

		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);
		PopulateData pd = new PopulateData();
		//populates database by calling this method
		pd.insertData(graphDb);
		
		//iterates over database and execute queries
		ResourceIterator<Node> users = graphDb.findNodes(PopulateData.LabelTypes.USER);
		ResourceIterator<Node> companies = graphDb.findNodes(PopulateData.LabelTypes.COMPANY);
		ResourceIterator<Node> join_dates = graphDb.findNodes(PopulateData.LabelTypes.JOINING_DATE);
		while (users.hasNext()) {
			pd.getUsersLocations(users.next());
			
		}
		while(companies.hasNext()) { 
			 pd.getCompanyUsers(companies.next());
		 }
		while(join_dates.hasNext()) { 
			 pd.getJoiningDatesOfUser(join_dates.next());
		 }
		
	}

}
