package RDFDemo;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;

public class test {
    public static void main(String[] args) {
        //some definitions
        String personURI = "http://somewhere/JohnSmith";
        String givenName = "John";
        String familyName = "Smith";
        String fullName = givenName + " " + familyName;

        // an empty model
        Model model = ModelFactory.createDefaultModel();

        // create Resource and add property
        Resource johnSmith = model.createResource(personURI);
        johnSmith.addProperty(VCARD.FN, fullName);
        johnSmith.addProperty(VCARD.N, model.createResource()
                                            .addProperty(VCARD.Given, givenName)
                                            .addProperty(VCARD.Family, familyName));

        // list statements
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();  // get next statement
            Resource  subject   = stmt.getSubject();     // get the subject
            Property  predicate = stmt.getPredicate();   // get the predicate
            RDFNode   object    = stmt.getObject();      // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }

            System.out.println(" .");
        }
    }
}
