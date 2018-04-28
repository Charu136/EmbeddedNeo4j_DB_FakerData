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

	// Types of Labels
	public static enum LabelTypes implements Label {
		USER, LOCATION, COMPANY, JOINING_DATE

	}

	// Types of Relationships
	public static enum RelTypes implements RelationshipType {
		HAS_EMPLOYED, CREATED_ACCOUNT_ON, LIVES_AT
	}

	ArrayList<Node> user_list = new ArrayList<>();
	ArrayList<Node> company_list = new ArrayList<>();
	ArrayList<Node> location_list = new ArrayList<>();
	ArrayList<Node> joining_date_list = new ArrayList<>();

	//method to populate data using Faker and neo4j
	void insertData(GraphDatabaseService graphDb1) {
		graphDb = graphDb1;
		graphDb.beginTx();
		Faker faker = new Faker();

		int total_names = 10000;
		int total_addresses = 10000;
		int total_companies = 10000;
		int total_dates = 10000;

		for (int i = 0; i < total_names; i++) {

			// nodes created with Label type USER and properties FIRSTNAME and LASTNAME
			Node newNode = graphDb.createNode();
			newNode.addLabel(LabelTypes.USER);
			newNode.setProperty("firstName", faker.name().firstName());
			newNode.setProperty("lastName", faker.name().lastName());

		}

		for (int i = 0; i < total_companies; i++) {

			// // nodes created with Label type COMPANY and properties COMPANY NAME, LOGO and INDUSTRY
			Node newNode = graphDb.createNode();
			newNode.addLabel(LabelTypes.COMPANY);
			newNode.setProperty("companyName", faker.company().name());
			newNode.setProperty("logo", faker.company().logo());
			newNode.setProperty("industry", faker.company().industry());
		}

		for (int i = 0; i < total_addresses; i++) {

			// // nodes created with Label type LOCATION and properties CITY, STATE and STREET ADDRESS
			Node newNode = graphDb.createNode();
			newNode.addLabel(LabelTypes.LOCATION);
			newNode.setProperty("city", faker.address().city());
			newNode.setProperty("state", faker.address().state());
			newNode.setProperty("country", faker.address().country());
			newNode.setProperty("streetAddress", faker.address().streetAddress());

		}

		for (int i = 0; i < total_dates; i++) {

			// // nodes created with Label type JOINING DATE and properties PAST
			Node newNode = graphDb.createNode();
			newNode.addLabel(LabelTypes.JOINING_DATE);
			newNode.setProperty("past", faker.date().past(3650, TimeUnit.DAYS).toString());

		}

		// iterate over nodes to establish relationships among them
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
	
	//query to get list of users living at certain locations
	public void getUsersLocations(final Node person) {
		graphDb.beginTx();
		String output = "";
		TraversalDescription td = graphDb.traversalDescription().breadthFirst()
				.relationships(RelTypes.LIVES_AT, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition());

		for (Path userPath : td.traverse(person)) {
			System.out.println(userPath);
		}
		System.out.println("----------------------------------------------------------------------------------------------------------------------------");
	}

	//query to get users who work at different companies
	public void getCompanyUsers(final Node person) {
		graphDb.beginTx();
		String output = "";
		TraversalDescription td = graphDb.traversalDescription().breadthFirst()
				.relationships(RelTypes.HAS_EMPLOYED, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition());

		for (Path companyPath : td.traverse(person)) {
			System.out.println(companyPath);
		}
		System.out.println("----------------------------------------------------------------------------------------------------------------------------");
	}

	//query to fetch the users who have joined in the near past of 10 years
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

}
