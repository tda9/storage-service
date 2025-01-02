package org.example.daiam.infrastruture.domainrepository;

import java.util.UUID;

public interface DomainRepository<D> {
    D save (D domain);

    D getById(UUID id);

}
