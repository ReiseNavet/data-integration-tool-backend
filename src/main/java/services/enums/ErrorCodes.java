package services.enums;

public enum ErrorCodes {

  /*
  Error codes with corresponding messages. Codes ranges for the different error types.
  Parsing errors: 100-199
  Alignment errors: 200-299
  IOErrors: 300-399
  */

  ALIGNMENT_ERROR("200", "Alignment Exception"),
  OWL_ONTOLOGY_CREATION_ERROR("201", "OWL Ontology Creation Exception"),
  URI_SYNTAX_ERROR("202", "URI Syntax Exception"),
  ONTOWRAP_ERROR("203", "Ontowrap Exception"),
  JWNL_ERROR("204", "Wordnet Exception"),
  FILE_NOT_FOUND("300", "File Not Found Exception"),
  IO_EXCEPTION("301", "IOException");

  private final String code;
  private final String msg;

  private ErrorCodes(String code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public String getErrorCode() {
    return code;
  }

  public String getErrorMsg() {
    if(Integer.valueOf(code)<300 & Integer.valueOf(code)>=200){
      return "Error code " + code + ", " + msg + ": Your input files could not be aligned.";
    }else if(Integer.valueOf(code)<400 & Integer.valueOf(code)>=300){
      return "Error code " + code + ", " + msg + ": Unknown file-related error. Check your input files.";
    }
      return "Error code " + code + ", " + msg;
  }
}