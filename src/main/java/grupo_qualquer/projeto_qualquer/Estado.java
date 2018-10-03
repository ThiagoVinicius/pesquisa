package grupo_qualquer.projeto_qualquer;

import java.util.List;
import java.util.stream.Collectors;

public class Estado {

    String sigla;
    List<Municipio> municipios;

    public List<Bairro> listaBairros() {
        return municipios.stream().flatMap(m -> m.bairros.stream()).collect(Collectors.toList());
    }
    
}
