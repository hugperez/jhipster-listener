package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityAAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntityAAllPropertiesEquals(EntityA expected, EntityA actual) {
        assertEntityAAutoGeneratedPropertiesEquals(expected, actual);
        assertEntityAAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntityAAllUpdatablePropertiesEquals(EntityA expected, EntityA actual) {
        assertEntityAUpdatableFieldsEquals(expected, actual);
        assertEntityAUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntityAAutoGeneratedPropertiesEquals(EntityA expected, EntityA actual) {
        assertThat(expected)
            .as("Verify EntityA auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntityAUpdatableFieldsEquals(EntityA expected, EntityA actual) {
        assertThat(expected)
            .as("Verify EntityA relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()))
            .satisfies(e -> assertThat(e.getTitle()).as("check title").isEqualTo(actual.getTitle()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntityAUpdatableRelationshipsEquals(EntityA expected, EntityA actual) {
        // empty method
    }
}
