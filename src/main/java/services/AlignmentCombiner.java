package services;

import fr.inrialpes.exmo.align.impl.URIAlignment;

public class AlignmentCombiner {
    public URIAlignment combine (URIAlignment[] results){
        if(results.length > 0) {
            return results[0];
        }
        return null;
    }
}
