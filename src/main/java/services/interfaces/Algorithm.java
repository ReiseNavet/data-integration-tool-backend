package services.interfaces;

import fr.inrialpes.exmo.align.impl.URIAlignment;

public interface Algorithm {
    public URIAlignment run(Object onto1, Object onto2);
}
