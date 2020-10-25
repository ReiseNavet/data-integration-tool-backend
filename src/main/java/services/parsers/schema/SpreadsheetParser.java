package services.parsers.schema;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import services.dataclasses.OntologyConcept;
import services.interfaces.SchemaParser;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpreadsheetParser implements SchemaParser {

    @Override
    public List<OntologyConcept> parse(String filePath) throws Exception {
        List<OntologyConcept> ontologyList = new ArrayList<OntologyConcept>();
        File file = new File(filePath);
        FileInputStream excelFile = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(excelFile);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = datatypeSheet.iterator();
    
        //Setting index of element that will have subclasses
        String lastElement = null;
    
        while (iterator.hasNext()) {
          Row currentRow = iterator.next();
          //Skipping first row which contains headers
          if (currentRow.getRowNum() == 0){
            continue;
          }
          OntologyConcept ontologyConcept = new OntologyConcept(); //Change object name after merge to dev
          Iterator<Cell> cellIterator = currentRow.iterator();
          
          while (cellIterator.hasNext()) {
            Cell currentCell = cellIterator.next();
            if (currentCell.getColumnIndex() == 0){
              lastElement = currentCell.getStringCellValue();
              ontologyConcept.name = lastElement;
            }
            if (currentCell.getColumnIndex() == 1 & lastElement != null){
              ontologyConcept.name = currentCell.getStringCellValue();
              ontologyConcept.subClassof = lastElement;
            }
            if (currentCell.getColumnIndex() == 2 ){
              ontologyConcept.description = currentCell.getStringCellValue();
            }
          }
          ontologyList.add(ontologyConcept);
        }
        workbook.close();
        
        return ontologyList;
    }
    
}
