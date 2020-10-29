package services.utils;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

import java.net.URISyntaxException;
import fr.inrialpes.exmo.ontowrap.OntowrapException;
import rita.wordnet.jwnl.JWNLException;
import java.io.FileNotFoundException;
import java.io.IOException;

import services.enums.ErrorCodes;

public class ExceptionHandler extends Exception {

  private static final long serialVersionUID = 1L;

  public static String getErrorMessage(Exception e) {
    if(e instanceof AlignmentException){
      return ErrorCodes.ALIGNMENT_ERROR.getErrorMessage();
    } else if (e instanceof OWLOntologyCreationException){
      return ErrorCodes.OWL_ONTOLOGY_CREATION_ERROR.getErrorMessage();
    } else if (e instanceof URISyntaxException){
      return ErrorCodes.URI_SYNTAX_ERROR.getErrorMessage();
    } else if (e instanceof OntowrapException){
      return ErrorCodes.ONTOWRAP_ERROR.getErrorMessage();
    } else if (e instanceof JWNLException){
      return ErrorCodes.JWNL_ERROR.getErrorMessage();
    } else if (e instanceof FileNotFoundException){
      return ErrorCodes.FILE_NOT_FOUND.getErrorMessage();
    } else if (e instanceof InvalidFormatException){
      return ErrorCodes.INVALID_FORMAT_EXCEPTION.getErrorMessage();
    } else if (e instanceof IOException){
      return ErrorCodes.IO_EXCEPTION.getErrorMessage();
    } else if (e instanceof ParsingException){
      return ErrorCodes.PARSING_EXCEPTION.getErrorMessage();
    }
    return "Unknown server error: " + e.getStackTrace()[0].toString();
  }

}