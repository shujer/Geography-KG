package com.sysu.maven.campus;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import static org.neo4j.driver.v1.Values.parameters;

public class HelloWorldExample implements AutoCloseable
{
    private final Driver driver;

    public HelloWorldExample( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public void printGreeting( final String message )
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                	tx.run( "LOAD CSV WITH HEADERS FROM \"file:///zone.csv\" AS row " +
                            "CREATE (n:Zone) " +
                			"SET n = row");
                    tx.run("LOAD CSV WITH HEADERS FROM \"file:///campus.csv\" AS row "+
                			"CREATE (n:Campus) " +
                    		"SET n = row");
                    tx.run("LOAD CSV WITH HEADERS FROM \"file:///faculty.csv\" AS row "+
                    		"CREATE (n:Faculty) "+
                    		"SET n = row");
                    tx.run("MATCH (z:Zone),(c:Campus) " + 
                    		"WHERE z.zoneID = c.zoneID " + 
                    		"CREATE (z)-[:contains]->(c)");
                    tx.run("MATCH (c:Campus),(f:Faculty) " + 
                    		"WHERE c.campusID = f.campusID " + 
                    		"CREATE (c)-[:contains]->(f)");
                    return message;
                }
            } );
            System.out.println( greeting );
        }
    }

    public static void main( String... args ) throws Exception
    {
        try ( HelloWorldExample greeter = new HelloWorldExample( "bolt://127.0.0.1:7687", "neo4j", "sysu" ) )
        {
            greeter.printGreeting( "hello, world" );
        }
    }
}