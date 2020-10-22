package services.IO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OWLOntologyToFile {
    public static File Convert(OWLOntology owlOntology, String baseSaveLocation) throws Exception{
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OutputStream os = new FileOutputStream(baseSaveLocation);
        manager.saveOntology(owlOntology, os);
        os.close();
        return new File(baseSaveLocation);
    }
}
