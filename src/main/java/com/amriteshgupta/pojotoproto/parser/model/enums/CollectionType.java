package com.amriteshgupta.pojotoproto.parser.model.enums;

import java.util.*;

public enum CollectionType {

    ITERABLE(Iterable.class),
    COLLECTION(Collection.class),
    ABSTRACT_COLLECTION(AbstractCollection.class),
    LIST(List.class),
    ABSTRACT_LIST(AbstractList.class),
    SET(Set.class),
    ABSTRACT_SET(AbstractSet.class);

    private final Class<?> clazz;

    CollectionType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public static List<Class> getCollectionTypes() {
        List<Class> collectionTypes = new ArrayList<>();
        for (CollectionType collectionType : CollectionType.values()) {
            collectionTypes.add(collectionType.getClazz());
        }
        return collectionTypes;
    }
}
