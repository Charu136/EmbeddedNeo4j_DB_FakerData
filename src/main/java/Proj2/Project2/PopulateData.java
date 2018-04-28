package Proj2.Project2;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.helpers.collection.Iterators;

import Proj2.Project2.EmbeddedNeo4j;
import org.neo4j.graphdb.Label;
import com.github.javafaker.Faker;

public class PopulateData {

	GraphDatabaseService graphDb;

	public static enum LabelTypes implements Label {
		USER, LOCATION, COMPANY, JOINING_DATE

	}

	// START SNIPPET: createReltype
	public static enum RelTypes implements RelationshipType {
		HAS_EMPLOYED, CREATED_ACCOUNT_ON, LIVES_AT
	}

	// END SNIPPET: createReltype
	ArrayList<Node> user_list = new ArrayList<>();
	ArrayList<Node> company_list = new ArrayList<>();
	ArrayList<Node> location_list = new ArrayList<>();
	ArrayList<Node> joining_date_list = new ArrayList<>();

	void insertData(GraphDatabaseService graphDb1) {
		graphDb = graphDb1;
		graphDb.beginTx();
		Faker faker = new Faker();

		int total_names = 1000;
		int total_addresses = 1000;
		int total_companies = 1000;
		int total_dates = 1000;

		for (int i = 0; i < total_names; i++) {

			// Database operations go here
			// END SNIPPET: transaction
			// START SNIPPET: addData
			Node newNode = graphDb.createNode();
			newNode.addLabel(LabelTypes.USER);
			newNode.setProperty("firstName", faker.name().firstName());
			newNode.setProperty("lastName", faker.name().lastName());

		}

		for (int i = 0; i < total_companies; i++) {

			// Database operations go here
			// END SNIPPET: transaction
			// START SNIPPET: addData
			Node newNode = graphDb.createNode();
			newNode.addLabel(LabelTypes.COMPANY);
			newNode.setProperty("companyName", faker.company().name());
			newNode.setProperty("logo", faker.company().logo());
			newNode.setProperty("industry", faker.company().industry());
			// newNode.setProperty("profession", faker.company().profession());

		}

		for (int i = 0; i < total_addresses; i++) {

			// Database operations go here
			// END SNIPPET: transaction
			// START SNIPPET: addData
			Node newNode = graphDb.createNode();
			newNode.addLabel(LabelTypes.LOCATION);
			newNode.setProperty("city", faker.address().city());
			newNode.setProperty("state", faker.address().state());
			newNode.setProperty("country", faker.address().country());
			newNode.setProperty("streetAddress", faker.address().streetAddress());

		}

		for (int i = 0; i < total_dates; i++) {

			// Database operations go here
			// END SNIPPET: transaction
			// START SNIPPET: addData
			Node newNode = graphDb.createNode();
			newNode.addLabel(LabelTypes.JOINING_DATE);
			newNode.setProperty("past", faker.date().past(3650, TimeUnit.DAYS).toString());

		}

		try (ResourceIterator<Node> users = graphDb.findNodes(LabelTypes.USER)) {
			ResourceIterator<Node> companies = graphDb.findNodes(LabelTypes.COMPANY);

			ResourceIterator<Node> locations = graphDb.findNodes(LabelTypes.LOCATION);

			ResourceIterator<Node> dates = graphDb.findNodes(LabelTypes.JOINING_DATE);
			while (users.hasNext()) {
				user_list.add(users.next());
			}
			while (companies.hasNext()) {
				company_list.add(companies.next());
			}
			/*
			 * int k = 0; for (int i = 0; i < company_list.size(); i++) { for (int j = k; i
			 * <20; j++) { company_list.get(i).createRelationshipTo(user_list.get(j),
			 * RelTypes.HAS_EMPLOYED); } k=k+20;
			 * 
			 * }
			 */
			while (locations.hasNext()) {
				location_list.add(locations.next());
			}

			while (dates.hasNext()) {
				joining_date_list.add(dates.next());
			}

			for (int i = 0; i < company_list.size(); i++) {
				company_list.get(i).createRelationshipTo(user_list.get(i), RelTypes.HAS_EMPLOYED);
			}

			for (int i = 0; i < user_list.size(); i++) {
				user_list.get(i).createRelationshipTo(location_list.get(i), RelTypes.LIVES_AT);
			}

			for (int i = 0; i < user_list.size(); i++) {
				user_list.get(i).createRelationshipTo(joining_date_list.get(i), RelTypes.CREATED_ACCOUNT_ON);
			}

		}

	}
	
	
	String toString(Node n, String prop) {
		return "("+n.getProperty(prop)+")";
		}
	
	
	String toString(Relationship r) {
		return "-["+r.getType().name()+"]->"; 
		}
	

	public void getUsersLocations(final Node person) {
		graphDb.beginTx();
		String output = "";
		TraversalDescription td = graphDb.traversalDescription().breadthFirst()
				.relationships(RelTypes.LIVES_AT, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition());

		for (Path userPath : td.traverse(person)) {
			output=render(userPath, UserProperties.FIRSTNAME);
		}
		
	
		
		
		System.out.println("----------------------------------------------------------------------------------------------------------------------------");
	}

	public String render(Path path, String prop) {
		StringBuilder sb = new StringBuilder();
		for (PropertyContainer pc : path) {
			if (pc instanceof Node)
				sb.append(toString((Node) pc, prop));
			else
				sb.append(toString((Relationship) pc));
		}
		return sb.toString();
	}

	public void getCompanyUsers(final Node person) {
		graphDb.beginTx();
		String output = "";
		TraversalDescription td = graphDb.traversalDescription().breadthFirst()
				.relationships(RelTypes.HAS_EMPLOYED, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition());

		for (Path companyPath : td.traverse(person)) {
			System.out.println(companyPath);
		}
		System.out.println(
				"----------------------------------------------------------------------------------------------------------------------------");
	}

	public void getJoiningDatesOfUsers(final Node person) {
		graphDb.beginTx();
		String output = "";
		TraversalDescription td = graphDb.traversalDescription().breadthFirst()
				.relationships(RelTypes.CREATED_ACCOUNT_ON, Direction.OUTGOING)
				.evaluator(Evaluators.excludeStartPosition());

		for (Path joindatePath : td.traverse(person)) {
			System.out.println(joindatePath);
		}

	}

	/*
	 * public void findEmployeeList( final Node startNode ) {
	 * System.out.println(""); graphDb.beginTx(); TraversalDescription td =
	 * graphDb.traversalDescription() .breadthFirst() .relationships(
	 * RelTypes.HAS_EMPLOYED, Direction.OUTGOING ) .relationships(
	 * RelTypes.LIVES_AT, Direction.OUTGOING ) .evaluator(
	 * Evaluators.includeWhereLastRelationshipTypeIs( RelTypes.LIVES_AT ) ); String
	 * output=""; int numberOfHackers=0; for ( Path friendPath :
	 * td.traverse(startNode) ) { output += "At depth " + friendPath.length() +
	 * " => " + friendPath.endNode() .getProperty( "name" ) + "\n";
	 * numberOfHackers++; } output += "Number of hackers found: " + numberOfHackers
	 * + "\n"; }
	 */

}
