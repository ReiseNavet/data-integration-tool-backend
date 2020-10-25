package services;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import java.net.URISyntaxException;
import fr.inrialpes.exmo.ontowrap.OntowrapException;
import rita.wordnet.jwnl.JWNLException;
import java.io.FileNotFoundException;
import services.enums.ErrorCodes;

public class ExceptionHandler extends Exception {

  private static final long serialVersionUID = 1L;

  public ExceptionHandler() {};

  public static String getErrorMsg(Exception e) {
    if(e instanceof AlignmentException){
      return ErrorCodes.ALIGNMENT_ERROR.getErrorMsg();
    }else if (e instanceof OWLOntologyCreationException){
      return ErrorCodes.OWL_ONTOLOGY_CREATION_ERROR.getErrorMsg();
    }else if (e instanceof URISyntaxException){
      return ErrorCodes.URI_SYNTAX_ERROR.getErrorMsg();
    }else if (e instanceof OntowrapException){
      return ErrorCodes.ONTOWRAP_ERROR.getErrorMsg();
    }else if (e instanceof JWNLException){
      return ErrorCodes.JWNL_ERROR.getErrorMsg();
    }else if (e instanceof FileNotFoundException){
      return ErrorCodes.FILE_NOT_FOUND.getErrorMsg();
    }else if (e instanceof InvalidFormatException){
      return ErrorCodes.INVALID_FORMAT_EXCEPTION.getErrorMsg();
    }
    return "Unknown server error";
  }

}