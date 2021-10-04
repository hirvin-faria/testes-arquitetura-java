package com.dev.hirvinfaria;

import static org.junit.Assert.assertTrue;

import com.dev.hirvinfaria.persistencia.Dao;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
public class FooArchitectTest {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("com.dev.hirvinfaria");

    @Test
    public void verificarDependenciasParaCamadaPersistencia() {
        
        ArchRule rule = classes()
        .that().resideInAPackage("..persistencia..")
        .should().onlyHaveDependentClassesThat().resideInAnyPackage("..persistencia..", "..service..");

        rule.check(importedClasses);
    }

    @Test
    public void verificarDependenciasDaCamadaPersistencia() {
        
        ArchRule rule = noClasses()
        .that().resideInAPackage("..persistencia..")
        .should().dependOnClassesThat().resideInAPackage("..service..");

        rule.check(importedClasses);
    }

    @Test
    public void verificarNomesClassesCamadaPersistencia() {
        
        ArchRule rule = classes()
        .that().haveSimpleNameEndingWith("Dao")
        .should().resideInAPackage("..persistencia..");

        rule.check(importedClasses);
    }

    @Test
    public void verificarImplementacaoInterfaceDao() {
        
        ArchRule rule = classes()
        .that().implement(Dao.class)
        .should().haveSimpleNameEndingWith("Dao");

        rule.check(importedClasses);
    }

    @Test
    public void verificarDependenciasCiclicas() {
        
        ArchRule rule = slices()
        .matching("com.dev.hirvinfaria.{*}..").should().beFreeOfCycles();

        rule.check(importedClasses);
    }

    @Test
    public void verificarViolacaoCamadas() {
        
        ArchRule rule = layeredArchitecture()
        .layer("Service").definedBy("..service..")
        .layer("Persistencia").definedBy("..persistencia..")

        .whereLayer("Persistencia").mayOnlyBeAccessedByLayers("Service");

        rule.check(importedClasses);
    }

}
