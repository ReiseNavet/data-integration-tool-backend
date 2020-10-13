package services.parsers;

import org.semanticweb.owl.align.Cell;

public class CellParser {
    public static String getTarget(Cell cell){
        return cell.getObject1().toString().split("#")[1];
    }
    public static String getSource(Cell cell){
        return cell.getObject2().toString().split("#")[1];
    }
    public static String getRelation(Cell cell){
        String relation = cell.getRelation().getRelation();
        return relation.equals("&lt;") ? "<" : relation;
    }
    public static String getConfidence(Cell cell){
        return String.format("%.2f", cell.getStrength());  
    }
}
