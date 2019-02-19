package de.trawizardsofjava.messenger.data;

import de.trawizardsofjava.messenger.model.Nachricht;
import org.springframework.data.repository.CrudRepository;

public interface NachrichtenRepo extends CrudRepository<Nachricht, Long> {

}
